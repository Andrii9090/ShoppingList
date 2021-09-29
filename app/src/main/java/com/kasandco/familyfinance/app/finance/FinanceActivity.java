package com.kasandco.familyfinance.app.finance;

import static com.kasandco.familyfinance.core.Constants.TYPE_COSTS;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.BaseActivity;
import com.kasandco.familyfinance.app.finance.fragments.FragmentCreateCategory;
import com.kasandco.familyfinance.app.finance.fragments.FragmentCreateItemHistory;
import com.kasandco.familyfinance.app.finance.fragments.FragmentFinanceHistory;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.finance.presenters.FinanceActivityPresenter;
import com.kasandco.familyfinance.app.finance.presenters.FinanceViewContract;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

public class FinanceActivity extends BaseActivity implements FragmentFinanceHistory.ClickFragmentHistory, FragmentCreateItemHistory.ClickListener, FinanceViewContract, FragmentCreateCategory.CreateFinanceCategoryListener {
    TabLayout tabLayout;
    TabLayout dateTabLayout;

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
    LinearProgressIndicator loader;
    TextView textTotal;
    ImageButton btnOpenNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().plus(new FinanceModule(this)).inject(this);
        setContentView(R.layout.activity_finance);
        tabLayout = findViewById(R.id.activity_extensive_tab_layout);
        dateTabLayout = findViewById(R.id.activity_extensive_dateTab);
        viewPager = findViewById(R.id.activity_extensive_fragment);
        loader = findViewById(R.id.loading);
        drawerLayout = findViewById(R.id.finance_drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        textTotal = findViewById(R.id.activity_extensive_total);
        btnOpenNavigation = findViewById(R.id.activity_finance_btn_open_navigation);
        btnOpenNavigation.setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
        dateTabLayout.addOnTabSelectedListener(dateTabLayoutListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getLifecycle()));
        presenter.viewReady(this);
        presenter.setStatPeriod(Calendar.MONTH);
        setTabLayoutMediator();
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
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        presenter.selectCostFragment();
                        break;
                    case 1:
                        presenter.selectIncomeFragment();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.viewDestroy();
        presenter = null;
        super.onDestroy();
    }

    @Override
    public void onClickAddCategory(int type) {
        presenter.clickBtnNewCategory(type);
    }

    @Override
    public void onClickAddCosts(long categoryId, int type) {
        if (type == 1) {
            costCreateItemFragment.setCategory(categoryId);
            costCreateItemFragment.setCallback(this);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.frameLayout_full_screen, costCreateItemFragment).commitNow();
        } else {
            incomeCreateItemFragment.setCategory(categoryId);
            incomeCreateItemFragment.setCallback(this);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.frameLayout_full_screen, incomeCreateItemFragment).commitNow();
        }
    }

    @Override
    public void onClickEdit(int type, FinanceCategoryModel financeCategoryModel) {
        if (type == TYPE_COSTS) {
            fragmentCreateCostCategory.setEditItem(financeCategoryModel);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.frameLayout_full_screen, fragmentCreateCostCategory).commitNow();
        } else {
            fragmentCreateIncomeCategory.setEditItem(financeCategoryModel);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.frameLayout_full_screen, fragmentCreateIncomeCategory).commitNow();
        }
    }

    @Override
    public void onclickShowFinanceDetailActivity(long categoryId) {
        Intent intent = new Intent(this, FinanceDetailActivity.class);
        intent.putExtra(Constants.FINANCE_CATEGORY_ID, categoryId);
        startActivity(intent);
    }

    @Override
    public void showCreateCategoryFragment(int type) {
        if (type == TYPE_COSTS) {
            showFragment(fragmentCreateCostCategory);
        } else {
            showFragment(fragmentCreateIncomeCategory);
        }
    }

    private void showFragment(Fragment fragment) {
        if (getSupportFragmentManager().findFragmentById(R.id.frameLayout_full_screen) != null && getSupportFragmentManager().findFragmentById(R.id.frameLayout_full_screen).isAdded()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.frameLayout_full_screen, fragment).commitNow();
        } else {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).add(R.id.frameLayout_full_screen, fragment).commitNow();
        }
    }

    @Override
    public void showLoading() {
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        loader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void closeCreateFragment() {
        if (fragmentCreateCostCategory.isAdded()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).remove(fragmentCreateCostCategory).commitNow();
        }
        if (fragmentCreateIncomeCategory.isAdded()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).remove(fragmentCreateIncomeCategory).commitNow();
        }
    }

    @Override
    public void hideCreateFragment() {
        presenter.clickCloseCreateFragment();
    }

    @Override
    public void sendPeriodToFragment(String startDate, String endDate) {
        fragmentFinanceHistory.setPeriod(startDate, endDate);
        fragmentIncomeHistory.setPeriod(startDate, endDate);
    }

    @Override
    public void showDatePickerDialog() {
        MaterialDatePicker dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(getString(R.string.text_select_date))
                        .build();
        dateRangePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>) selection -> {
            Date startDate = new Date(selection.first);
            Date startEnd = new Date(selection.second);
            Calendar end = Calendar.getInstance();
            end.setTime(startEnd);
            end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DATE), 23, 59);
            presenter.setDateRangePeriod(String.valueOf(startDate.getTime()), String.valueOf(end.getTime().getTime()));
        });
        dateRangePicker.show(getSupportFragmentManager(), "DatePicker");
        hideLoading();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void setTotal(double total) {
        ValueAnimator animator = ValueAnimator.ofFloat(Float.parseFloat(textTotal.getText().toString().isEmpty() ? "0" : textTotal.getText().toString().replace(",", ".").split(" ")[0]), (float) total);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> textTotal.setText(String.format("%.2f %s", animation.getAnimatedValue(), new SharedPreferenceUtil(FinanceActivity.this).getSharedPreferences().getString(Constants.SHP_DEFAULT_CURRENCY, "USD"))));
        animator.start();
    }

    @Override
    public void getTabPositionSelected() {
        if (tabLayout.getSelectedTabPosition() == 1) {
            presenter.selectIncomeFragment();
        } else {
            presenter.selectCostFragment();
        }
    }

    @Override
    public void closeCreateItemHistory() {
        if (costCreateItemFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).remove(costCreateItemFragment).commitNow();
        } else {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).remove(incomeCreateItemFragment).commitNow();
        }
        costCreateItemFragment.onDestroyView();
    }

    private TabLayout.OnTabSelectedListener dateTabLayoutListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case 0:
                    presenter.setStatPeriod(Calendar.MONTH);
                    break;
                case 1:
                    presenter.setStatPeriod(Calendar.DAY_OF_YEAR);
                    break;
                case 2:
                    presenter.setStatPeriod(Calendar.DAY_OF_WEEK);
                    break;
                case 3:
                    presenter.clickSetPeriod();
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            if (tab.getPosition() == 3) {
                presenter.clickSetPeriod();
            }
        }
    };


    @Override
    protected void startNewActivity(Class activityClass) {
        if (activityClass != getClass()) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
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
            fragmentFinanceHistory.setPeriod(presenter.getDateStart(), presenter.getDateEnd());
            presenter.selectIncomeFragment();
            return fragmentIncomeHistory;
        }

        private Fragment showCostHistoryFragment() {
            fragmentIncomeHistory.setPeriod(presenter.getDateStart(), presenter.getDateEnd());
            presenter.selectCostFragment();
            return fragmentFinanceHistory;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }


}