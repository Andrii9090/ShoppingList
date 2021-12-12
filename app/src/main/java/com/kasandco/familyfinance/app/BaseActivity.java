package com.kasandco.familyfinance.app;

import android.content.Intent;
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
import com.kasandco.familyfinance.BuildConfig;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.finance.FinanceActivity;
import com.kasandco.familyfinance.app.list.ListActivity;
import com.kasandco.familyfinance.app.settings.SettingsActivity;
import com.kasandco.familyfinance.app.statistic.StatisticActivity;
import com.kasandco.familyfinance.app.user.group.UserGroupActivity;
import com.kasandco.familyfinance.app.user.login.LoginActivity;
import com.kasandco.familyfinance.app.user.settings.UserSettingsActivity;
import com.kasandco.familyfinance.core.BaseContract;
import com.kasandco.familyfinance.core.BasePresenter;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseContract {
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
            case R.id.menu_drawer_mail:
                startEmailIntent();
                break;
            case R.id.menu_drawer_group:
                startNewActivity(UserGroupActivity.class);
                break;
            case R.id.menu_drawer_feed:
                ToastUtils.showToast("Скоро...", this);
                break;
        }
        return true;
    }

    private void startEmailIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Version " + BuildConfig.VERSION_NAME);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"webdevua2017@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_review_email));
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, getString(R.string.select_mail_app)));
    }

    protected void startStatActivity(int type) {
        Intent intent = new Intent(this, StatisticActivity.class);
        intent.putExtra(Constants.STAT_TYPE, type);
        startActivity(intent);
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
        ToastUtils.showToast(getString(R.string.internet_connection_error), this);
    }


    @Override
    public void showLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        refreshLayout.setRefreshing(false);
    }

}
