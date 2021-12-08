package com.kasandco.familyfinance.app.finance;

import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textview.MaterialTextView;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.BaseActivity;
import com.kasandco.familyfinance.app.finance.adapters.FinanceDetailAdapter;
import com.kasandco.familyfinance.app.finance.core.FinanceDetailView;
import com.kasandco.familyfinance.app.finance.models.FinanceDetailModel;
import com.kasandco.familyfinance.app.finance.presenters.FinanceDetailPresenter;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.DateHelper;

import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

public class FinanceDetailActivity extends BaseActivity implements FinanceDetailView.View, FinanceDetailAdapter.OnClickDetailAdapterListener {

    private RecyclerView recyclerView;
    private ContentLoadingProgressBar loader;
    private Button btnPeriod;
    private long financeCategoryId;
    private FinanceDetailAdapter adapter;

    @Inject
    FinanceDetailPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().plus(new FinanceDetailModule()).inject(this);
        setContentView(R.layout.activity_finance_detail);
        recyclerView = findViewById(R.id.finance_detail_recycler_view);
        loader = findViewById(R.id.finance_detail_loader);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnPeriod = findViewById(R.id.finance_detail_btn_period);
        btnPeriod.setOnClickListener((view -> {
            showDatePickerDialog();
        }));

        Toolbar toolbar = findViewById(R.id.finance_detail_toolbar);

        Intent intent = getIntent();
        financeCategoryId = intent.getLongExtra(Constants.FINANCE_CATEGORY_ID, 1);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new FinanceDetailAdapter();

        setSupportActionBar(toolbar);
        MaterialTextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.title_finance_detail);
        toolbar.findViewById(R.id.toolbar_menu).setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        if (activityClass != getClass()) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, FinanceActivity.class);
        startActivity(intent);
    }

    @Override
    public void showLoading() {
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loader.setVisibility(View.GONE);
    }

    @Override
    public void showDatePickerDialog() {
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(getString(R.string.text_select_date))
                        .build();
        dateRangePicker.addOnPositiveButtonClickListener(selection -> presenter.setDateRangePeriod(String.valueOf(selection.first), String.valueOf(selection.second)));
        dateRangePicker.show(getSupportFragmentManager(), "DatePicker");
    }

    @Override
    public void setTextToBtnSelectPeriod(GregorianCalendar calendarStart, GregorianCalendar calendarEnd) {
        btnPeriod.setText(DateHelper.formatDatePeriod(getString(R.string.text_date_period), calendarStart.getTime().getTime(), calendarEnd.getTime().getTime()));
    }

    @Override
    public long getCategoryId() {
        return financeCategoryId;
    }

    @Override
    public void addAdapterToRV(List<FinanceDetailModel> items) {
        adapter.setItems(items);
        recyclerView.setAdapter(adapter);
        adapter.setCallbackListener(this);
    }

    @Override
    public void deleteViewItem(int position) {
        adapter.deleteItem(position);
    }

    @Override
    public void removeItem(int position) {
        presenter.clickDeleteItem(position);
    }
}