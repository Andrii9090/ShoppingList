package com.kasandco.familyfinance.app.user.registration;

import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.network.UserNetworkInterface;
import com.kasandco.familyfinance.network.model.UserRegisterModel;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.ResourceBundle;

import javax.inject.Inject;

import okhttp3.ResponseBody;
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

    public void createUser(UserRegisterModel user, UserRepositoryCallback callback){
        Call<UserRegisterModel> call = network.createUser(user);
        call.enqueue(new Callback<UserRegisterModel>() {
            @Override
            public void onResponse(Call<UserRegisterModel> call, Response<UserRegisterModel> response) {
                if(response.isSuccessful()){
                    sharedPreference.getEditor().putString(Constants.EMAIL, user.getEmail()).apply();
                    callback.createdUser(true);
                }else{
                    callback.createdUser(false);
                }
            }

            @Override
            public void onFailure(Call<UserRegisterModel> call, Throwable t) {
                callback.createdUser(false);
            }
        });
    }

    public interface UserRepositoryCallback{
        void createdUser(boolean isCreated);
    }
}
