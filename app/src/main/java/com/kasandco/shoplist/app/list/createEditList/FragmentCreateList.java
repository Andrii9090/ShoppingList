package com.kasandco.shoplist.app.list.createEditList;

import android.content.Context;
import android.widget.AdapterView;

import androidx.annotation.NonNull;

import com.kasandco.shoplist.core.icon.AdapterIcon.OnClickIconListener;

public class FragmentCreateList extends BaseFragmentCreateEdit implements AdapterView.OnItemSelectedListener, CreateListContract.View, OnClickIconListener {

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
    protected String getStringView(int resId) {
        return getContext().getString(resId);
    }

    @Override
    public void showToastNoInternet() {

    }
}
