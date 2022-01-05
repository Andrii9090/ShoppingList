package com.kasandco.familyfinance.network;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Requests {
    public static <R> void request(Call<R> call, RequestsInterface<R> callback) {
        //I - network interface, R - response type

        call.enqueue(new Callback<R>() {
            @Override
            public void onResponse(Call<R> call, Response<R> response) {
                if(response.code()==401 || response.code()==403){
                    callback.noPermit();
                }
                else if (response.isSuccessful()) {
                    if (callback != null)
                        new Thread(() -> callback.success(response.body())).start();
                } else {
                    if (callback != null)
                        callback.error();
                }
            }

            @Override
            public void onFailure(Call<R> call, Throwable t) {
                if (callback != null)
                    callback.error();
            }
        });
    }

    public interface RequestsInterface<M> {
        void success(M responseObj);

        void error();

        void noPermit();
    }
}
