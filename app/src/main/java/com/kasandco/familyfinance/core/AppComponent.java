package com.kasandco.familyfinance.core;

import com.kasandco.familyfinance.app.finance.core.FinanceComponent;
import com.kasandco.familyfinance.app.finance.core.FinanceDetailComponent;
import com.kasandco.familyfinance.app.finance.FinanceDetailModule;
import com.kasandco.familyfinance.app.finance.FinanceModule;
import com.kasandco.familyfinance.app.item.DataComponent;
import com.kasandco.familyfinance.app.item.DataModule;
import com.kasandco.familyfinance.app.item.ItemComponent;
import com.kasandco.familyfinance.app.item.ItemModule;
import com.kasandco.familyfinance.app.list.ListActivityComponent;
import com.kasandco.familyfinance.app.list.ListModule;
import com.kasandco.familyfinance.app.settings.SettingsComponent;
import com.kasandco.familyfinance.app.settings.SettingsModule;
import com.kasandco.familyfinance.app.splash.SplashComponent;
import com.kasandco.familyfinance.app.splash.SplashModule;
import com.kasandco.familyfinance.app.statistic.StatisticComponent;
import com.kasandco.familyfinance.app.statistic.StatisticModule;
import com.kasandco.familyfinance.app.user.login.LoginComponent;
import com.kasandco.familyfinance.app.user.login.LoginModule;
import com.kasandco.familyfinance.app.user.registration.RegistrationComponent;
import com.kasandco.familyfinance.app.user.registration.RegistrationModule;
import com.kasandco.familyfinance.app.user.settings.UserSettingsComponent;
import com.kasandco.familyfinance.app.user.settings.UserSettingsModule;

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
    RegistrationComponent plus(RegistrationModule module);
    LoginComponent plus(LoginModule module);
    FinanceDetailComponent plus(FinanceDetailModule module);
    UserSettingsComponent plus(UserSettingsModule module);
    SettingsComponent plus(SettingsModule module);
}
