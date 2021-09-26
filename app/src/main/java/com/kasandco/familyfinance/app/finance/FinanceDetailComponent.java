package com.kasandco.familyfinance.app.finance;

import dagger.Subcomponent;

@Subcomponent(modules = {FinanceDetailModule.class})
@FinanceDetailScope
public interface FinanceDetailComponent {
    void inject(FinanceDetailActivity activity);
}
