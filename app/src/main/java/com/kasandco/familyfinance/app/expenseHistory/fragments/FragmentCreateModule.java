package com.kasandco.familyfinance.app.expenseHistory.fragments;

import androidx.fragment.app.Fragment;

import com.kasandco.familyfinance.app.expenseHistory.FinanceRepository;
import com.kasandco.familyfinance.app.expenseHistory.presenters.CreateCategoryContract;
import com.kasandco.familyfinance.app.expenseHistory.presenters.PresenterCreateFinanceCategory;
import com.kasandco.familyfinance.app.list.ListDao;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentCreateModule {
    @CreateCategoryScope
    @Provides
    PresenterCreateFinanceCategory providesPresenter(FinanceRepository repository, ListDao listDao){
        return new PresenterCreateFinanceCategory(repository, listDao);
    }

}
