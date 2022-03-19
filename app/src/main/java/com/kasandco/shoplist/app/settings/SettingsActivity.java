package com.kasandco.shoplist.app.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;

import com.kasandco.shoplist.R;
import com.kasandco.shoplist.App;
import com.kasandco.shoplist.app.BaseActivity;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;

import java.util.Objects;

import javax.inject.Inject;

public class SettingsActivity extends BaseActivity implements FragmentColorThemeSetting.ColorThemeListener{
    private Toolbar toolbar;
    private Button btnSelectColorTheme;

    @Inject
    SettingsRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().plus(new SettingsModule()).inject(this);
        setContentView(R.layout.activity_settings);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.settings_toolbar);
        btnSelectColorTheme = findViewById(R.id.settings_btn_select_color_theme);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.toolbar_menu).setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
        btnSelectColorTheme.setOnClickListener(clickListenerSettings);
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        if(activityClass!=getClass()) {
            Intent intent = new Intent(this, activityClass);
            repository.saveSettingsToServer();
            startActivity(intent);
        }else{
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void showColorThemeSettingFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.setting_fragment, new FragmentColorThemeSetting(), "colorTheme").commitNow();
    }

    View.OnClickListener clickListenerSettings = view -> {
        switch (view.getId()) {
            case R.id.settings_btn_select_color_theme:
                showColorThemeSettingFragment();
                break;
        }
    };

    @Override
    public void onClickClose() {
        if(getSupportFragmentManager().findFragmentByTag("colorTheme")!=null) {
            getSupportFragmentManager().beginTransaction().remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("colorTheme"))).commitNow();
        }
        recreate();
    }
}