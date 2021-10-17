package com.kasandco.familyfinance.app.user.login;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.BaseActivity;
import com.kasandco.familyfinance.app.list.ListActivity;
import com.kasandco.familyfinance.app.user.registration.RegistrationActivity;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    private CircularProgressIndicator loader;
    private TextInputEditText email, password, password2;
    private Button btnEnter, btnStartRegister;
    private FrameLayout frameLayout;

    @Inject
    public LoginPresenter presenter;
    private TextInputLayout password2Layout;
    private ImageButton btnNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        App.getAppComponent().plus(new LoginModule()).inject(this);
        loader = findViewById(R.id.login_loader);
        email = findViewById(R.id.login_input_email);
        password = findViewById(R.id.login_input_password);
        password2 = findViewById(R.id.login_input_password2);
        btnEnter = findViewById(R.id.login_btn_enter);
        frameLayout = findViewById(R.id.login_frame_layout);
        btnStartRegister = findViewById(R.id.login_btn_registration);
        password2Layout = findViewById(R.id.login_input_password2_layout);
        btnStartRegister.setOnClickListener(btnClickListener);
        btnEnter.setOnClickListener(btnClickListener);
        password2.setVisibility(View.GONE);
        password2Layout.setVisibility(View.GONE);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnNav = findViewById(R.id.login_nav_menu_btn);
        btnNav.setOnClickListener(btnClickListener);
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
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    public void showLoading() {
        frameLayout.setOnClickListener(null);
        frameLayout.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        frameLayout.setVisibility(View.GONE);
        loader.setVisibility(View.GONE);
    }

    @Override
    public String[] getEnteredData() {
        if (Objects.requireNonNull(email.getText()).toString().isEmpty() || Objects.requireNonNull(password.getText()).toString().isEmpty()) {
            return null;
        } else {
            return new String[]{
                    email.getText().toString(),
                    password.getText().toString()
            };
        }
    }

    @Override
    public void showToast(int resource) {
        ToastUtils.showToast(getString(resource), this);
    }

    @Override
    public void startListActivity() {
        App.recreateDagger(getApplicationContext());
        startNewActivity(ListActivity.class);
    }


    private View.OnClickListener btnClickListener = view -> {
        switch (view.getId()) {
            case R.id.login_btn_registration:
                startNewActivity(RegistrationActivity.class);
                break;
            case R.id.login_btn_enter:
                presenter.clickEnterBtn();
                break;
            case R.id.login_nav_menu_btn:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
    };
}