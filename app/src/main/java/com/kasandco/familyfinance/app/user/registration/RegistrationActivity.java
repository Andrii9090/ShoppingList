package com.kasandco.familyfinance.app.user.registration;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.BaseActivity;
import com.kasandco.familyfinance.app.list.ListActivity;
import com.kasandco.familyfinance.utils.ToastUtils;

import javax.inject.Inject;

public class RegistrationActivity extends BaseActivity implements RegistrationContract.View {

    private CircularProgressIndicator loader;
    private TextInputEditText email, password, password2;
    private Button btnEnter, btnStartRegister;
    private FrameLayout frameLayout;

    @Inject
    public RegistrationPresenter presenter;

    private ImageButton btnNav;
    private TextView registrationTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        App.getAppComponent().plus(new RegistrationModule()).inject(this);

        loader = findViewById(R.id.login_loader);
        email = findViewById(R.id.login_input_email);
        password = findViewById(R.id.login_input_password);
        password2 = findViewById(R.id.login_input_password2);
        btnEnter = findViewById(R.id.login_btn_enter);
        frameLayout = findViewById(R.id.login_frame_layout);
        btnStartRegister = findViewById(R.id.login_btn_registration);

        registrationTextView = findViewById(R.id.login_registration_text);
        btnStartRegister.setVisibility(View.GONE);
        registrationTextView.setVisibility(View.GONE);

        btnEnter.setOnClickListener(btnClickListener);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnNav = findViewById(R.id.login_nav_menu_btn);
        btnNav.setOnClickListener(btnClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
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

    private View.OnClickListener btnClickListener = view -> {
        switch (view.getId()) {
            case R.id.login_btn_enter:
                if(isInternetAvailable()) {
                    presenter.clickRegistrationBtn();
                }else{
                    showToast(R.string.internet_connection_error);
                }
                break;
            case R.id.login_nav_menu_btn:
                drawerLayout.openDrawer(Gravity.LEFT);
        }
    };

    @Override
    public String[] getEnteredData() {
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || password2.getText().toString().isEmpty()){
            return null;
        }else{
            return new String[]{
                    email.getText().toString(),
                    password.getText().toString(),
                    password2.getText().toString(),
            };
        }
    }

    @Override
    public void showInfoDialog() {
        DialogInterface.OnClickListener dialogBtnListener = (dialogInterface, i) -> {
            if(i==AlertDialog.BUTTON_POSITIVE){
                ToastUtils.showToast(getString(R.string.app_name), RegistrationActivity.this);
            }else {
                startNewActivity(ListActivity.class);
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.confirmation_code_dialog_title)
                .setMessage(R.string.confirmation_code_dialog_msg)
                .setPositiveButton(R.string.confirmation_code_dialog_btn_enter_code, dialogBtnListener)
                .setNegativeButton(R.string.confirmation_code_dialog_btn_cancel, dialogBtnListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showToast(int resource) {
        ToastUtils.showToast(getString(resource), this);
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
}
