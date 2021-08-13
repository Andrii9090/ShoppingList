package com.kasandco.familyfinance.app.item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.item.fragmentCreate.FragmentItemCreate;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.KeyboardUtil;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class ItemActivity extends AppCompatActivity implements ItemContract, Constants, FragmentItemCreate.ClickListenerCreateFragment {
    @Inject
    ItemPresenter presenter;

    @Inject
    FragmentItemCreate createFragment;

    @Inject
    ItemRepository repository;


    private static final int REQUEST_TAKE_PHOTO = 1;


    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getItemComponent(this).inject(this);
        setContentView(R.layout.activity_item);
        recyclerView = findViewById(R.id.item_activity_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Toolbar toolbar = findViewById(R.id.item_activity_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    public void startAdapter(RecyclerView.Adapter<?> adapter) {
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

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
    public void showEditForm(ItemModel item) {
        showCreateFragment();
        createFragment.edit(item);
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item_activity, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_activity_add_new_list:
                showCreateFragment();
                break;
        }
        return true;
    }

    private void showCreateFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.item_activity_fragment) != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.item_activity_fragment, createFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.item_activity_fragment, createFragment).commit();
        }
        getSupportFragmentManager().executePendingTransactions();
        KeyboardUtil.showKeyboard(this);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_context_item_edit:
                editItem();
                break;
            case R.id.menu_context_item_remove:
                removeItem();
                break;
            case R.id.menu_context_item_add_photo_camera:
                clickCamera();
                break;
        }

        return true;
    }

    private void clickCamera() {

    }

    private void removeItem() {
        presenter.removeItem();
    }

    private void editItem() {
        presenter.clickEdit();
    }

    @Override
    public void close() {
        if (createFragment.isAdded()) {
            KeyboardUtil.hideKeyboard(this);
            getSupportFragmentManager().beginTransaction().remove(createFragment).commit();
        }
    }
}