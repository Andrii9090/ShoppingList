package com.kasandco.shoplist.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.list.ListActivity;
import com.kasandco.shoplist.app.pro.ProActivity;
import com.kasandco.shoplist.app.settings.SettingsActivity;
import com.kasandco.shoplist.app.user.group.UserGroupActivity;
import com.kasandco.shoplist.app.user.login.LoginActivity;
import com.kasandco.shoplist.app.user.settings.UserSettingsActivity;
import com.kasandco.shoplist.app.user.settings.UserSettingsRepository;
import com.kasandco.shoplist.core.BaseContract;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.utils.SharedPreferenceUtil;
import com.kasandco.shoplist.utils.ToastUtils;


public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseContract, UserSettingsRepository.UserSettingsRepositoryCallback {
    protected NavigationView navigationView;
    protected DrawerLayout drawerLayout;
    protected ImageButton btnUserSetting;
    protected Button btnLogin;
    protected TextView userEmail;
    protected SharedPreferenceUtil sharedPreferenceUtil;
    protected SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        int themeResource = sharedPreferenceUtil.getSharedPreferences().getInt(Constants.COLOR_THEME, R.style.Theme_FamilyFinance);
        setTheme(themeResource);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigationView.setNavigationItemSelectedListener(this);
        btnUserSetting = navigationView.getHeaderView(0).findViewById(R.id.navigation_drawer_btn_settings);
        btnLogin = navigationView.getHeaderView(0).findViewById(R.id.nav_header_login);

        userEmail = navigationView.getHeaderView(0).findViewById(R.id.navigation_drawer_email);
        if (sharedPreferenceUtil.getSharedPreferences().getString(Constants.TOKEN, null) != null) {
            btnLogin.setText(R.string.settings_user);
            userEmail.setText(sharedPreferenceUtil.getSharedPreferences().getString(Constants.EMAIL, ""));
        }
        if (sharedPreferenceUtil.getSharedPreferences().getString(Constants.USER_NAME, "").isEmpty()) {
            btnUserSetting.setVisibility(View.GONE);
            userEmail.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setOnClickListener(clickListener);
        } else {
            btnUserSetting.setVisibility(View.VISIBLE);
            userEmail.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_drawer_lists:
                startNewActivity(ListActivity.class);
                break;
            case R.id.menu_drawer_setting:
                startNewActivity(SettingsActivity.class);
                break;
            case R.id.menu_drawer_group:
                startNewActivity(UserGroupActivity.class);
                break;
                case R.id.menu_drawer_pro:
                    if (!sharedPreferenceUtil.isLogged()) {
                        AlertDialog dialogAlert = new AlertDialog.Builder(this).setMessage(R.string.pro_not_register)
                                .setTitle(R.string.registration)
                                .setPositiveButton("OK", (dialog, which) -> {
                                    startNewActivity(LoginActivity.class);
                                })
                                .setOnCancelListener(dialog -> {
                                    startNewActivity(LoginActivity.class);
                                })
                                .create();
                        dialogAlert.show();

                    }else {
                        startNewActivity(ProActivity.class);
                    }
                break;
            case R.id.menu_drawer_feed:
                openPlayMarket();
                break;
        }
        return true;
    }

    private void openPlayMarket() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    protected abstract void startNewActivity(Class<?> activityClass);

    private View.OnClickListener clickListener = (view -> {
        switch (view.getId()) {
            case R.id.nav_header_login:
                if (sharedPreferenceUtil.getSharedPreferences().getString(Constants.TOKEN, null) != null) {
                    startNewActivity(UserSettingsActivity.class);
                } else {
                    startNewActivity(LoginActivity.class);
                }
                break;
            case R.id.navigation_drawer_btn_settings:
                break;
        }
    });

    @Override
    public void showToastNoInternet() {
        ToastUtils.showToast(getString(R.string.text_error_connect_to_internet), this);
    }


    @Override
    public void showLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void dataCleared() {

    }

    @Override
    public void uid(String uid) {

    }
}
