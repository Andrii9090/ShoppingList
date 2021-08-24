package com.kasandco.familyfinance.app.expenseHistory.fragments;

import android.content.Context;
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
import com.kasandco.familyfinance.app.expenseHistory.adapters.FinanceCategoryAdapter;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryModel;
import com.kasandco.familyfinance.app.expenseHistory.presenters.HistoryContract;
import com.kasandco.familyfinance.app.expenseHistory.presenters.PresenterFinanceHistory;
import com.kasandco.familyfinance.utils.ToastUtils;

public class FragmentFinanceHistory extends Fragment implements HistoryContract, FinanceCategoryAdapter.OnClickItemListener {
    private ImageButton btnAddCategory, btnScan, btnAddAmount;
    private RecyclerView recyclerView;

    private PresenterFinanceHistory presenter;

    private ClickFragmentHistory callback;
    private int type;

    public FragmentFinanceHistory(int type, PresenterFinanceHistory presenter){
        this.type = type;
        this.presenter = presenter;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        presenter.viewReady(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance_history, container, false);
        btnAddCategory = view.findViewById(R.id.fragment_expensive_btn_add_category);
        btnAddCategory.setOnClickListener(clickListener);

        btnScan = view.findViewById(R.id.fragment_expensive_btn_scan);
        btnAddAmount = view.findViewById(R.id.fragment_expensive_btn_add_amount);
        btnScan.setOnClickListener(addHistoryItemListener);
        btnAddAmount.setOnClickListener(addHistoryItemListener);

        recyclerView = view.findViewById(R.id.fragment_expensive_recycler_view);

        this.callback = (ClickFragmentHistory) view.getContext();

        return view;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.fragment_expensive_btn_add_category:
                        callback.onClickAddCategory(type);
                    break;
                case R.id.fragment_expensive_btn_scan:

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
        btnScan.setEnabled(true);
        btnAddAmount.setEnabled(true);
    }

    @Override
    public void showToast(int resource) {
        ToastUtils.showToast(getString(resource), getContext());
    }

    @Override
    public void showCreateHistoryItemForm(long id) {
        callback.onClickAddCosts(id);
    }

    @Override
    public void clickToItem() {
        presenter.clickToCategoryItem();
    }

    private View.OnClickListener addHistoryItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.fragment_expensive_btn_scan:
                    break;
                default:
                    presenter.clickToCreateNewHistoryItem();
            }
        }
    };

    public interface ClickFragmentHistory {
        void onClickAddCategory(int financeType);
        void onClickScanCost(long categoryId);
        void onClickAddCosts(long categoryId);
        void onClickEdit(int type, FinanceCategoryModel financeCategoryModel);

    }
}
