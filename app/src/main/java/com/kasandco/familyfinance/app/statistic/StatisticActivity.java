package com.kasandco.familyfinance.app.statistic;

import androidx.core.util.Pair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.BaseActivity;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

public class StatisticActivity extends BaseActivity implements StatisticContract, OnChartValueSelectedListener {

    @Inject
    StatisticPresenter presenter;

    private PieChart pieChart;
    private Button btnPeriod;
    private ImageButton btnMenu;
    private LinearProgressIndicator loading;
    private int type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().plus(new StatisticModule()).inject(this);
        Intent intent = getIntent();
        type = intent.getIntExtra(Constants.STAT_TYPE,Constants.TYPE_COSTS);
        setContentView(R.layout.activity_statistic);
        pieChart = findViewById(R.id.pieChart_view);
        btnPeriod = findViewById(R.id.activity_statistic_btn_set_period);
        btnMenu = findViewById(R.id.activity_statistic_btn_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        loading = findViewById(R.id.activity_statistic_loading);

        btnMenu.setOnClickListener(clickListener);
        btnPeriod.setOnClickListener(clickListener);
        //TODO Сделать навигацию меню DRAW
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    public void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(0);
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.getDescription().setEnabled(false);
    }

    @Override
    public void loadPieChartData(List<StatModel> data) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (StatModel item : data) {
            entries.add(new PieEntry(item.getPercent(),
                            String.format(getString(R.string.format_category_stat),
                                    item.getName(),
                                    formatCurrencyTotal(getString(R.string.text_currency_format),
                                            (float) item.getTotal())
                            )
                    )
            );
        }
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }
        for (int color : ColorTemplate.COLORFUL_COLORS) {
            colors.add(color);
        }
        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextSize(13f);
        pieData.setValueTextColor(Color.BLACK);

        pieChart.setData(pieData);
        presenter.dadaLoaded();
    }

    private String formatCurrencyTotal(String resource, float total) {
        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(this);
        String currency = sharedPreferenceUtil.getSharedPreferences().getString(Constants.SHP_DEFAULT_CURRENCY, Constants.DEFAULT_CURRENCY_VALUE);
        return String.format(resource, total, currency);
    }

    @Override
    public void showStatChart() {
        pieChart.invalidate();
        pieChart.animateY(600, Easing.Linear);
    }

    @Override
    public String getStringResource(int resourceId) {
        return getString(resourceId);
    }

    @Override
    public void showEmptyText() {
        pieChart.setData(null);
        pieChart.setNoDataText(getString(R.string.text_empty_pie_chart));
        pieChart.setNoDataTextColor(Color.BLACK);
        pieChart.invalidate();
    }

    @Override
    public void setTotalText(float total) {
        pieChart.setCenterText(String.format(getString(R.string.text_currency_stat_total_format), getString(R.string.text_currency_stat_total), formatCurrencyTotal(getString(R.string.text_currency_format), total)));
    }

    @Override
    public void showDatePickerDialog() {
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(getString(R.string.text_select_date))
                        .build();
        dateRangePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                presenter.setDateRangePeriod(String.valueOf(selection.first), String.valueOf(selection.second));
            }
        });
        dateRangePicker.show(getSupportFragmentManager(), "DatePicker");
        hideLoading();
    }

    @Override
    public void setTextToBtnSelectPeriod(GregorianCalendar calendarStart, GregorianCalendar calendarEnd) {
        @SuppressLint("SimpleDateFormat") String btnSelectPeriodText = String.format(getString(R.string.text_date_period), new SimpleDateFormat("dd/MM/yy").format(calendarStart.getTime().getTime()), new SimpleDateFormat("dd/MM/yy").format(calendarEnd.getTime().getTime()));
        btnPeriod.setText(btnSelectPeriodText);
    }

    @Override
    public void showDetailDialog(List<FinanceStatModel> clickItems) {
        if(clickItems.size()>1) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.detailt_stat);
            for (FinanceStatModel item : clickItems) {
                adapter.add(String.format(getString(R.string.format_detail_stat_item), item.getName(), formatCurrencyTotal(getString(R.string.text_currency_format), (float) item.getTotal()), item.getPercent() * 100));
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("")
                    .setAdapter(adapter, null)
                    .setNegativeButton(R.string.text_ok, (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    });


            AlertDialog dialog = builder.create();
            dialog.show();
        }else{
            ToastUtils.showToast(String.format(getString(R.string.format_detail_stat_item), clickItems.get(0).getName(), formatCurrencyTotal(getString(R.string.text_currency_format), (float) clickItems.get(0).getTotal()), clickItems.get(0).getPercent() * 100), this);
        }
    }

    @Override
    public int getStatisticType() {
        return type;
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        presenter.selectPieChartItem((int) h.getX());
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loading.setVisibility(View.INVISIBLE);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.activity_statistic_btn_menu) {
                drawerLayout.openDrawer(Gravity.LEFT);
            } else {
                presenter.clickBtnSelectPeriod();
            }
        }
    };

    @Override
    protected void onDestroy() {
        presenter.destroy();
        presenter = null;
        super.onDestroy();
    }
}