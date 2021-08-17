package com.kasandco.familyfinance.app.list;

import android.content.Context;

import com.kasandco.familyfinance.app.list.CreateList.FragmentCreatePresenter;
import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.network.ListNetworkInterface;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ListActivityModule {
    Context activityContext;

    public ListActivityModule(@Named("context") Context context){
        activityContext = context;
    }

    @Provides
    @Named("context")
    Context providesActivityContext(){
        return activityContext;
    }

    @Provides
    @ListActivityScope
    ListRepository providesListRepository(ListDao listDao){
        return new ListRepository(listDao);
    }

    @Provides
    @ListActivityScope
    ListDao providesListDao(AppDataBase dataBase){
        return dataBase.getListDao();
    }
}
