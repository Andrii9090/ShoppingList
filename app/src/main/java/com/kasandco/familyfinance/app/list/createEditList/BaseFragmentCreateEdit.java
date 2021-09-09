package com.kasandco.familyfinance.app.list.createEditList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.icon.AdapterIcon;
import com.kasandco.familyfinance.core.icon.AdapterIcon.OnClickIconListener;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.utils.KeyboardUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.util.List;


public abstract class BaseFragmentCreateEdit extends Fragment implements AdapterView.OnItemSelectedListener, CreateEditListBaseView, OnClickIconListener {

    protected CreateEditListBasePresenter presenter;

    protected CreateListListener createListener;
    protected Button btnCreate;
    protected EditText name;
    protected Spinner financeCategorySpinner;
    protected RecyclerView recyclerView;
    protected TextView textSpinner;
    protected String iconPath;

    public BaseFragmentCreateEdit(CreateEditListBasePresenter presenter){
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_list_item, container, false);

        createListener = (CreateListListener) view.getContext();
        initElement(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        name.setFocusable(true);
        name.requestFocus();
        return view;
    }

    protected void initElement(View view) {
        btnCreate = view.findViewById(R.id.fragment_create_list_btn_create);
        name = view.findViewById(R.id.fragment_create_list_input);
        textSpinner = view.findViewById(R.id.fragment_create_list_text_stat);
        financeCategorySpinner = view.findViewById(R.id.fragment_create_list_statistic);
        recyclerView = view.findViewById(R.id.fragment_create_list_recycler_view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.viewReady(this);
        KeyboardUtil.showKeyboard(getActivity());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.clickClose();
                createListener.closeFragmentCreate();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.clickCreateBtn();
            }
        });
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
    public void setDataToSelection(List<String> names) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, names);
            financeCategorySpinner.setAdapter(arrayAdapter);
            financeCategorySpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void setRecyclerViewAdapter(AdapterIcon adapterIcon) {
        recyclerView.setAdapter(adapterIcon);
    }

    @Override
    public void getInputData() {
        presenter.setData(name.getText().toString(), iconPath);
    }

    @Override
    public String getStringResource(int resId) {
        return getStringView(resId);
    }

    protected abstract String getStringView(int resId);

    @Override
    public void clearViewData() {
        name.getText().clear();
    }

    @Override
    public void onClickIcon(IconModel icon) {
        iconPath = icon.path;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        presenter.selectedSpinner(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        presenter.selectedSpinner(0);
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
