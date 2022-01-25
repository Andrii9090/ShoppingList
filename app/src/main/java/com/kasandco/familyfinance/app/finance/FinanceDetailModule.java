package com.kasandco.familyfinance.app.finance;

import com.kasandco.familyfinance.app.finance.core.FinanceDetailScope;
import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.presenters.FinanceDetailPresenter;
import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.utils.NetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class FinanceDetailModule {

    @FinanceDetailScope
    @Provides
    FinanceDao providesFinanceDao(AppDataBase appDataBase){
        return appDataBase.getFinanceDao();
    }

    @Provides
    @FinanceDetailScope
    FinanceDetailRepository providesFinanceDetailRepository(FinanceDao financeDao, AppDataBase appDataBase, Retrofit retrofit, SharedPreferenceUtil sharedPreferenceUtil, NetworkConnect _isNetworkConnect){
        return new FinanceDetailRepository(financeDao, appDataBase.getFinanceHistorySyncDao(), retrofit, sharedPreferenceUtil,_isNetworkConnect, appDataBase.getFinanceCategoryDao());
    }

    @FinanceDetailScope
    @Provides
    FinanceDetailPresenter providesFinanceDetailPresenter(FinanceDetailRepository repository){
        return new FinanceDetailPresenter(repository);
    }
}
