package com.kasandco.shoplist.app.list.createEditList;

import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.list.ListActivityScope;
import com.kasandco.shoplist.app.list.ListRepository;

import javax.inject.Inject;

@ListActivityScope
public class CreatePresenter extends BaseCreateEditPresenter<CreateListContract.View> implements CreateListContract.Presenter, ListRepository.IconCallback {
    @Inject
    public CreatePresenter() {

    }

    @Override
    public void create() {
        if (validate()) {
            createListItem(0);
        } else {
            view.showToast(R.string.text_name_error);
        }
    }

    @Override
    public void viewReady(CreateListContract.View view) {

    }

    @Override
    public void swipeRefresh() {

    }
}
