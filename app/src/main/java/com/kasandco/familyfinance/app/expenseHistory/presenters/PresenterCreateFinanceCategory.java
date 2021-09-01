package com.kasandco.familyfinance.app.expenseHistory.presenters;

import android.os.Handler;
import android.os.Looper;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.expenseHistory.FinanceRepository;
import com.kasandco.familyfinance.app.list.ListDao;
import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.core.BasePresenter;

public class PresenterCreateFinanceCategory extends BasePresenter<CreateCategoryContract> implements FinanceRepository.FinanceRepositoryCallback {
    FinanceRepository repository;

    ListDao listDao;
    boolean checked;
    FinanceCategoryModel category;

    public PresenterCreateFinanceCategory(FinanceRepository repository, ListDao listDao) {
        this.repository = repository;
        this.listDao = listDao;
    }

    @Override
    public void viewReady(CreateCategoryContract view) {
        this.view = view;
    }

    public void inputData(FinanceCategoryModel editItem, String name, String iconPath, int type, boolean checked) {
        this.checked = checked;
        if (name!=null && iconPath!=null) {
            if (name.length() > 20) {
                view.showToast(R.string.text_name_length);
            } else {
                if(editItem==null) {
                    category = new FinanceCategoryModel(name, iconPath, type, String.valueOf(System.currentTimeMillis()));
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
        } else {
            view.showToast(R.string.text_name_or_icon_error);
        }
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
    public void added(long id) {
        if(checked){
            ListModel list = new ListModel(category.getName(), String.valueOf(System.currentTimeMillis()), category.getIconPath(), category.getId());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listDao.insert(list);
                }
            }).start();
        }
        view.close();
    }
}
