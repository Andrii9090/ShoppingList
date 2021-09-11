package com.kasandco.familyfinance.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.FinanceActivity;
import com.kasandco.familyfinance.app.list.ListActivity;
import com.kasandco.familyfinance.app.settings.SettingsActivity;
import com.kasandco.familyfinance.app.statistic.StatisticActivity;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import javax.inject.Inject;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    protected NavigationView navigationView;
    protected DrawerLayout drawerLayout;
    protected ImageButton btnUserSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(this);
        int themeResource = sharedPreferenceUtil.getSharedPreferences().getInt(Constants.COLOR_THEME, R.style.Theme_FamilyFinance);
        setTheme(themeResource);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.setNavigationItemSelectedListener(this);
        btnUserSetting = navigationView.getHeaderView(0).findViewById(R.id.navigation_drawer_btn_settings);
        btnUserSetting.setOnClickListener((view -> {
            Log.e("Test", "Tes");
        }));
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_drawer_lists:
                startNewActivity(ListActivity.class);
                break;
            case R.id.menu_drawer_finance:
                startNewActivity(FinanceActivity.class);
                break;
            case R.id.menu_drawer_stat_cost:
                startStatActivity(Constants.TYPE_COSTS);
                break;
            case R.id.menu_drawer_stat_income:
                startStatActivity(Constants.TYPE_INCOME);
                break;
            case R.id.menu_drawer_setting:
                startNewActivity(SettingsActivity.class);
                break;
        }
        return true;
    }

    private void startStatActivity(int type) {
        Intent intent = new Intent(this, StatisticActivity.class);
        intent.putExtra(Constants.STAT_TYPE, type);
        startActivity(intent);
    }

    protected abstract void startNewActivity(Class<?> activityClass);
}
