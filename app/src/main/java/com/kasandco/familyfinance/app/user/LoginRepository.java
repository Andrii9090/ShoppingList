package com.kasandco.familyfinance.app.user;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.network.model.UserTokenApiModel;
import com.kasandco.familyfinance.network.model.UserRegisterApiModel;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private SharedPreferenceUtil sharedPreference;
    private UserNetworkInterface network;

    @Inject
    public LoginRepository(UserNetworkInterface userNetworkInterface, SharedPreferenceUtil sharedPreferenceUtil){
        network = userNetworkInterface;
        sharedPreference = sharedPreferenceUtil;
    }

    public void login(UserRegisterApiModel user, LoginCallback callback){
        Call<UserTokenApiModel> call = network.login(user);
        call.enqueue(new Callback<UserTokenApiModel>() {
            @Override
            public void onResponse(Call<UserTokenApiModel> call, Response<UserTokenApiModel> response) {
                if(response.isSuccessful()){
                    callback.logged(true, response.body().getToken());
                }else {
                    callback.logged(false, null);
                }
            }

            @Override
            public void onFailure(Call<UserTokenApiModel> call, Throwable t) {
                callback.logged(false, null);
            }
        });
    }

    public void saveToken(String token, String email) {
        sharedPreference.getEditor().putString(Constants.TOKEN, token).apply();
        sharedPreference.getEditor().putString(Constants.EMAIL, email).apply();
    }

    public interface LoginCallback{
        void logged(boolean isLogged, String token);
    }
}
