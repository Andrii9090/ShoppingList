package com.kasandco.shoplist.app.item;

import android.content.Context;

import com.kasandco.shoplist.app.item.create.FragmentItemCreate;
import com.kasandco.shoplist.app.item.create.ItemCreatePresenter;
import com.kasandco.shoplist.core.AppDataBase;
import com.kasandco.shoplist.network.ItemNetworkInterface;
import com.kasandco.shoplist.utils.NetworkConnect;
import com.kasandco.shoplist.utils.SaveImageUtils;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(includes = {DataModule.class})
public class ItemModule {
    @Named("activity_context")
    Context context;

    public ItemModule(Context context){
        this.context = context;
    }

    @Provides
    FragmentItemCreate providesFragmentItemCreate(ItemCreatePresenter presenter){
        return new FragmentItemCreate(presenter);
    }

    @ItemActivityScope
    @Provides
    ItemPresenter providesPresenter(ItemRepository repository, ItemDao dao, ItemAdapter adapter, SaveImageUtils saveImageUtils){
        return new ItemPresenter(repository, adapter, saveImageUtils);
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

    @ItemActivityScope
    @Provides
    ItemRepository providesItemRepository(Retrofit retrofit, AppDataBase appDataBase, SharedPreferenceUtil sharedPreferenceUtil, NetworkConnect networkConnect, SaveImageUtils saveImage){
        return new ItemRepository(retrofit.create(ItemNetworkInterface.class), appDataBase.getItemDao(), saveImage, sharedPreferenceUtil, networkConnect, appDataBase.getItemSyncDao());
    }
}
