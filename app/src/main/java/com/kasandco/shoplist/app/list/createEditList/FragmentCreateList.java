package com.kasandco.shoplist.app.list.createEditList;

import android.content.Context;

import androidx.annotation.NonNull;


public class FragmentCreateList extends BaseFragmentCreateEdit {

    public FragmentCreateList(CreateListContract.Presenter presenter) {
        super(presenter);
        this.presenter = presenter;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        presenter.viewReady(this);
    }

    @Override
    public void showToastNoInternet() {

    }
}
