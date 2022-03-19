package com.kasandco.shoplist.app.user.login;

import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.user.LoginRepository;
import com.kasandco.shoplist.core.BasePresenter;

import javax.inject.Inject;

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter, LoginRepository.LoginCallback {
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
    public void receivedIdToken(String idToken) {
        repository.login(idToken, this);
    }

    @Override
    public void swipeRefresh() {

    }


    @Override
    public void logged(boolean isLogged, String token) {
        if (isLogged){
            if(token!=null){
                view.startListActivity();
            }else {
                view.showToast(R.string.text_login_error);
            }
        }else {
            view.showToast(R.string.text_login_error);
        }
        view.hideLoading();
    }
}
