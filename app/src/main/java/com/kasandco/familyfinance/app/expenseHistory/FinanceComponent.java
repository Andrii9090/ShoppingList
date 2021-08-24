package com.kasandco.familyfinance.app.expenseHistory;

import com.kasandco.familyfinance.app.expenseHistory.fragments.FragmentCreateComponent;
import com.kasandco.familyfinance.app.expenseHistory.fragments.FragmentCreateModule;

import dagger.Subcomponent;

@Subcomponent(modules = {FinanceModule.class})
public interface FinanceComponent {
    void inject(FinanceActivity activity);

    FragmentCreateComponent plus (FragmentCreateModule module);
}
