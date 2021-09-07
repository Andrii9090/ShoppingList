package com.kasandco.familyfinance.app.statistic;

import com.kasandco.familyfinance.core.BaseContract;

import java.util.GregorianCalendar;
import java.util.List;

public interface StatisticContract extends BaseContract {
    void setupPieChart();

    void loadPieChartData(List<StatModel> statDb);

    void showStatChart();

    String getStringResource(int resourceId);

    void showEmptyText();

    void setTotalText(float total);

    void showDatePickerDialog();

    void setTextToBtnSelectPeriod(GregorianCalendar calendarStart, GregorianCalendar calendarEnd);

    void showDetailDialog(List<FinanceStatModel> clickItem);

    int getStatisticType();
}
