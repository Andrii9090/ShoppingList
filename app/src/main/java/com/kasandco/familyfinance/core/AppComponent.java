package com.kasandco.familyfinance.core;

import com.kasandco.familyfinance.MainActivity;
import com.kasandco.familyfinance.app.expenseHistory.FinanceComponent;
import com.kasandco.familyfinance.app.expenseHistory.FinanceModule;
import com.kasandco.familyfinance.app.item.DataComponent;
import com.kasandco.familyfinance.app.item.DataModule;
import com.kasandco.familyfinance.app.item.ItemComponent;
import com.kasandco.familyfinance.app.item.ItemModule;
import com.kasandco.familyfinance.app.list.ListActivityComponent;
import com.kasandco.familyfinance.app.list.ListActivityModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AppModule.class})
@Singleton
public interface AppComponent {
    ListActivityComponent plus(ListActivityModule module);
    ItemComponent plus(ItemModule module);
    DataComponent plus(DataModule module);
    FinanceComponent plus(FinanceModule module);

    void inject(MainActivity activity);
}
