package com.kasandco.familyfinance.app.user.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.BaseActivity;
import com.kasandco.familyfinance.app.list.ListActivity;
import com.kasandco.familyfinance.app.user.login.LoginActivity;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.ToastUtils;

import javax.inject.Inject;

public class UserSettingsActivity extends BaseActivity implements UserSettingsView {

    private EditText email, oldPassword, newPassword, newPassword2;
    private Button btnSave, btnChangePassword;
    private Toolbar toolbar;
    private ImageButton btnNav;
    private LinearProgressIndicator loader;

    @Inject
    UserSettingsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        App.getAppComponent().plus(new UserSettingsModule()).inject(this);
        email = findViewById(R.id.user_settings_email);
        oldPassword = findViewById(R.id.user_settings_password);
        newPassword = findViewById(R.id.user_settings_new_password);
        newPassword2 = findViewById(R.id.user_settings_new_password2);
        loader = findViewById(R.id.user_settings_loading);
        btnSave = findViewById(R.id.user_settings_btn_save);
        btnChangePassword = findViewById(R.id.user_settings_btn_change_password);
        btnSave.setOnClickListener(listener);
        btnChangePassword.setOnClickListener(listener);
        toolbar = findViewById(R.id.user_settings_toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.user_settings_title);

        if (sharedPreferenceUtil.getSharedPreferences().getString(Constants.EMAIL, null) != null) {
            email.setText(sharedPreferenceUtil.getSharedPreferences().getString(Constants.EMAIL, ""));
        }

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        toolbar.findViewById(R.id.toolbar_menu).setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        if (activityClass != getClass()) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            presenter.clickLogOut();
        }
        return true;
    }

    private View.OnClickListener listener = view -> {
        switch (view.getId()) {
            case R.id.user_settings_btn_save:
                presenter.clickBtnSave();
                break;
            case R.id.user_settings_btn_change_password:
                presenter.clickBtnChangePassword();
                break;
        }
    };

    @Override
    public String[] getUserData() {
        String emailText = "";
        String oldPasswordText = "";
        String newPasswordText = "";
        String newPassword2Text = "";
        if (email.getText().toString() != null && !email.getText().toString().isEmpty()) {
            emailText = email.getText().toString();
        }
        if (oldPassword.getText().toString() != null && !oldPassword.getText().toString().isEmpty()) {
            oldPasswordText = oldPassword.getText().toString();
        }
        if (newPassword.getText().toString() != null && !newPassword.getText().toString().isEmpty()) {
            newPasswordText = newPassword.getText().toString();
        }
        if (newPassword2.getText().toString() != null && !newPassword2.getText().toString().isEmpty()) {
            newPassword2Text = newPassword2.getText().toString();
        }
        String[] userData = new String[]{emailText, oldPasswordText, newPasswordText, newPassword2Text};
        return userData;
    }

    @Override
    public void showToast(int recourse) {
        ToastUtils.showToast(getString(recourse), this);
    }

    @Override
    public void showNewPasswordTextInput() {
        if (email.isEnabled()) {
            newPassword.setVisibility(View.VISIBLE);
            newPassword2.setVisibility(View.VISIBLE);
            email.setEnabled(false);
        } else {
            newPassword.setVisibility(View.GONE);
            newPassword2.setVisibility(View.GONE);
            email.setEnabled(true);
        }
    }

    @Override
    public void startListActivity() {
        App.recreateDagger(getApplicationContext());
        startNewActivity(ListActivity.class);
    }

    @SuppressLint("HardwareIds")
    @Override
    public String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Override
    public void showLoader() {
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        loader.setVisibility(View.GONE);
    }
}