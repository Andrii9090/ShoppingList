package com.kasandco.familyfinance.app.list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.item.ItemActivity;
import com.kasandco.familyfinance.app.list.CreateList.FragmentCreateList;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.KeyboardUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.io.Serializable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends AppCompatActivity implements Constants, ListContract, FragmentCreateList.CreateListListener, ListRvAdapter.ListAdapterListener {
    @Inject
    ListPresenter presenter;
    @Inject
    FragmentCreateList fragmentCreateList;

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
        refreshLayout.setOnRefreshListener(refreshListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    public void menuOnClick(ListRvAdapter.ViewHolder holder) {
        Log.e("ClickMenu", "okok");
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
            KeyboardUtil.showKeyboard(this);
        }
    }

    @Override
    public void showEditFragment(ListModel listModel) {
        showCreateFragment();
        fragmentCreateList.setEditItem(listModel);
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
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.context_menu_list_item_remove:
                presenter.selectRemoveList();
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
        }
        return true;
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
            KeyboardUtil.hideKeyboard(this);
        }
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            presenter.swipeRefresh();
        }
    };
}