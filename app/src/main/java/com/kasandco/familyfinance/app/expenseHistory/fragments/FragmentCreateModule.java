package com.kasandco.familyfinance.app.expenseHistory.fragments;

import androidx.fragment.app.Fragment;

import com.kasandco.familyfinance.app.expenseHistory.FinanceRepository;
import com.kasandco.familyfinance.app.expenseHistory.presenters.CreateCategoryContract;
import com.kasandco.familyfinance.app.expenseHistory.presenters.PresenterCreateFinanceCategory;
import com.kasandco.familyfinance.app.list.ListDao;
import com.kasandco.familyfinance.core.AppDataBase;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentCreateModule {
    @CreateCategoryScope
    @Provides
    PresenterCreateFinanceCategory providesPresenter(FinanceRepository repository) {
        return new PresenterCreateFinanceCategory(repository);
    }

    @Provides
    @CreateCategoryScope
    ListDao providesListDao(AppDataBase appDataBase) {
        return appDataBase.getListDao();
    }

}
