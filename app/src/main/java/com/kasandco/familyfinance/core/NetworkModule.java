package com.kasandco.familyfinance.core;

import android.content.Context;

import androidx.annotation.NonNull;

import com.kasandco.familyfinance.app.item.ItemRepository;
import com.kasandco.familyfinance.utils.IsNetworkConnect;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule implements Constants {

    @Provides
    @Singleton
    Retrofit network(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

    }

    @Provides
    @Singleton
    OkHttpClient createHttpConnection(HttpLoggingInterceptor interceptor, SharedPreferenceUtil sharedPreference) {

        Interceptor interceptor1 = chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Token " + sharedPreference.getSharedPreferences().getString(Constants.TOKEN, ""))
                    .build();
            return chain.proceed(newRequest);
        };
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if(sharedPreference.getSharedPreferences().getString(Constants.TOKEN, null)!=null){
            client.addInterceptor(interceptor1);
        }
        client.addInterceptor(interceptor);
        return client.build();
    }

    @Provides
    @Singleton
    HttpLoggingInterceptor createHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    @Provides
    @Singleton
    IsNetworkConnect providesIsNetworkConnect(@Named("application.context") Context context){
        return new IsNetworkConnect(context);
    }
}