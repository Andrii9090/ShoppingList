package com.kasandco.shoplist.app.list.createEditList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.shoplist.R;
import com.kasandco.shoplist.core.icon.AdapterIcon;
import com.kasandco.shoplist.core.icon.AdapterIcon.OnClickIconListener;
import com.kasandco.shoplist.core.icon.IconModel;
import com.kasandco.shoplist.utils.KeyboardUtil;
import com.kasandco.shoplist.utils.ToastUtils;


public abstract class BaseFragmentCreateEdit extends Fragment implements CreateEditListBaseView, OnClickIconListener {

    protected CreateEditListBasePresenter presenter;

    protected CreateListListener createListener;
    protected Button btnCreate;
    protected EditText name;
    protected Spinner financeCategorySpinner;
    protected RecyclerView recyclerViewIcons;
    protected String iconPath;

    public BaseFragmentCreateEdit(CreateEditListBasePresenter presenter) {
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_list_item, container, false);

        createListener = (CreateListListener) view.getContext();
        initElement(view);

        recyclerViewIcons.setLayoutManager(new GridLayoutManager(getContext(), 7));

        name.setFocusable(true);
        name.requestFocus();
        return view;
    }

    protected void initElement(View view) {
        btnCreate = view.findViewById(R.id.fragment_create_list_btn_create);
        name = view.findViewById(R.id.fragment_create_list_input);
        financeCategorySpinner = view.findViewById(R.id.fragment_create_list_statistic);
        recyclerViewIcons = view.findViewById(R.id.fragment_create_list_recycler_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KeyboardUtil.showKeyboard(getActivity());
        view.setOnClickListener(view1 -> {
            presenter.clickClose();
            createListener.closeFragmentCreate();
        });

        btnCreate.setOnClickListener(view12 -> presenter.clickCreateBtn());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showToast(int resourceId) {
        ToastUtils.showToast(getString(resourceId), getContext());
    }

    @Override
    public void close() {
        name.getText().clear();
        iconPath = null;
        createListener.closeFragmentCreate();
    }

    @Override
    public void setRecyclerViewAdapter(AdapterIcon adapterIcon) {
        recyclerViewIcons.setAdapter(adapterIcon);
    }

    @Override
    public void getInputData() {
        presenter.setData(name.getText().toString(), iconPath);
    }

    @Override
    public void clearViewData() {
        name.getText().clear();
    }

    @Override
    public void onClickIcon(IconModel icon) {
        iconPath = icon.path;
    }

    public interface CreateListListener {
        void closeFragmentCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroyView();
    }
}
