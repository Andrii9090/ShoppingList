package com.kasandco.familyfinance.app.user;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.network.model.ResponseUserTokenModel;
import com.kasandco.familyfinance.network.model.UserRegisterModel;
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

    public void login(UserRegisterModel user, LoginCallback callback){
        Call<ResponseUserTokenModel> call = network.login(user);
        call.enqueue(new Callback<ResponseUserTokenModel>() {
            @Override
            public void onResponse(Call<ResponseUserTokenModel> call, Response<ResponseUserTokenModel> response) {
                if(response.isSuccessful()){
                    callback.logged(true, response.body().getToken());
                }else {
                    callback.logged(false, null);
                }
            }

            @Override
            public void onFailure(Call<ResponseUserTokenModel> call, Throwable t) {
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
