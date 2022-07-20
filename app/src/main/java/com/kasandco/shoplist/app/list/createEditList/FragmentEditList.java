package com.kasandco.shoplist.app.list.createEditList;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.list.ListModel;


public class FragmentEditList extends BaseFragmentCreateEdit implements EditListContract.View{
    private ListModel editItem;
    private EditListContract.BasePresenter presenter;

    public FragmentEditList(EditListContract.BasePresenter presenter) {
        super(presenter);
        this.presenter = presenter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        financeCategorySpinner.setEnabled(false);
        btnCreate.setText(R.string.text_edit);
    }

    public void setEditItem(ListModel editItem){
        this.editItem = editItem;
        iconPath = editItem.getIcon();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setEditItem(editItem);
    }


    @Override
    public void setIconPosition(int iconPosition) {
        recyclerViewIcons.scrollToPosition(iconPosition);
    }


    @Override
    public void setName(String name) {
        this.name.setText(name);
        this.name.setSelection(name.length());
    }

    @Override
    public void showToastNoInternet() {

    }
}
