package com.kasandco.familyfinance.app.list;

import com.kasandco.familyfinance.core.BaseContract;

public interface ListContract extends BaseContract {
    void addAdapter(ListRvAdapter adapter);
    void showEmptyText(boolean isShow);
    void showToast(int resource);
    void showCreateFragment();
    void showEditFragment(ListModel listModel);
    void showActivityDetails(ListModel listModel);
    void showCreateItemHistoryFragment();
    void setCategoryId(long financeCategoryId);
    void runSendIntent(String text);
    String getStringResource(int resource);
    String getDeviceId();
}
