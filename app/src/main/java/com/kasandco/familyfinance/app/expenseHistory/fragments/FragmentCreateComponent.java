package com.kasandco.familyfinance.app.expenseHistory.fragments;

import android.app.Fragment;

import dagger.Subcomponent;

@Subcomponent(modules = FragmentCreateModule.class)
public interface FragmentCreateComponent {
    void inject(FragmentCreateCategory fragment);
}
