package com.kasandco.familyfinance.app.user.group;

import com.kasandco.familyfinance.core.BaseContract;

import java.util.List;

public interface ContractUserGroup {
    interface View extends BaseContract {
        void setDataToAdapter(List<String> users);

        void showToastErrorRemoveUser();

        void userRemoved(String email);

        void showDialogNotRegisterUser();

        void showToast(int text_error_load_group);

        void noMainUser();

        void showLoading(boolean isShow);
    }

    interface Presenter {
        void removeUser(int position);
    }
}
