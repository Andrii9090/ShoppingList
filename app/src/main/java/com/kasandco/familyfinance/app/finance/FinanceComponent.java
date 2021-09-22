package com.kasandco.familyfinance.app.finance;

import com.kasandco.familyfinance.app.finance.fragments.FragmentCreateComponent;
import com.kasandco.familyfinance.app.finance.fragments.FragmentCreateModule;

import dagger.Subcomponent;

@Subcomponent(modules = {FinanceModule.class})
public interface FinanceComponent {
    void inject(FinanceActivity activity);

    FragmentCreateComponent plus (FragmentCreateModule module);
}
