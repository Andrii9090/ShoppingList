package com.kasandco.familyfinance.app.list;

import android.content.Context;

import com.kasandco.familyfinance.app.finance.FinanceRepository;
import com.kasandco.familyfinance.app.finance.fragments.FragmentCreateItemHistory;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.finance.presenters.CreateHistoryItemPresenter;
import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.app.list.createEditList.EditListPresenter;
import com.kasandco.familyfinance.app.list.createEditList.FragmentCreatePresenter;
import com.kasandco.familyfinance.app.list.createEditList.FragmentCreateList;
import com.kasandco.familyfinance.app.list.createEditList.FragmentEditList;
import com.kasandco.familyfinance.core.AppDataBase;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ListModule {
    Context activityContext;

    public ListModule(@Named("context") Context context){
        activityContext = context;
    }

    @Provides
    @Named("context")
    Context providesActivityContext(){
        return activityContext;
    }

    @Provides
    @ListActivityScope
    ListRepository providesListRepository(ListDao listDao, AppDataBase appDataBase){
        return new ListRepository(listDao, appDataBase.getIconDao(), appDataBase.getItemDao());
    }

    @Provides
    @ListActivityScope
    FragmentEditList providesEditListFragment(EditListPresenter presenter){
        return new FragmentEditList(presenter);
    }


    @Provides
    @ListActivityScope
    FragmentCreateList providesCreateListFragment(FragmentCreatePresenter presenter){
        return new FragmentCreateList(presenter);
    }

    @Provides
    @ListActivityScope
    FinanceCategoryDao providesFinanceCategoryDao(AppDataBase appDataBase){
        return appDataBase.getFinanceCategoryDao();
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
    FinanceRepository providesFinanceRepository(AppDataBase appDataBase){
        return new FinanceRepository(appDataBase.getFinanceCategoryDao(), appDataBase.getFinanceDao(), appDataBase.getListDao());
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
}
