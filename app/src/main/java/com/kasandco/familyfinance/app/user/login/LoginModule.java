package com.kasandco.familyfinance.app.user.login;

import com.kasandco.familyfinance.network.UserNetworkInterface;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class LoginModule {

    @Provides
    @LoginScope
    UserNetworkInterface providesUserNetworkInterface(Retrofit retrofit){
        return retrofit.create(UserNetworkInterface.class);
    }
}
