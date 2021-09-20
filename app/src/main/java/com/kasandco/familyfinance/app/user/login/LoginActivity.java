package com.kasandco.familyfinance.app.user.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.list.ListActivity;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private CircularProgressIndicator loader;
    private TextInputEditText email, password, password2;
    private Button btnEnter, btnStartRegister;
    private FrameLayout frameLayout;
    private LoginContract.Presenter presenter;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private TextInputLayout password2Layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferenceUtil = new SharedPreferenceUtil(this);
        int themeResource = sharedPreferenceUtil.getSharedPreferences().getInt(Constants.COLOR_THEME, R.style.Theme_FamilyFinance);
        setTheme(themeResource);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        presenter = new LoginPresenter();
        password2.setVisibility(View.GONE);
        password2Layout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    private View.OnClickListener btnClickListener = view -> {
        switch (view.getId()) {
            case R.id.login_btn_registration:
                //@TODO Реализовать старт активити регистрации
                break;
            case R.id.login_btn_enter:
                presenter.clickEnterBtn();
                break;
        }
    };

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
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);

    }
}