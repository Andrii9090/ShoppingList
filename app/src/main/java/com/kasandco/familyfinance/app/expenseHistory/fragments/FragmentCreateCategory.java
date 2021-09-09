package com.kasandco.familyfinance.app.expenseHistory.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.FinanceModule;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.expenseHistory.presenters.CreateCategoryContract;
import com.kasandco.familyfinance.app.expenseHistory.presenters.PresenterCreateFinanceCategory;
import com.kasandco.familyfinance.core.icon.AdapterIcon;
import com.kasandco.familyfinance.core.icon.IconDao;
import com.kasandco.familyfinance.core.icon.IconModel;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.KeyboardUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentCreateCategory extends Fragment implements CreateCategoryContract, AdapterIcon.OnClickIconListener {
    private EditText name;
    private CheckBox checkBox;
    private Button btnCreate;
    private RecyclerView recyclerView;
    private String iconPath;

    @Inject
    PresenterCreateFinanceCategory presenter;

    @Inject
    AdapterIcon adapterIcon;
    @Inject
    IconDao iconDao;

    private FinanceCategoryModel editItem;

    private int type;
    CreateFinanceCategoryListener callback;

    public FragmentCreateCategory(int type) {
        this.type = type;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        App.getAppComponent().plus(new FinanceModule(context)).plus(new FragmentCreateModule()).inject(this);
        presenter.viewReady(this);
        callback = (CreateFinanceCategoryListener) context;
    }

    public void setEditItem(FinanceCategoryModel item){
        editItem = item;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        KeyboardUtil.showKeyboard(getActivity());

        View view = inflater.inflate(R.layout.fragment_create_finance_category, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtil.hideKeyboard(getActivity());
                nullingView();
                callback.closeCreateFragment();
            }
        });
        name = view.findViewById(R.id.fragment_create_finance_cat_input_text);
        name.requestFocus();
        name.setFocusable(true);

        checkBox = view.findViewById(R.id.is_create_list);
        btnCreate = view.findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(clickListener);
        recyclerView = view.findViewById(R.id.fragment_create_finance_cat_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapterIcon);

        adapterIcon.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(positionStart);
            }
        });

        adapterIcon.setListener(this);

        iconDao.getAllIcon()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<IconModel>>() {
                    @Override
                    public void accept(List<IconModel> iconModels) throws Throwable {
                        adapterIcon.setItems(iconModels);
                        if(editItem!=null){
                            setIcon();
                        }
                    }
                });
        if (type != Constants.TYPE_COSTS) {
            checkBox.setChecked(false);
            checkBox.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(editItem != null){
            name.setText(editItem.getName());
            name.setSelection(editItem.getName().length());
            btnCreate.setText(R.string.text_edit);
            checkBox.setChecked(false);
            checkBox.setVisibility(View.GONE);
        }
    }

    private void setIcon() {
        adapterIcon.setIcon(editItem.getIconPath());
    }

    @Override
    public void getInputData() {
        presenter.inputData(editItem, name.getText().toString(), iconPath, type, checkBox.isChecked());
    }

    @Override
    public void showToast(int resource) {
        ToastUtils.showToast(getString(resource), getContext());
    }

    @Override
    public void close() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                KeyboardUtil.hideKeyboard(getActivity());
                callback.closeCreateFragment();
            }
        });
    }

    @Override
    public void nullingView() {
        name.setText("");
        checkBox.setChecked(false);
        adapterIcon.setDefaultBackground();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_create) {
                presenter.clickButtonCreate();
            }
        }
    };

    @Override
    public void onClickIcon(IconModel icon) {
        iconPath = icon.path;
    }


    public interface CreateFinanceCategoryListener{
        void closeCreateFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.destroyView();
    }
}
