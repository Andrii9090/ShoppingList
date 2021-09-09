package com.kasandco.familyfinance.app.statistic;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.BasePresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
@StatisticScope
public class StatisticPresenter extends BasePresenter<StatisticContract> implements StatisticRepository.RepositoryCallback {
    private StatisticRepository repository;
    private List<FinanceStatModel> statDb;
    private int type;
    List<StatModel> statResult;

    long startDate, endDate;

    @Inject
    public StatisticPresenter(StatisticRepository repository) {
        this.repository = repository;
        statResult =new ArrayList<>();
    }

    @Override
    public void viewReady(StatisticContract view) {
        this.view = view;
        this.view.setupPieChart();
        this.view.showLoading();
        type = this.view.getStatisticType();
        setDefaultPeriod();
    }

    private void setDefaultPeriod() {
        GregorianCalendar now = new GregorianCalendar();
        now.setTime(new Date());
        setDateRangePeriod(String.valueOf(now.getTime().getTime()), String.valueOf(now.getTime().getTime()));
    }

    @Override
    public void loadedData(List<FinanceStatModel> statDb) {
        this.statDb = statDb;
        prepareDataForPieChart();
    }

    private void prepareDataForPieChart() {
        float total = getTotal();
        view.setTotalText(total);
        if (total > 0) {
            statResult.clear();
            StatModel otherStat = new StatModel();
            otherStat.setName(view.getStringResource(R.string.text_other_cat_statistic));
            otherStat.setTotal(0);
            for (int i = 0; i < statDb.size(); i++) {
                statDb.get(i).setPercent((float) statDb.get(i).getTotal() * 100 / total);
                if (statDb.get(i).getPercent()<0.025f || i > 9) {
                    otherStat.setTotal((float) otherStat.getTotal()+(float) statDb.get(i).getTotal());
                    otherStat.setItem(statDb.get(i));
                }else {
                    StatModel item = new StatModel(statDb.get(i).getName(), (float) statDb.get(i).getTotal(), statDb.get(i).getPercent());
                    item.setItem(statDb.get(i));
                    statResult.add(item);
                }
            }

            if (otherStat.getTotal() > 0) {
                otherStat.setPercent((float) otherStat.getTotal()*100/total);
                statResult.add(otherStat);
            }

            view.loadPieChartData(statResult);
            view.hideLoading();
        } else {
            view.hideLoading();
            view.showEmptyText();
        }
    }


    private float getTotal() {
        float total = 0f;
        for (FinanceStatModel item : statDb) {
            total += item.getTotal();
        }
        return total;
    }

    public void dadaLoaded() {
        view.hideLoading();
        view.showStatChart();
    }

    private void loadData() {
        view.showLoading();
        repository.getAllFromPeriod(type, String.valueOf(startDate), String.valueOf(endDate), this);
    }

    public void clickBtnSelectPeriod() {
        view.showDatePickerDialog();
    }

    public void setDateRangePeriod(String dateStart, String dateEnd) {
        GregorianCalendar start = new GregorianCalendar();
        start.setTime(new Date(Long.parseLong(dateStart)));
        start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DATE), 0, 0, 0);
        startDate = start.getTime().getTime();

        Date endPeriod = new Date(Long.parseLong(dateEnd));
        GregorianCalendar end = new GregorianCalendar();
        end.setTime(endPeriod);
        end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DATE), 23, 59, 59);
        endDate = end.getTime().getTime();

        view.setTextToBtnSelectPeriod(start, end);
        loadData();
    }

    public void selectPieChartItem(int dataIndex) {
        List<FinanceStatModel> clickItem = statResult.get(dataIndex).getItems();
        view.showDetailDialog(clickItem);
    }

    @Override
    public void destroy() {
        super.destroy();
        repository = null;
    }
}
