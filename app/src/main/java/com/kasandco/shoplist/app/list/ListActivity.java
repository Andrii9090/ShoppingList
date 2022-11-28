package com.kasandco.shoplist.app.list;

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
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.kasandco.shoplist.App;
import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.BaseActivity;
import com.kasandco.shoplist.app.item.ItemActivity;
import com.kasandco.shoplist.app.list.createEditList.FragmentCreateList;
import com.kasandco.shoplist.app.list.createEditList.FragmentEditList;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.utils.KeyboardUtil;
import com.kasandco.shoplist.utils.ShowCaseUtil;
import com.kasandco.shoplist.utils.ToastUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class ListActivity extends BaseActivity implements Constants, ListContract, FragmentCreateList.CreateListListener, GuideListener, ListRvAdapter.ListAdapterListener {

    @Inject
    public ListPresenter presenter;
    @Inject
    FragmentCreateList fragmentCreateList;
    @Inject
    FragmentEditList fragmentEditList;

    RecyclerView recyclerView;
    TextView emptyText;
    Toolbar toolbar;

    @Inject
    ShowCaseUtil showCaseUtil;

    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        App.getListActivityComponent(this).inject(this);
        toolbar = findViewById(R.id.activity_list_toolbar);
        refreshLayout = findViewById(R.id.swipe_container);
        recyclerView = findViewById(R.id.activity_list_rv_items);
        emptyText = findViewById(R.id.activity_list_text_empty);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mAdView = findViewById(R.id.list_item_adView);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.title_list);
        toolbar.findViewById(R.id.toolbar_menu).setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
        refreshLayout.setOnRefreshListener(refreshListener);
        emptyText.setVisibility(View.GONE);
        showAdd();
    }

    private void showAdd() {
        if (sharedPreferenceUtil.isPro()) {
            mAdView.setVisibility(View.GONE);
        } else {
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            MobileAds.initialize(this, initializationStatus -> {
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
        new Handler().postDelayed(() -> {
            if (sharedPreferenceUtil.getSharedPreferences().getString(Constants.IS_SHOW_INFO_ADD_LIST, null) == null) {
                showCaseUtil.setCase(findViewById(R.id.menu_list_activity_add_new_list), R.string.creating_new_list, R.string.creating_new_list_text);
                showCaseUtil.show();
                showCaseUtil.setOnClickListener(this);
            }
        }, 1000);
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        if (activityClass != getClass()) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void itemOnClick(ListRvAdapter.ViewHolder holder) {
        presenter.clickItem(holder);
    }

    @Override
    public void loaded(boolean empty) {
        showEmptyText(empty);
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
        switch (item.getItemId()) {
            case R.id.context_menu_list_item_remove:
                confirmRemove("selectRemoveList");
                break;
            case R.id.context_menu_list_item_edit:
                presenter.selectEditList();
                break;
            case R.id.context_menu_list_item_clear_bought:
                confirmRemove("selectClearBought");
                break;
            case R.id.context_menu_list_item_clear_all:
                confirmRemove("selectClearAll");
                break;
            case R.id.context_menu_list_item_send_list:
                presenter.selectSendListToMessage();
                break;
            case R.id.context_menu_list_item_private_list:
                presenter.selectPrivateList();
                break;
        }
        return true;
    }

    private void confirmRemove(String removeName) {
        DialogInterface.OnClickListener dialogListener = (dialogInterface, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                remove(removeName);
            } else {
                dialogInterface.cancel();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.delete_dialog)
                .setPositiveButton(R.string.text_positive_btn, dialogListener)
                .setNegativeButton(R.string.text_negative_btn, dialogListener);

        builder.show();
    }

    private void remove(String removeName) {
        switch (removeName){
            case "selectRemoveList":
                presenter.selectRemoveList();
                break;
            case "selectClearBought":
                presenter.selectClearBought();
                break;
            case "selectClearAll":
                presenter.selectClearAll();
                break;
        }
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
        } else {
            getSupportFragmentManager().beginTransaction().remove(fragmentEditList).commitNow();
        }
        KeyboardUtil.hideKeyboard(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
        presenter = null;
    }

    protected SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            presenter.swipeRefresh();
        }
    };

    @Override
    public void onDismiss(View view) {
        showCaseUtil.hide();
    }
}