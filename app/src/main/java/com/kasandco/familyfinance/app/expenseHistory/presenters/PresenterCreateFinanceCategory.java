package com.kasandco.familyfinance.app.expenseHistory.presenters;

import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.expenseHistory.FinanceRepository;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.app.list.ListDao;
import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.core.BasePresenter;

import javax.inject.Inject;

public class PresenterCreateFinanceCategory extends BasePresenter<CreateCategoryContract> implements FinanceRepository.FinanceRepositoryCallback {
    FinanceRepository repository;

    @Inject
    ListDao listDao;

    public PresenterCreateFinanceCategory(FinanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void viewReady(CreateCategoryContract view) {
        this.view = view;
    }

    public void inputData(FinanceCategoryModel editItem, String name, String iconPath, int type, boolean checked) {
        if (iconPath.isEmpty() || name.isEmpty()) {
            view.showToast(R.string.text_name_or_icon_error);
        } else if (name.length() > 20) {
            view.showToast(R.string.text_name_lenght);
        } else {
            if(editItem==null) {
                FinanceCategoryModel category = new FinanceCategoryModel(name, iconPath, type, String.valueOf(System.currentTimeMillis()));
                repository.createNewCategory(category, this, checked);
            }else {
                if(!editItem.getName().equals(name) || !editItem.getIconPath().equals(iconPath)){
                    editItem.setDateMod(String.valueOf(System.currentTimeMillis()));
                    editItem.setIconPath(iconPath);
                    editItem.setName(name);
                    repository.edit(editItem, this);
                }
            }
        }
    }

    @Override
    public void setLastCategory(FinanceCategoryModel category) {
        ListModel list = new ListModel(category.getName(), String.valueOf(System.currentTimeMillis()), category.getIconPath(), category.getId());
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDao.insert(list);
            }
        }).start();
    }

    public void clickButtonCreate() {
        view.getInputData();
    }

    @Override
    public void notUnique() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.showToast(R.string.text_error_category_existed);
            }
        });
    }

    @Override
    public void added() {
        view.close();
    }
}
