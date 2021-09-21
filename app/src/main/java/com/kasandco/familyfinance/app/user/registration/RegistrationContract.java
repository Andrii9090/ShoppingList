package com.kasandco.familyfinance.app.user.registration;

import com.kasandco.familyfinance.core.BaseContract;

public interface RegistrationContract {
    interface View extends BaseContract {
        String[] getEnteredData();
        void showInfoDialog();

        void showToast(int resource);
    }

    interface Presenter {
        void clickRegistrationBtn();
    }
}
