package com.kasandco.familyfinance.app.list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.icon.FragmentIcon;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.KeyboardUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements Constants, FragmentIcon.OnSelectIcon, FragmentCreateList.CreateListContract, ListContract, ListRvAdapter.ListAdapterListener {
    @Inject
    ListPresenter presenter;
    private ListRvAdapter adapter;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    TextView emptyText;
    @BindView(R.id.activity_list_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        App.getListActivityComponent(this).inject(this);
        refreshLayout = findViewById(R.id.activity_list_swipe_container);
        recyclerView = findViewById(R.id.activity_list_rv_items);
        emptyText = findViewById(R.id.activity_list_text_empty);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
        presenter.getListItems();
    }

    @Override
    public void menuOnClick(ListRvAdapter.ViewHolder holder) {
        Log.e("ClickMenu", "okok");
    }

    @Override
    public void itemOnClick(ListRvAdapter.ViewHolder holder) {

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
    public void addAdapter() {
        adapter = new ListRvAdapter(presenter.getItems());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
    public void updateAdapter(int position, boolean isEdit) {
        if(!isEdit) {
            adapter.notifyItemInserted(position);
            if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT)).isAdded()) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT);
                reloadFragment(fragment);
            }
            showToast(R.string.text_list_added);
        }else {
            adapter.notifyItemChanged(position);
            adapter.nullPosition();
            closeFragmentCreate();
        }
    }

    private void reloadFragment(Fragment fragment) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.add(R.id.activity_list_fragment, FragmentCreateList.class, null, CREATE_FRAGMENT);
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void editListItem(ListModel listItem) {
        if(adapter.getPosition()!=-1){
            showCrateFragment();
            if(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT)).isAdded()) {
                ((FragmentCreateList) Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT))).setEdit();
                ((FragmentCreateList) Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT))).setText(listItem.getName());
                ((FragmentCreateList) Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT))).setIcon(listItem.getIcon());
            }
        }
    }

    @Override
    public void removeItemUpdate(int position) {
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        presenter.onContextMenuClick(item.getItemId(), adapter.getPosition());
        return super.onContextItemSelected(item);
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
                showCrateFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean showCrateFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.activity_list_fragment) == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_list_fragment, FragmentCreateList.class, null, CREATE_FRAGMENT).commit();
            KeyboardUtil.showKeyboard(this);
            getSupportFragmentManager().executePendingTransactions();
            return true;
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_list_fragment, FragmentCreateList.class, null, CREATE_FRAGMENT).commit();
            KeyboardUtil.showKeyboard(this);
            getSupportFragmentManager().executePendingTransactions();
            return true;
        }
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

        }
    };


    @Override
    public void create(String text, String iconPath) {
        presenter.create(text, iconPath);
        iconPath=null;
    }

    @Override
    public void edit(String text, String iconPath) {
        presenter.edit(adapter.getPosition(), text, iconPath);
    }

    @Override
    public void closeFragmentCreate() {
        if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT)).isAdded()) {
            KeyboardUtil.hideKeyboard(this);
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT)).commit();

        }
    }

    @Override
    public void showSelectIconFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.activity_list_fragment2, FragmentIcon.class, null, SELECT_ICON_FRAGMENT).commit();
    }


    @Override
    public void onSelectIcon(String path) {
        if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT)).isAdded()) {
            ((FragmentCreateList) getSupportFragmentManager().findFragmentByTag(CREATE_FRAGMENT)).setIcon(path);

        }
        closeIconFragment();
    }

    @Override
    public void closeIconFragment() {
        if (Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag(SELECT_ICON_FRAGMENT)).isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag(SELECT_ICON_FRAGMENT)).commit();
        }
    }

    @Override
    public void removeIcon() {
        onSelectIcon(null);
    }
}