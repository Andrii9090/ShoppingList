package com.kasandco.familyfinance.app.finance;

import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.presenters.FinanceDetailPresenter;
import com.kasandco.familyfinance.core.AppDataBase;

import dagger.Module;
import dagger.Provides;

@Module
public class FinanceDetailModule {

    @FinanceDetailScope
    @Provides
    FinanceDao providesFinanceDao(AppDataBase appDataBase){
        return appDataBase.getFinanceDao();
    }

    @Provides
    @FinanceDetailScope
    FinanceDetailRepository providesFinanceDetailRepository(FinanceDao financeDao){
        return new FinanceDetailRepository(financeDao);
    }

    @FinanceDetailScope
    @Provides
    FinanceDetailPresenter providesFinanceDetailPresenter(FinanceDetailRepository repository){
        return new FinanceDetailPresenter(repository);
    }
}
