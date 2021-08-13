package com.kasandco.familyfinance.app;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.kasandco.familyfinance.app.item.ViewModelItem;

public class ViewModelCreateFactory extends ViewModelProvider.NewInstanceFactory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass== ViewModelItem.class){
            return (T) new ViewModelItem();
        }
        return null;
    }
}
