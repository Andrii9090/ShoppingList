package com.kasandco.familyfinance.app.finance.core;

import com.kasandco.familyfinance.app.finance.FinanceDetailActivity;
import com.kasandco.familyfinance.app.finance.FinanceDetailModule;

import dagger.Subcomponent;

@Subcomponent(modules = {FinanceDetailModule.class})
@FinanceDetailScope
public interface FinanceDetailComponent {
    void inject(FinanceDetailActivity activity);
}
