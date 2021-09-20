package com.kasandco.familyfinance.app.user.login;


import com.kasandco.familyfinance.core.BaseContract;

interface LoginContract {
    interface View extends BaseContract {
        String[] getEnteredData();
        void showToast(int resource);

        void startListActivity();
    }

     interface Presenter {
         void viewReady(LoginContract.View view);

         void clickEnterBtn();


     }
}
