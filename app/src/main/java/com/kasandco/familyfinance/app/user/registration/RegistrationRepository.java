package com.kasandco.familyfinance.app.user.registration;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.network.model.UserRegisterApiModel;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistrationRepository {
    private UserNetworkInterface network;
    private SharedPreferenceUtil sharedPreference;

    @Inject
    public RegistrationRepository(Retrofit retrofit, SharedPreferenceUtil sharedPreferenceUtil){
        network = retrofit.create(UserNetworkInterface.class);
        sharedPreference = sharedPreferenceUtil;
    }

    public void createUser(UserRegisterApiModel user, UserRepositoryCallback callback){
        Call<UserRegisterApiModel> call = network.createUser(user);
        call.enqueue(new Callback<UserRegisterApiModel>() {
            @Override
            public void onResponse(Call<UserRegisterApiModel> call, Response<UserRegisterApiModel> response) {
                if(response.isSuccessful()){
                    sharedPreference.getEditor().putString(Constants.EMAIL, user.getEmail()).apply();
                    callback.createdUser(true);
                }else{
                    callback.createdUser(false);
                }
            }

            @Override
            public void onFailure(Call<UserRegisterApiModel> call, Throwable t) {
                callback.createdUser(false);
            }
        });
    }

    public interface UserRepositoryCallback{
        void createdUser(boolean isCreated);
    }
}
