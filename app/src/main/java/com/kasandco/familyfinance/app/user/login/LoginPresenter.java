package com.kasandco.familyfinance.app.user.login;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.user.LoginRepository;
import com.kasandco.familyfinance.app.user.registration.RegistrationPresenter;
import com.kasandco.familyfinance.core.BasePresenter;
import com.kasandco.familyfinance.network.model.UserRegisterModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter, LoginRepository.LoginCallback {
    private String email;
    private String password;
    private LoginRepository repository;

    @Inject
    public LoginPresenter(LoginRepository _repository){
        repository = _repository;
    }

    @Override
    public void viewReady(LoginContract.View view) {
        this.view = view;
    }

    @Override
    public void clickEnterBtn() {
        String[] data = view.getEnteredData();
        if (data!=null && data.length==2){
            email = data[0];
            password = data[1];
            if(validation()) {
                view.showLoading();
                login();
            }else {
                view.showToast(R.string.error_email_or_password);
            }
        }else {
            view.showToast(R.string.error_email_or_password);
            view.hideLoading();
        }
    }


    private boolean validation() {
        if(password.isEmpty() || email.isEmpty()){
            return false;
        }
        else return RegistrationPresenter.validateEmail(email);
    }

    private void login() {
        repository.login(new UserRegisterModel(email, password), this);
    }

    @Override
    public void logged(boolean isLogged, String token) {
        if (isLogged){
            if(token!=null){
                repository.saveToken(token);
                view.startListActivity();
            }else {
                view.showToast(R.string.text_loggin_error);
            }
        }else {
            view.showToast(R.string.text_loggin_error);
        }
        view.hideLoading();
    }
}
