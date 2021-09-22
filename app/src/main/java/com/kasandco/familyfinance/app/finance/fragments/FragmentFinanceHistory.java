package com.kasandco.familyfinance.app.finance.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.finance.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.finance.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.finance.presenters.HistoryContract;
import com.kasandco.familyfinance.app.finance.presenters.PresenterFinanceHistory;
import com.kasandco.familyfinance.utils.ToastUtils;

import javax.inject.Inject;

public class FragmentFinanceHistory extends Fragment implements HistoryContract, FinanceCategoryAdapter.OnClickItemListener {
    private ImageButton btnAddCategory, btnAddAmount;
    private RecyclerView recyclerView;

    private PresenterFinanceHistory presenter;

    private ClickFragmentHistory callback;
    private int type;

    @Inject
    public FragmentFinanceHistory(int type, PresenterFinanceHistory presenter){
        this.type = type;
        this.presenter = presenter;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance_history, container, false);
        btnAddCategory = view.findViewById(R.id.fragment_expensive_btn_add_category);
        btnAddCategory.setOnClickListener(clickListener);

        btnAddAmount = view.findViewById(R.id.fragment_expensive_btn_add_amount);
        btnAddAmount.setOnClickListener(addHistoryItemListener);

        recyclerView = view.findViewById(R.id.fragment_expensive_recycler_view);

        this.callback = (ClickFragmentHistory) view.getContext();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.viewReady(this);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.fragment_expensive_btn_add_category:
                        callback.onClickAddCategory(type);
                    break;
            }
        }
    };

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        presenter.contextMenuClick(item.getItemId());
        return super.onContextItemSelected(item);
    }

    @Override
    public int getHistoryType() {
        return type;
    }

    @Override
    public void setAdapter(FinanceCategoryAdapter adapter) {
        adapter.setOnClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEditForm(int type, FinanceCategoryModel financeCategoryModel) {
        callback.onClickEdit(type, financeCategoryModel);
    }

    @Override
    public void unblockButtons() {
        btnAddAmount.setEnabled(true);
    }

    @Override
    public void showToast(int resource) {
        ToastUtils.showToast(getString(resource), getContext());
    }

    @Override
    public void showCreateHistoryItemForm(long id, int type) {
        callback.onClickAddCosts(id, type);
    }

    @Override
    public void clickToItem() {
        presenter.clickToCategoryItem();
    }

    private View.OnClickListener addHistoryItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                default:
                    presenter.clickToCreateNewHistoryItem();
            }
        }
    };

    public void setPeriod(String startDate, String endDate) {
        presenter.periodSet(startDate, endDate);
    }

    public interface ClickFragmentHistory {
        void onClickAddCategory(int financeType);
        void onClickAddCosts(long categoryId, int type);
        void onClickEdit(int type, FinanceCategoryModel financeCategoryModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }
}
