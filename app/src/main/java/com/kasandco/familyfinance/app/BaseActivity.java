package com.kasandco.familyfinance.app;

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
        }
        return true;
    }

    protected abstract void startNewActivity(Class<?> activityClass);
}
