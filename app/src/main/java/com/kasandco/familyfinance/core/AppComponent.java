package com.kasandco.familyfinance.core;

import com.kasandco.familyfinance.app.splash.SplashActivity;
import com.kasandco.familyfinance.app.expenseHistory.FinanceComponent;
import com.kasandco.familyfinance.app.expenseHistory.FinanceModule;
import com.kasandco.familyfinance.app.item.DataComponent;
import com.kasandco.familyfinance.app.item.DataModule;
import com.kasandco.familyfinance.app.item.ItemComponent;
import com.kasandco.familyfinance.app.item.ItemModule;
import com.kasandco.familyfinance.app.list.ListActivityComponent;
import com.kasandco.familyfinance.app.list.ListModule;
import com.kasandco.familyfinance.app.splash.SplashComponent;
import com.kasandco.familyfinance.app.splash.SplashModule;
import com.kasandco.familyfinance.app.statistic.StatisticComponent;
import com.kasandco.familyfinance.app.statistic.StatisticModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {
    ListActivityComponent plus(ListModule module);
    ItemComponent plus(ItemModule module);
    DataComponent plus(DataModule module);
    FinanceComponent plus(FinanceModule module);
    SplashComponent plus(SplashModule module);
    StatisticComponent plus(StatisticModule module);
}
