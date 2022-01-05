package com.kasandco.familyfinance.app.finance.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.kasandco.familyfinance.app.finance.presenters.FinanceCategoryContract;
import com.kasandco.familyfinance.app.finance.presenters.PresenterFinanceCategory;
import com.kasandco.familyfinance.utils.ToastUtils;

import javax.inject.Inject;

public class FragmentFinanceCategory extends Fragment implements FinanceCategoryContract {
    private ImageButton btnAddCategory, btnReloadData;
    private RecyclerView recyclerView;

    private PresenterFinanceCategory presenter;

    private ClickFragmentHistory callback;
    private int type;

    @Inject
    public FragmentFinanceCategory(int type, PresenterFinanceCategory presenter) {
        this.type = type;
        this.presenter = presenter;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finance_category, container, false);
        btnAddCategory = view.findViewById(R.id.fragment_expensive_btn_add_category);
        btnAddCategory.setOnClickListener(clickListener);
        btnReloadData = view.findViewById(R.id.fragment_expensive_btn_update);
        btnReloadData.setOnClickListener(clickListener);

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
            switch (view.getId()) {
                case R.id.fragment_expensive_btn_add_category:
                    callback.onClickAddCategory(type);
                    break;
                case R.id.fragment_expensive_btn_update:
                    callback.reloadTotal();
                    presenter.clickReloadData();
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
    public void showToastNoInternet() {

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (isVisible()) {
            switch (item.getItemId()) {
                case R.id.context_finance_category_edit:
                    presenter.clickEdit();
                    break;
                case R.id.context_finance_category_detail:
                    presenter.clickDetail();
                    break;
                case R.id.context_finance_category_remove:
                    presenter.clickCategoryRemove();
                    break;
                case R.id.context_finance_category_create_new:
                    presenter.clickToCreateNewHistoryItem();
                    break;
                case R.id.context_finance_category_private:
                    presenter.clickPrivate();
                    break;
            }
            return super.onContextItemSelected(item);
        } else {
            return false;
        }
    }

    @Override
    public int getHistoryType() {
        return type;
    }

    @Override
    public void setAdapter(FinanceCategoryAdapter adapter) {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showEditForm(int type, FinanceCategoryModel financeCategoryModel) {
        callback.onClickEdit(type, financeCategoryModel);
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
    public void startFinanceDetailActivity(FinanceCategoryModel category) {
        callback.onclickShowFinanceDetailActivity(category.getId());
    }

    @Override
    public void showDialogRemove(int position) {
        DialogInterface.OnClickListener dialogListener = (dialogInterface, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                presenter.selectRemoveList(position);

            } else {
                dialogInterface.cancel();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setMessage(R.string.delete_dialog)
                .setPositiveButton(R.string.text_positive_btn, dialogListener)
                .setNegativeButton(R.string.text_negative_btn, dialogListener);

        builder.show();
    }

    @Override
    public void reloadTotal() {
        callback.reloadTotal();
    }

    @Override
    public void animateBtnReload(boolean animate) {
        btnReloadData.animate().rotation(360).setDuration(600);
        btnReloadData.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                btnReloadData.setRotation(0f);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    public void setPeriod(String startDate, String endDate) {
        presenter.periodSet(startDate, endDate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    public interface ClickFragmentHistory {
        void onClickAddCategory(int financeType);

        void onClickAddCosts(long categoryId, int type);

        void onClickEdit(int type, FinanceCategoryModel financeCategoryModel);

        void onclickShowFinanceDetailActivity(long categoryId);

        void reloadTotal();
    }
}
