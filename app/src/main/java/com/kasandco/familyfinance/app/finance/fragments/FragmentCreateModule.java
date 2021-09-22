package com.kasandco.familyfinance.app.finance.fragments;

import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.presenters.PresenterCreateFinanceCategory;
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
