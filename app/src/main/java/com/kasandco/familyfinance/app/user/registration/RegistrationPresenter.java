package com.kasandco.familyfinance.app.user.registration;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.BasePresenter;
import com.kasandco.familyfinance.network.model.UserRegisterApiModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class RegistrationPresenter extends BasePresenter<RegistrationContract.View> implements RegistrationContract.Presenter, RegistrationRepository.UserRepositoryCallback {
    private String email;
    private String password;
    private String password2;
    private RegistrationRepository repository;

    @Inject
    public RegistrationPresenter(RegistrationRepository repository){
        this.repository=repository;
    }

    @Override
    public void clickRegistrationBtn() {
        String[] data = view.getEnteredData();
        view.showLoading();
        if(data==null){
            view.showToast(R.string.error_email_or_password);
            view.hideLoading();
        }else {
            email = data[0];
            password = data[1];
            password2 = data[2];
            registration();
        }
    }

    private void registration() {
        if(validation()){
            UserRegisterApiModel user = new UserRegisterApiModel(email, password);
            repository.createUser(user, this);
        }else {
            view.showToast(R.string.text_error_password_or_email);
        }
        view.hideLoading();
    }

    private boolean validation() {
        if(password.isEmpty() || password2.isEmpty() || email.isEmpty()){
            return false;
        }

        else if(!password.equals(password2)){
            return false;
        }

        else return RegistrationPresenter.validateEmail(email);
    }

    @Override
    public void viewReady(RegistrationContract.View view) {
        this.view = view;
    }

    @Override
    public void swipeRefresh() {

    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @Override
    public void createdUser(boolean isCreated) {
        if(isCreated){
            view.showInfoDialog();
        }else {
            view.showToast(R.string.register_user_error);
        }
    }
}
