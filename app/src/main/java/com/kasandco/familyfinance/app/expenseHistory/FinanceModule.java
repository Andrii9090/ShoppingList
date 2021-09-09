package com.kasandco.familyfinance.app.expenseHistory;

import android.content.Context;

import com.kasandco.familyfinance.app.expenseHistory.core.FinanceActivityScope;
import com.kasandco.familyfinance.app.expenseHistory.fragments.FragmentCreateCategory;
import com.kasandco.familyfinance.app.expenseHistory.fragments.FragmentCreateItemHistory;
import com.kasandco.familyfinance.app.expenseHistory.fragments.FragmentFinanceHistory;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceDao;
import com.kasandco.familyfinance.app.expenseHistory.presenters.CreateHistoryItemPresenter;
import com.kasandco.familyfinance.app.expenseHistory.presenters.FinanceActivityPresenter;
import com.kasandco.familyfinance.app.expenseHistory.presenters.PresenterFinanceHistory;
import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.core.AppDataBase;
import com.kasandco.familyfinance.core.Constants;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
@Module
public class FinanceModule {
    @Named("activityContext")
    Context context;


    public FinanceModule(Context context){
        this.context = context;
    }

    @FinanceActivityScope
    @Provides
    @Named("activity_context") Context  providesContext(){
        return this.context;
    }

    @FinanceActivityScope
    @Provides
    FinanceCategoryDao providesFinanceCatDao(AppDataBase appDataBase){
       return appDataBase.getFinanceCategoryDao();
    }

    @FinanceActivityScope
    @Provides
    FinanceDao providesFinanceDao(AppDataBase appDataBase){
       return appDataBase.getFinanceDoa();
    }

    @Named("cost_history")
    @FinanceActivityScope
    @Provides
    FragmentCreateCategory provideCreateCategory(){
        return new FragmentCreateCategory(Constants.TYPE_COSTS);
    }

    @Named("income_history")
    @FinanceActivityScope
    @Provides
    FragmentCreateCategory provideCreateCategoryIncome(){
        return new FragmentCreateCategory(Constants.TYPE_INCOME);
    }

    @FinanceActivityScope
    @Provides
    FinanceRepository providesFinanceRepository(FinanceCategoryDao dao, FinanceDao financeDao, AppDataBase appDataBase){
        return new FinanceRepository(dao, financeDao, appDataBase.getListDao());
    }

    @Named("cost_history_fragment")
    @FinanceActivityScope
    @Provides
    FragmentFinanceHistory providesFinanceHistory(@Named("cost_presenter")PresenterFinanceHistory presenter){
        return new FragmentFinanceHistory(Constants.TYPE_COSTS, presenter);
    }
    @Named("income_history_fragment")
    @FinanceActivityScope
    @Provides
    FragmentFinanceHistory providesIncomeHistory(@Named("income_presenter") PresenterFinanceHistory presenter){
        return new FragmentFinanceHistory(Constants.TYPE_INCOME, presenter);
    }

    @FinanceActivityScope
    @Provides
    FinanceActivityPresenter providesFinanceActivityPresenter(FinanceRepository repository){
        return new FinanceActivityPresenter(repository);
    }

    @Named("cost_presenter")
    @Provides
    PresenterFinanceHistory providesPresenter(FinanceRepository financeRepository){
        return new PresenterFinanceHistory(financeRepository);
    }
    @Named("income_presenter")
    @Provides
    PresenterFinanceHistory providesPresenterIncome(FinanceRepository financeRepository){
        return new PresenterFinanceHistory(financeRepository);
    }

    @Named("cost_create")
    @Provides
    FragmentCreateItemHistory providesCreateItemHistoryCost(CreateHistoryItemPresenter presenter){
        return new FragmentCreateItemHistory(Constants.TYPE_COSTS, presenter);
    }
    @Named("income_create")
    @Provides
    FragmentCreateItemHistory providesCreateItemHistoryIncome(CreateHistoryItemPresenter presenter){
        return new FragmentCreateItemHistory(Constants.TYPE_INCOME, presenter);
    }

    @Provides
    @FinanceActivityScope
    IconDao provideIconDao(AppDataBase appDataBase){
        return appDataBase.getIconDao();
    }
}
