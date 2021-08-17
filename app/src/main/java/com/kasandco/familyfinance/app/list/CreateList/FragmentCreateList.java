package com.kasandco.familyfinance.app.list.CreateList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.icon.AdapterIcon;
import com.kasandco.familyfinance.app.icon.IconDao;
import com.kasandco.familyfinance.app.icon.IconModel;
import com.kasandco.familyfinance.app.list.ListModel;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentCreateList extends Fragment implements AdapterView.OnItemSelectedListener, CreateListContract, AdapterIcon.OnClickIconListener {

    @Inject
    FragmentCreatePresenter presenter;

    @Inject
    IconDao iconDao;

    CreateListListener createListener;
    Button btnCreate;
    EditText text;
    List<IconModel> icons;
    ListModel editList;
    Spinner statisticItem;
    RecyclerView recyclerView;
    IconModel iconModel;
    long statId;

    @Inject
    AdapterIcon adapterIcon;

    @Inject
    public FragmentCreateList() {
        App.getListActivityComponent(getContext()).plus(new FragmentCreateModule(this)).inject(this);
        icons = new ArrayList<>();
        statId = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_list_item, container, false);
        createListener = (CreateListListener) view.getContext();
        btnCreate = view.findViewById(R.id.fragment_create_list_btn_create);
        text = view.findViewById(R.id.fragment_create_list_input);
        text.setFocusable(true);
        text.requestFocus();
        statisticItem = view.findViewById(R.id.fragment_create_list_statistic);
        recyclerView = view.findViewById(R.id.fragment_create_list_recycler_view);

        List<String> category = new ArrayList<>();

        iconDao.getAllIcon().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<IconModel>>() {
                    @Override
                    public void accept(List<IconModel> iconModels) throws Throwable {
                        icons.addAll(iconModels);
                        adapterIcon.setItems(iconModels);
                        recyclerView.setAdapter(adapterIcon);
                        if(editList !=null) {
                            selectIcon(editList.getIcon());
                            btnCreate.setText(R.string.text_edit);
                        }
                    }
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        category.add(getString(R.string.text_empty_statistic_category));
        category.add(getString(R.string.create_category));
        category.add("Продукты");
        category.add("Для авто");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, category);
        statisticItem.setAdapter(arrayAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapterIcon.setListener(this);
        presenter.viewReady(this);
        //@TODO Реализовать категории расходов в этот адаптер
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createListener.closeFragmentCreate();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.create(text.getText().toString(), iconModel == null ? "" : iconModel.path, statId);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //@TODO получить инжект перезентера категории и создать новую категорию с иконкой и названием, в соответсвии с названием списка
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
    public void setEditData(ListModel listModel) {
        editList = listModel;
        if(listModel!=null) {
            text.setText(listModel.getName());
            text.setSelection(listModel.getName().length() - 1);
            statisticItem.setSelection(1);
        }
    }

    private void selectIcon(String iconId) {
        for (IconModel icon : icons) {
            if (iconId.equals(String.valueOf(icon.path))) {
                icon.setSelect(true);
                adapterIcon.notifyItemChanged(icons.indexOf(icon));
                recyclerView.scrollToPosition(icons.indexOf(icon));
                iconModel = icon;
                break;
            }
        }
    }

    @Override
    public void close() {
        text.setText("");
        changeSelected();
        adapterIcon.notifyDataSetChanged();
        createListener.closeFragmentCreate();
    }

    @Override
    public void onClickIcon(IconModel icon) {
        iconModel = icon;
        changeSelected();
        iconModel.setSelect(true);
        adapterIcon.notifyItemChanged(icons.indexOf(icon));
    }

    private void changeSelected() {
        for (IconModel icon : icons) {
            if (icon.isSelect()) {
                icon.setSelect(false);
                adapterIcon.notifyItemChanged(icons.indexOf(icon));
            }
        }
    }

    public void setEditItem(ListModel listModel) {
        presenter.setEditItem(listModel);
    }

    public interface CreateListListener {
        void closeFragmentCreate();
    }
}
