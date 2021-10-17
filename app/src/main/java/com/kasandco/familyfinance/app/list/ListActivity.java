package com.kasandco.familyfinance.app.list;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.BaseActivity;
import com.kasandco.familyfinance.app.finance.fragments.FragmentCreateItemHistory;
import com.kasandco.familyfinance.app.item.ItemActivity;
import com.kasandco.familyfinance.app.list.createEditList.FragmentCreateList;
import com.kasandco.familyfinance.app.list.createEditList.FragmentEditList;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.KeyboardUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends BaseActivity implements Constants, ListContract, FragmentCreateList.CreateListListener, ListRvAdapter.ListAdapterListener, FragmentCreateItemHistory.ClickListener {
    @Inject
    ListPresenter presenter;
    @Inject
    FragmentCreateList fragmentCreateList;

    @Inject
    FragmentEditList fragmentEditList;

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    TextView emptyText;
    @BindView(R.id.activity_list_toolbar)
    Toolbar toolbar;

    @Inject
    FragmentCreateItemHistory fragmentCreateItemHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        App.getListActivityComponent(this).inject(this);
        refreshLayout = findViewById(R.id.activity_list_swipe_container);
        recyclerView = findViewById(R.id.activity_list_rv_items);
        emptyText = findViewById(R.id.activity_list_text_empty);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        MaterialTextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.title_list);
        toolbar.findViewById(R.id.toolbar_menu).setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
        refreshLayout.setOnRefreshListener(refreshListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        if(activityClass!=getClass()) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        }else{
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void itemOnClick(ListRvAdapter.ViewHolder holder) {
        presenter.clickItem(holder);
    }

    @Override
    public void showLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void addAdapter(ListRvAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(positionStart);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(positionStart);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(positionStart);
            }
        });
    }

    @Override
    public void showEmptyText(boolean isShow) {
        if (isShow) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void showToast(int resource) {
        ToastUtils.showToast(getString(resource), this);
    }

    @Override
    public void showCreateFragment() {
        if (!fragmentCreateList.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_list_fragment, fragmentCreateList).commitNow();
        }
    }

    @Override
    public void showEditFragment(ListModel listModel) {
        fragmentEditList.setEditItem(listModel);
        getSupportFragmentManager().beginTransaction().add(R.id.activity_list_fragment, fragmentEditList).commitNow();
    }

    @Override
    public void showActivityDetails(ListModel listModel) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra(LIST_ITEM_ID, listModel.getId());
        intent.putExtra(LIST_SERVER_ID, listModel.getServerId());
        intent.putExtra(LIST_NAME, listModel.getName());
        startActivity(intent);
    }

    @Override
    public void showCreateItemHistoryFragment() {
        fragmentCreateItemHistory.setCallback(this);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.activity_list_fragment2, fragmentCreateItemHistory).commitNow();
    }

    @Override
    public void setCategoryId(long financeCategoryId) {
        if(fragmentCreateItemHistory.isAdded()){
            fragmentCreateItemHistory.setCategory(financeCategoryId);
        }
    }

    @Override
    public void runSendIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        startActivity(intent);
    }

    @Override
    public String getStringResource(int resource) {
        return getString(resource);
    }

    @SuppressLint("HardwareIds")
    @Override
    public String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.context_menu_list_item_remove:
                removeList();
                break;
            case R.id.context_menu_list_item_edit:
                presenter.selectEditList();
                break;
            case R.id.context_menu_list_item_clear_bought:
                presenter.selectClearBought();
                break;
            case R.id.context_menu_list_item_clear_all:
                presenter.selectClearAll();
                break;
            case R.id.context_menu_list_item_add_cost:
                presenter.selectAddCost();
                break;
            case R.id.context_menu_list_item_send_list:
                presenter.selectShareList();
                break;
        }
        return true;
    }

    private void removeList() {
        DialogInterface.OnClickListener dialogListener = (dialogInterface, i) -> {
            if(i==DialogInterface.BUTTON_POSITIVE){
                presenter.selectRemoveList();

            }else{
                dialogInterface.cancel();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.delete_dialog)
                .setPositiveButton(R.string.text_positive_btn, dialogListener)
                .setNegativeButton(R.string.text_negative_btn, dialogListener);

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_list_activity_add_new_list:
                clickShowCrateFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clickShowCrateFragment() {
        presenter.clickShowCreateFragment();
    }

    @Override
    public void closeFragmentCreate() {
        if (fragmentCreateList.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(fragmentCreateList).commitNow();
        }else {
            getSupportFragmentManager().beginTransaction().remove(fragmentEditList).commitNow();
        }
        KeyboardUtil.hideKeyboard(this);
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            presenter.swipeRefresh();
        }
    };

    @Override
    public void closeCreateItemHistory() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).remove(fragmentCreateItemHistory).commitNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
        presenter = null;
    }
}