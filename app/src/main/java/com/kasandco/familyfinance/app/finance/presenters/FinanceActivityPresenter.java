package com.kasandco.familyfinance.app.finance.presenters;

import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.Calendar;

public class FinanceActivityPresenter extends BasePresenter<FinanceViewContract> implements FinanceRepository.FinanceTotalResult {
    private FinanceRepository repository;
    private String startDate;
    private String endDate;
    private double totalCost;
    private double totalIncome;

    public FinanceActivityPresenter(FinanceRepository repository){
        this.repository = repository;
    }

    @Override
    public void viewReady(FinanceViewContract view) {
        this.view = view;
    }

    @Override
    public void swipeRefresh() {

    }

    public void clickBtnNewCategory(int type) {
        view.showCreateCategoryFragment(type);
    }

    public void clickCloseCreateFragment() {
        view.hideCreateFragment();
    }

    public void setStatPeriod(int period) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 1000);
        endDate = String.valueOf(calendar.getTime().getTime());
        Calendar calendarStart = Calendar.getInstance();
        switch (period) {
            case Calendar.MONTH:
                calendarStart.add(Calendar.MONTH, -1);
                break;
            case Calendar.DAY_OF_YEAR:
                calendarStart.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case Calendar.DAY_OF_WEEK:
                calendarStart.set(calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DATE), 0, 0);
                break;
        }
        startDate = String.valueOf(calendarStart.getTime().getTime());
        view.sendPeriodToFragment(startDate,endDate);
        getTotalToPeriod();
    }

    public void setDateRangePeriod(String start, String end){
        startDate = start;
        endDate = end;
        view.sendPeriodToFragment(startDate, endDate);
        getTotalToPeriod();
    }

    public void getTotalToPeriod() {
        repository.getTotalToPeriod(startDate, endDate, this);
    }

    public void clickSetPeriod() {
        view.showLoading();
        view.showDatePickerDialog();
    }

    @Override
    public void setTotal(int type, double res) {
        if(type==1){
            totalCost=res;
        }else {
            totalIncome=res;
        }
        view.getTabPositionSelected();
    }

    public void selectIncomeFragment() {
        view.setTotal(totalIncome);
    }

    public void selectCostFragment() {
        view.setTotal(totalCost);
    }

    public String getDateStart() {
        return startDate;
    }
    public String getDateEnd() {
        return endDate;
    }

    public void viewDestroy() {
        repository.clearDisposable();
    }
}
