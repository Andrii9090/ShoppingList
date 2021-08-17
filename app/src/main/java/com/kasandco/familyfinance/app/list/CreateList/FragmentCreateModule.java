package com.kasandco.familyfinance.app.list.CreateList;

import androidx.fragment.app.Fragment;

import dagger.Module;

@Module
public class FragmentCreateModule {
    Fragment fragment;

    public FragmentCreateModule(Fragment fragment){
        this.fragment = fragment;
    }
}
