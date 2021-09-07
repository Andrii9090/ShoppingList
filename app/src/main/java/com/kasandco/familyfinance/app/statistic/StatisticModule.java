package com.kasandco.familyfinance.app.statistic;

import com.kasandco.familyfinance.core.AppDataBase;

import dagger.Module;
import dagger.Provides;

@Module
public class StatisticModule {

    @StatisticScope
    @Provides
    StatisticPresenter providesStatProvider(StatisticRepository repository){
        return new StatisticPresenter(repository);
    }

    @StatisticScope
    @Provides
    StatisticRepository providesStatRepository(AppDataBase appDataBase){
        return new StatisticRepository(appDataBase.getFinanceDoa());
    }

}
