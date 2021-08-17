package com.kasandco.familyfinance.app.item.fragmentCreate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.item.ItemModel;
import com.kasandco.familyfinance.utils.ToastUtils;

public class FragmentItemCreate extends Fragment implements FragmentItemCreateContract {

    ClickListenerCreateFragment listener;
    FragmentItemCreatePresenter presenter;
    TextInputEditText name;
    ImageButton btnCreate;
    ItemModel itemEdit;

    public FragmentItemCreate(FragmentItemCreatePresenter presenter){
        App.getItemComponent(getContext()).plus(new CreateItemModule(this)).inject(this);
        this.presenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_item, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.viewReady(this);
        listener = (ClickListenerCreateFragment)view.getContext();
        name = view.findViewById(R.id.create_item_text);
        name.requestFocus();
        name.setFocusableInTouchMode(true);

        btnCreate = view.findViewById(R.id.create_item_enter);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.close();
            }
        });
        btnCreate.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.create_item_enter:
                    if (itemEdit == null) {
                        presenter.create(name.getText().toString());
                    }else {
                        presenter.edit(name.getText().toString(), itemEdit);
                        listener.close();
                    }
                    name.setText("");
                    break;
            }
        }
    };

    @Override
    public void showToast() {
        ToastUtils.showToast(getString(R.string.text_item_created), getContext());
    }

    @Override
    public void setEditedItemNull() {
        itemEdit=null;
    }

    public void edit(ItemModel item){
        String text = String.format("%s:%s", item.getName(), item.getQuantity()!=null?item.getQuantity():"");
        name.setText(text);
        name.setSelection(text.length());
        itemEdit = item;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    public interface ClickListenerCreateFragment{
        void close();
    }
}
