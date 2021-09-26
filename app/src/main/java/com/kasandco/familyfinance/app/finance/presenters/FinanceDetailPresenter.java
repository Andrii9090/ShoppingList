package com.kasandco.familyfinance.app.finance.presenters;

import com.kasandco.familyfinance.app.finance.FinanceDetailRepository;
import com.kasandco.familyfinance.app.finance.FinanceDetailView;
import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.helpers.ConverterFinanceModelToFinanceDetailModel;
import com.kasandco.familyfinance.app.finance.models.FinanceDetailModel;
import com.kasandco.familyfinance.app.finance.models.FinanceModel;
import com.kasandco.familyfinance.core.BasePresenter;
import com.kasandco.familyfinance.utils.DateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

public class FinanceDetailPresenter extends BasePresenter<FinanceDetailView.View> implements FinanceDetailView.Presenter, FinanceDetailRepository.FinanceDetailCallback {
    private List<FinanceDetailModel> items;
    private FinanceDetailRepository repository;

    private long startDate, endDate;

    public FinanceDetailPresenter(FinanceDetailRepository _repository){
        items = new ArrayList<>();
        repository = _repository;
    }

    @Override
    public void viewReady(FinanceDetailView.View view) {
        this.view = view;
        setDefaultPeriod();
    }

    private void setDefaultPeriod() {
        GregorianCalendar now = new GregorianCalendar();
        now.setTime(new Date());
        setDateRangePeriod(String.valueOf(now.getTime().getTime()), String.valueOf(now.getTime().getTime()));
    }

    @Override
    public void setDateRangePeriod(String dateStart, String dateEnd) {
        GregorianCalendar[] period = DateHelper.formatDateToStartAndEndDay(dateStart, dateEnd);
        startDate = period[0].getTime().getTime();
        endDate = period[1].getTime().getTime();
        view.setTextToBtnSelectPeriod(period[0], period[1]);
        loadData();
    }

    private void loadData() {
        repository.getFinancesDetail(view.getCategoryId(), String.valueOf(startDate), String.valueOf(endDate), this);
    }

    @Override
    public void setFinanceItems(List<FinanceModel> items) {
        ConverterFinanceModelToFinanceDetailModel converter = new ConverterFinanceModelToFinanceDetailModel(items);
        this.items = converter.convert();
        view.addAdapterToRV(this.items);
    }
}
