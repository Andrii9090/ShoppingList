package com.kasandco.familyfinance.app.finance;

import android.content.Context;

import com.kasandco.familyfinance.app.finance.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.finance.core.FinanceActivityScope;
import com.kasandco.familyfinance.app.finance.fragments.FragmentCreateCategory;
import com.kasandco.familyfinance.app.finance.fragments.FragmentCreateItemHistory;
import com.kasandco.familyfinance.app.finance.fragments.FragmentFinanceHistory;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryDao;
import com.kasandco.familyfinance.app.finance.models.FinanceDao;
import com.kasandco.familyfinance.app.finance.presenters.CreateHistoryItemPresenter;
import com.kasandco.familyfinance.app.finance.presenters.FinanceActivityPresenter;
import com.kasandco.familyfinance.app.finance.presenters.PresenterFinanceHistory;
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


    public FinanceModule(Context context) {
        this.context = context;
    }

    @FinanceActivityScope
    @Provides
    @Named("activity_context")
    Context providesContext() {
        return this.context;
    }

    @FinanceActivityScope
    @Provides
    FinanceCategoryDao providesFinanceCatDao(AppDataBase appDataBase) {
        return appDataBase.getFinanceCategoryDao();
    }

    @FinanceActivityScope
    @Provides
    FinanceDao providesFinanceDao(AppDataBase appDataBase) {
        return appDataBase.getFinanceDao();
    }

    @Named("cost_history")
    @FinanceActivityScope
    @Provides
    FragmentCreateCategory provideCreateCategory() {
        return new FragmentCreateCategory(Constants.TYPE_COSTS);
    }

    @Named("income_history")
    @FinanceActivityScope
    @Provides
    FragmentCreateCategory provideCreateCategoryIncome() {
        return new FragmentCreateCategory(Constants.TYPE_INCOME);
    }

    @FinanceActivityScope
    @Provides
    FinanceRepository providesFinanceRepository(FinanceCategoryDao dao, FinanceDao financeDao, AppDataBase appDataBase) {
        return new FinanceRepository(dao, financeDao, appDataBase.getListDao());
    }

    @Named("cost_history_fragment")
    @FinanceActivityScope
    @Provides
    FragmentFinanceHistory providesFinanceHistory(@Named("cost_presenter") PresenterFinanceHistory presenter) {
        return new FragmentFinanceHistory(Constants.TYPE_COSTS, presenter);
    }

    @Named("income_history_fragment")
    @FinanceActivityScope
    @Provides
    FragmentFinanceHistory providesIncomeHistory(@Named("income_presenter") PresenterFinanceHistory presenter) {
        return new FragmentFinanceHistory(Constants.TYPE_INCOME, presenter);
    }

    @FinanceActivityScope
    @Provides
    FinanceActivityPresenter providesFinanceActivityPresenter(FinanceRepository repository) {
        return new FinanceActivityPresenter(repository);
    }

    @Named("cost_presenter")
    @Provides
    @FinanceActivityScope
    PresenterFinanceHistory providesPresenter(FinanceRepository financeRepository, @Named("cost_adapter")FinanceCategoryAdapter adapter) {
        return new PresenterFinanceHistory(financeRepository, adapter);
    }

    @Named("income_presenter")
    @Provides
    @FinanceActivityScope
    PresenterFinanceHistory providesPresenterIncome(FinanceRepository financeRepository, @Named("income_adapter") FinanceCategoryAdapter adapter) {
        return new PresenterFinanceHistory(financeRepository, adapter);
    }

    @Named("income_adapter")
    @Provides
    @FinanceActivityScope
    FinanceCategoryAdapter providesIncomeAdapter() {
        return new FinanceCategoryAdapter();
    }

    @Named("cost_adapter")
    @Provides
    @FinanceActivityScope
    FinanceCategoryAdapter providesCostAdapter() {
        return new FinanceCategoryAdapter();
    }

    @Named("cost_create")
    @Provides
    FragmentCreateItemHistory providesCreateItemHistoryCost(CreateHistoryItemPresenter presenter) {
        return new FragmentCreateItemHistory(Constants.TYPE_COSTS, presenter);
    }

    @Named("income_create")
    @Provides
    FragmentCreateItemHistory providesCreateItemHistoryIncome(CreateHistoryItemPresenter presenter) {
        return new FragmentCreateItemHistory(Constants.TYPE_INCOME, presenter);
    }

    @Provides
    @FinanceActivityScope
    IconDao provideIconDao(AppDataBase appDataBase) {
        return appDataBase.getIconDao();
    }
}
