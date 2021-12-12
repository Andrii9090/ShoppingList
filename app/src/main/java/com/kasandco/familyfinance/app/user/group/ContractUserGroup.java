package com.kasandco.familyfinance.app.user.group;

import com.kasandco.familyfinance.core.BaseContract;

import java.util.List;

public interface ContractUserGroup {
    interface View extends BaseContract {
        void setGroupName(String groupName);

        void setDataToAdapter(List<String> users);

        void showToastErrorRemoveUser();

        void userRemoved(String email);
    }

    interface Presenter {
        void removeUser(int position);
    }
}
