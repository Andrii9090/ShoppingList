package com.kasandco.shoplist.app.list;

import android.app.Activity;
import android.content.Context;

import com.kasandco.shoplist.app.item.ItemDao;
import com.kasandco.shoplist.app.item.ItemRepository;
import com.kasandco.shoplist.app.list.createEditList.CreatePresenter;
import com.kasandco.shoplist.core.icon.IconDao;
import com.kasandco.shoplist.app.list.createEditList.EditListPresenter;
import com.kasandco.shoplist.app.list.createEditList.FragmentCreateList;
import com.kasandco.shoplist.app.list.createEditList.FragmentEditList;
import com.kasandco.shoplist.core.AppDataBase;
import com.kasandco.shoplist.network.ItemNetworkInterface;
import com.kasandco.shoplist.utils.NetworkConnect;
import com.kasandco.shoplist.utils.SaveImageUtils;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;
import com.kasandco.shoplist.utils.ShowCaseUtil;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ListModule {
    Context activityContext;

    public ListModule(@Named("activity_context") Context context){
        activityContext = context;
    }

    @Provides
    @Named("activity_context")
    @ListActivityScope
    Context providesActivityContext(){
        return activityContext;
    }

    @Provides
    @ListActivityScope
    FragmentEditList providesEditListFragment(EditListPresenter presenter){
        return new FragmentEditList(presenter);
    }

    @Provides
    @ListActivityScope
    ItemDao providesItemDao(AppDataBase appDataBase){
        return appDataBase.getItemDao();
    }

    @Provides
    @ListActivityScope
    ListSyncHistoryDao providesListSyncDao(AppDataBase appDataBase){
        return appDataBase.getListSyncDao();
    }

    @Provides
    @ListActivityScope
    FragmentCreateList providesCreateListFragment(CreatePresenter presenter){
        return new FragmentCreateList(presenter);
    }

    @ListActivityScope
    @Provides
    ItemRepository providesItemRepository(Retrofit retrofit, AppDataBase appDataBase, SharedPreferenceUtil sharedPreferenceUtil, NetworkConnect networkConnect, SaveImageUtils saveImage){
        return new ItemRepository(retrofit.create(ItemNetworkInterface.class), appDataBase.getItemDao(), saveImage, sharedPreferenceUtil, networkConnect, appDataBase.getItemSyncDao());
    }

    @Provides
    @ListActivityScope
    ListDao providesListDao(AppDataBase dataBase){
        return dataBase.getListDao();
    }

    @Provides
    @ListActivityScope
    IconDao provideIconDao(AppDataBase appDataBase){
        return appDataBase.getIconDao();
    }

    @Provides
    @ListActivityScope
    SaveImageUtils providesSaveImageUtils(@Named("activity_context")Context context){
        return new SaveImageUtils(context);
    }
    @Provides
    @ListActivityScope
    ShowCaseUtil providesShowcaseUtil(@Named("activity_context")Context context, SharedPreferenceUtil s){
        return new ShowCaseUtil((Activity) context, s);
    }
}
