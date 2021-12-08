package com.kasandco.familyfinance.app.finance.helpers;

import com.kasandco.familyfinance.app.finance.models.FinanceDetailModel;
import com.kasandco.familyfinance.app.finance.models.FinanceHistoryModel;
import com.kasandco.familyfinance.utils.DateHelper;

import java.util.ArrayList;
import java.util.List;

public class ConverterFinanceModelToFinanceDetailModel {
    private List<FinanceHistoryModel> financeModel;
    private List<FinanceDetailModel> financeDetailModel;
    private String currentDate;

    public ConverterFinanceModelToFinanceDetailModel(List<FinanceHistoryModel> _financeModel){
        financeModel = new ArrayList<>();
        financeDetailModel = new ArrayList<>();
        financeModel.addAll(_financeModel);
    }

    public List<FinanceDetailModel> convert(){
        for (int i=0; i<financeModel.size();i++) {
            String dateItem = DateHelper.formatDateToStr(financeModel.get(i).getDate());

            if(i==0) {
                currentDate = dateItem;
                financeDetailModel.add(new FinanceDetailModel(financeModel.get(i).getDate(), -1));
            }else {
                if (!dateItem.equals(currentDate)) {
                    financeDetailModel.add(new FinanceDetailModel(financeModel.get(i).getDate(), -1));
                    currentDate = dateItem;
                }
            }
            financeDetailModel.add(new FinanceDetailModel(financeModel.get(i).getId(), financeModel.get(i).getServerId(), financeModel.get(i).getUserEmail(), financeModel.get(i).getDate(), financeModel.get(i).getCategoryId(), financeModel.get(i).getTotal(), financeModel.get(i).getComment(), financeModel.get(i).getType()));

        }
        return financeDetailModel;
    }

}
