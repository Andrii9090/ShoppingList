package com.kasandco.familyfinance.app.list;

import android.content.Context;

import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.fragments.FragmentCreateItemHistory;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.finance.presenters.CreateHistoryItemPresenter;
import com.kasandco.familyfinance.app.item.ItemDao;
import com.kasandco.familyfinance.app.item.ItemRepository;
import com.kasandco.familyfinance.app.list.createEditList.CreatePresenter;
import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.app.list.createEditList.EditListPresenter;
import com.kasandco.familyfinance.app.list.createEditList.FragmentCreateList;
import com.kasandco.familyfinance.app.list.createEditList.FragmentEditList;
import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.network.ItemNetworkInterface;
import com.kasandco.familyfinance.utils.NetworkConnect;
import com.kasandco.familyfinance.utils.SaveImageUtils;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

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

    @Provides
    @ListActivityScope
    FinanceCategoryDao providesFinanceCategoryDao(AppDataBase appDataBase){
        return appDataBase.getFinanceCategoryDao();
    }


    @ListActivityScope
    @Provides
    ItemRepository providesItemRepository(Retrofit retrofit, AppDataBase appDataBase, SharedPreferenceUtil sharedPreferenceUtil, NetworkConnect networkConnect, SaveImageUtils saveImage){
        return new ItemRepository(retrofit.create(ItemNetworkInterface.class), appDataBase.getItemDao(), saveImage, sharedPreferenceUtil, networkConnect, appDataBase.getItemSyncDao());
    }

    @Provides
    @ListActivityScope
    CreateHistoryItemPresenter providesCreateItemHistoryPresenter(FinanceRepository repository){
        return new CreateHistoryItemPresenter(repository);
    }

    @Provides
    @ListActivityScope
    FragmentCreateItemHistory providesCreateItemHistory(CreateHistoryItemPresenter presenter){
        return new FragmentCreateItemHistory(1, presenter);
    }

    @Provides
    @ListActivityScope
    FinanceRepository providesFinanceRepository(AppDataBase appDataBase, Retrofit retrofit, SharedPreferenceUtil sharedPreference, NetworkConnect networkConnect){
        return new FinanceRepository(appDataBase, retrofit, sharedPreference, networkConnect);
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
}
