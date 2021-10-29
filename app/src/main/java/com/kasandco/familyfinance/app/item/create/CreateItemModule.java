package com.kasandco.familyfinance.app.item.create;

import androidx.fragment.app.Fragment;

import dagger.Module;
import dagger.Provides;

@Module
public class CreateItemModule {
    Fragment fragment;

    public CreateItemModule(Fragment fragment){
        this.fragment = fragment;
    }

    @ItemCreateScope
    @Provides
    ItemCreatePresenter providesPresenter(){
        return new ItemCreatePresenter();
    }
}
