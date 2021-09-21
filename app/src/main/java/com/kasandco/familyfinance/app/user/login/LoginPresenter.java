package com.kasandco.familyfinance.app.user.login;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.BasePresenter;

import javax.inject.Inject;

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter{
    private String email;
    private String password;


    @Inject
    public LoginPresenter(){

    }

    @Override
    public void viewReady(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void clickEnterBtn() {
        String[] data = view.getEnteredData();
        if (data!=null){
            view.showLoading();
            email = data[0];
            password = data[1];
            login();
        }else {
            view.showToast(R.string.error_email_or_password);
        }
    }

    private void login() {
        try {
            Thread.sleep(5000);
            view.startListActivity();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
