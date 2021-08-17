package com.kasandco.familyfinance.app.list.CreateList;

import androidx.fragment.app.Fragment;

import dagger.Subcomponent;

@Subcomponent(modules = {FragmentCreateModule.class})
public interface FragmentCreateListComponent {
    void inject(Fragment fragment);
}
