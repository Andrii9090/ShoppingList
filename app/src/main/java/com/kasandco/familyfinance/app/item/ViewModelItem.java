package com.kasandco.familyfinance.app.item;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kasandco.familyfinance.App;

import java.util.List;

import javax.inject.Inject;


public class ViewModelItem extends ViewModel {

    @Inject
    public ItemRepository repository;

    private MutableLiveData<List<ItemModel>> items;

    public MutableLiveData<List<ItemModel>> getItems() {
        App.getAppComponent().plus(new DataModule()).inject(this);
        if (items == null) {
            items = new MutableLiveData<>();
            loadData();
        }
        return items;
    }

    private void loadData() {
    }

    public void setItem(ItemModel item) {
        items.getValue().add(0, item);
        items.postValue(items.getValue());
    }

    public void remove(int position) {
        repository.remove(items.getValue().remove(position));
        items.postValue(items.getValue());
    }

    public void edit(String[] array, int position) {
        items.getValue().get(position).setName(array[0]);
        items.getValue().get(position).setQuantity(array[1]!=null?array[1]:"");
        items.postValue(items.getValue());
    }
}
