package com.kasandco.familyfinance.app.finance.fragments;

import dagger.Subcomponent;

@Subcomponent(modules = FragmentCreateModule.class)
public interface FragmentCreateComponent {
    void inject(FragmentCreateCategory fragment);
}
