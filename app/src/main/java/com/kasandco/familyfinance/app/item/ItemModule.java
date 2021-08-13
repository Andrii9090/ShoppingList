package com.kasandco.familyfinance.app.item;

import android.content.Context;

import com.kasandco.familyfinance.app.ViewModelCreateFactory;
import com.kasandco.familyfinance.app.item.fragmentCreate.CreateItemModule;
import com.kasandco.familyfinance.app.item.fragmentCreate.FragmentItemCreate;
import com.kasandco.familyfinance.app.item.fragmentCreate.FragmentItemCreatePresenter;
import com.kasandco.familyfinance.app.item.fragmentCreate.ItemCreateScope;
import com.kasandco.familyfinance.core.AppDataBase;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = {DataModule.class})
public class ItemModule {
    @Named("activity_context")
    Context context;

    public ItemModule(@Named("activity_context")Context context){
        this.context = context;
    }

    @Provides
    FragmentItemCreate providesFragmentItemCreate(FragmentItemCreatePresenter presenter){
        return new FragmentItemCreate(presenter);
    }

    @ItemActivityScope
    @Provides
    ItemPresenter providesPresenter(ItemRepository repository, ItemDao dao, ItemAdapter adapter){
        return new ItemPresenter(repository, dao, adapter);
    }

    @ItemActivityScope
    @Provides
    ItemAdapter provideItemAdapter(){
        return new ItemAdapter();
    }

    @ItemActivityScope
    @Provides
    Context provideContext(){
        return this.context;
    }
}
