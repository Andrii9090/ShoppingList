package com.kasandco.familyfinance.core;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule implements Constants {

    @Provides
    Retrofit network(OkHttpClient client){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    @Provides
    OkHttpClient createHttpConnection(HttpLoggingInterceptor interceptor){
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    @Provides
    HttpLoggingInterceptor createHttpLoggingInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }
}