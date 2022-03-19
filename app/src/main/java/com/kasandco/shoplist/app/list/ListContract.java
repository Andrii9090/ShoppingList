package com.kasandco.shoplist.app.list;

import com.kasandco.shoplist.core.BaseContract;

public interface ListContract extends BaseContract {
    void addAdapter(ListRvAdapter adapter);
    void showEmptyText(boolean isShow);
    void showToast(int resource);
    void showCreateFragment();
    void showEditFragment(ListModel listModel);
    void showActivityDetails(ListModel listModel);
    void runSendIntent(String text);
    String getStringResource(int resource);
    String getDeviceId();
}
