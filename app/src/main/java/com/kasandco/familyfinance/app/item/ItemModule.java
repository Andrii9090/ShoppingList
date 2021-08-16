package com.kasandco.familyfinance.app.item;

import android.content.Context;

import com.kasandco.familyfinance.app.ViewModelCreateFactory;
import com.kasandco.familyfinance.app.item.fragmentCreate.CreateItemModule;
import com.kasandco.familyfinance.app.item.fragmentCreate.FragmentItemCreate;
import com.kasandco.familyfinance.app.item.fragmentCreate.FragmentItemCreatePresenter;
import com.kasandco.familyfinance.app.item.fragmentCreate.ItemCreateScope;
import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.utils.SaveImageUtils;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = {DataModule.class})
public class ItemModule {
    @Named("activity_context")
    Context context;

    public ItemModule(Context context){
        this.context = context;
    }

    @Provides
    FragmentItemCreate providesFragmentItemCreate(FragmentItemCreatePresenter presenter){
        return new FragmentItemCreate(presenter);
    }

    @ItemActivityScope
    @Provides
    ItemPresenter providesPresenter(ItemRepository repository, ItemDao dao, ItemAdapter adapter, SaveImageUtils saveImageUtils){
        return new ItemPresenter(repository, dao, adapter, saveImageUtils);
    }

    @Provides
    SaveImageUtils providesSaveImageUtils(@Named("activity_context")Context context){
        return new SaveImageUtils(context);
    }

    @ItemActivityScope
    @Provides
    ItemAdapter provideItemAdapter(){
        return new ItemAdapter();
    }

    @ItemActivityScope
    @Provides
    @Named("activity_context")
    Context provideContext(){
        return this.context;
    }
}
