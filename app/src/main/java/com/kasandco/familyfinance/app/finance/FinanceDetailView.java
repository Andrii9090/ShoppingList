package com.kasandco.familyfinance.app.finance;

import com.kasandco.familyfinance.app.finance.models.FinanceDetailModel;
import com.kasandco.familyfinance.core.BaseContract;

import java.util.GregorianCalendar;
import java.util.List;

public interface FinanceDetailView {
    interface View extends BaseContract {
        void showDatePickerDialog();

        void setTextToBtnSelectPeriod(GregorianCalendar gregorianCalendar, GregorianCalendar gregorianCalendar1);

        long getCategoryId();

        void addAdapterToRV(List<FinanceDetailModel> items);
    }
    
    interface Presenter{
        void setDateRangePeriod(String valueOf, String valueOf1);
    }
}
