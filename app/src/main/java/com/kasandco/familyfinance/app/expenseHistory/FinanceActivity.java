package com.kasandco.familyfinance.app.expenseHistory;

import static com.kasandco.familyfinance.core.Constants.TYPE_COSTS;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.fragments.FragmentCreateCategory;
import com.kasandco.familyfinance.app.expenseHistory.fragments.FragmentCreateItemHistory;
import com.kasandco.familyfinance.app.expenseHistory.fragments.FragmentFinanceHistory;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.app.expenseHistory.presenters.FinanceActivityPresenter;
import com.kasandco.familyfinance.app.expenseHistory.presenters.FinanceViewContract;
import com.kasandco.familyfinance.core.Constants;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Provides;

public class FinanceActivity extends AppCompatActivity implements FragmentFinanceHistory.ClickFragmentHistory, FragmentCreateItemHistory.ClickListener, FinanceViewContract, FragmentCreateCategory.CreateFinanceCategoryListener {
    TabLayout tabLayout;

    @Named("cost_history_fragment")
    @Inject
    FragmentFinanceHistory fragmentFinanceHistory;

    @Named("income_history_fragment")
    @Inject
    FragmentFinanceHistory fragmentIncomeHistory;


    @Named("cost_history")
    @Inject
    FragmentCreateCategory fragmentCreateCostCategory;

    @Named("income_history")
    @Inject
    FragmentCreateCategory fragmentCreateIncomeCategory;

    @Inject
    FinanceActivityPresenter presenter;

    @Named("cost_create")
    @Inject
    FragmentCreateItemHistory costCreateItemFragment;

    @Named("income_create")
    @Inject
    FragmentCreateItemHistory incomeCreateItemFragment;

    ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().plus(new FinanceModule(this)).inject(this);
        setContentView(R.layout.activity_finance);
        tabLayout = findViewById(R.id.activity_extensive_tab_layout);
        viewPager = findViewById(R.id.activity_extensive_fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getLifecycle()));
        setTabLayoutMediator();
        presenter.viewReady(this);
    }

    private void setTabLayoutMediator() {
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 1:
                            tab.setText(R.string.btn_income);
                            break;
                        default:
                            tab.setText(R.string.btn_expensive);
                            break;
                    }
                }).attach();
    }

    @Override
    public void onClickAddCategory(int type) {
        presenter.clickBtnNewCategory(type);
    }

    @Override
    public void onClickScanCost(long categoryId) {

    }

    @Override
    public void onClickAddCosts(long categoryId) {
        costCreateItemFragment.setCategory(categoryId);
        costCreateItemFragment.setCallback(this);
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_full_screen, costCreateItemFragment).commitNow();
    }

    @Override
    public void onClickEdit(int type, FinanceCategoryModel financeCategoryModel) {
        if(type==TYPE_COSTS){
            fragmentCreateCostCategory.setEditItem(financeCategoryModel);
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_full_screen,fragmentCreateCostCategory).commitNow();
        }else{

        }
    }

    @Override
    public void showCreateCategoryFragment(int type) {
        if (type == TYPE_COSTS) {
            if (getSupportFragmentManager().findFragmentById(R.id.frameLayout_full_screen) != null && getSupportFragmentManager().findFragmentById(R.id.frameLayout_full_screen).isAdded()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_full_screen, fragmentCreateCostCategory).commitNow();
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_full_screen, fragmentCreateCostCategory).commitNow();
            }
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void closeCreateFragment() {
        if (fragmentCreateCostCategory.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(fragmentCreateCostCategory).commitNow();
        }
        if (fragmentCreateIncomeCategory.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(fragmentCreateIncomeCategory).commitNow();
        }
    }

    @Override
    public void hideCreateFragment() {
        presenter.clickCloseCreateFragment();
    }

    @Override
    public void closeCreateItemHistory() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).remove(costCreateItemFragment).commitNow();
    }

    class FragmentAdapter extends FragmentStateAdapter {
        public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                    return showIncomeFragment();
                case 0:
                    return showCostHistoryFragment();
            }
            return showCostHistoryFragment();
        }

        private Fragment showIncomeFragment() {
            return fragmentIncomeHistory;
        }

        private Fragment showCostHistoryFragment() {
            return fragmentFinanceHistory;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}