package com.kasandco.familyfinance.app.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.BaseActivity;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

public class SettingsActivity extends BaseActivity implements FragmentColorThemeSetting.ColorThemeListener, FragmentSelectCurrency.SelectCurrencyListener {
    private Toolbar toolbar;
    private Button btnSelectCurrency, btnSelectColorTheme;
    private SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.settings_toolbar);
        btnSelectCurrency = findViewById(R.id.settings_btn_select_currency);
        btnSelectColorTheme = findViewById(R.id.settings_btn_select_color_theme);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.toolbar_menu).setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
        btnSelectCurrency.setOnClickListener(clickListenerSettings);
        btnSelectColorTheme.setOnClickListener(clickListenerSettings);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        btnSelectCurrency.setText(sharedPreferenceUtil.getSharedPreferences().getString(Constants.SHP_DEFAULT_CURRENCY,Constants.DEFAULT_CURRENCY_VALUE));
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        if(activityClass!=getClass()) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        }else{
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void showCurrencySettingFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.setting_fragment, new FragmentSelectCurrency(), "currency").commitNow();
    }


    private void showColorThemeSettingFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.setting_fragment, new FragmentColorThemeSetting(), "colorTheme").commitNow();
    }

    View.OnClickListener clickListenerSettings = view -> {
        switch (view.getId()) {
            case R.id.settings_btn_select_currency:
                showCurrencySettingFragment();
                break;
            case R.id.settings_btn_select_color_theme:
                showColorThemeSettingFragment();
                break;
        }
    };

    @Override
    public void onClickClose() {
        if(getSupportFragmentManager().findFragmentByTag("colorTheme")!=null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("colorTheme")).commitNow();
        }
        recreate();
    }

    @Override
    public void closeCurrencyFragment() {
        if(getSupportFragmentManager().findFragmentByTag("currency")!=null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("currency")).commitNow();
        }
    }

    @Override
    public void selectCurrency(String name) {
        String[] currencyType = name.split("-");
        sharedPreferenceUtil.getEditor().putString(Constants.SHP_DEFAULT_CURRENCY,currencyType[0]).apply();
        btnSelectCurrency.setText(currencyType[0]);
        closeCurrencyFragment();
    }
}