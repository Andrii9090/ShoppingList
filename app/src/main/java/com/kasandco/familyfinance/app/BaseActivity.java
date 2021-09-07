package com.kasandco.familyfinance.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.FinanceActivity;
import com.kasandco.familyfinance.app.list.ListActivity;
import com.kasandco.familyfinance.app.statistic.StatisticActivity;
import com.kasandco.familyfinance.core.Constants;

import javax.inject.Inject;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    protected NavigationView navigationView;
    protected DrawerLayout drawerLayout;

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setNavigationItemSelectedListener(this);
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
