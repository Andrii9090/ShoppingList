package com.kasandco.familyfinance.app.item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;

import java.util.List;

import javax.inject.Inject;

public class ItemActivity extends AppCompatActivity implements ItemContract {
    @Inject
    ItemPresenter presenter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        App.getItemComponent(this).inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    public void startAdapter(List<ItemModel> items) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }
}