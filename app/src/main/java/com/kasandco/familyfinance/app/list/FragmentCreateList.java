package com.kasandco.familyfinance.app.list;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.utils.ImageBackgroundUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FragmentCreateList extends Fragment implements AdapterView.OnItemSelectedListener {
    CreateListContract createListener;
    ImageView iconView;
    ImageButton btnCreate;
    Button btnSelectIcon;
    EditText text;
    Spinner spinnerCostCategory;
    boolean isEdit;
    int costCategoryAction;
    String iconPath;

    public FragmentCreateList() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createListener = (CreateListContract) container.getContext();
        isEdit = false;
        View view = inflater.inflate(R.layout.fragment_create_list_item, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text = view.findViewById(R.id.create_list_item_text);
        btnCreate = view.findViewById(R.id.create_list_item_enter);
        iconView = view.findViewById(R.id.create_list_item_icon);
        btnSelectIcon = view.findViewById(R.id.create_list_item_select_icon);
        spinnerCostCategory = view.findViewById(R.id.create_list_item_spinner);
        spinnerCostCategory.setOnItemSelectedListener(this);

        List<String> category = new ArrayList<>();

        category.add(getString(R.string.text_empty_statistic_category));
        category.add(getString(R.string.cteate_category));
        category.add("Продукты");
        category.add("Для авто");

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, category);

        spinnerCostCategory.setAdapter(arrayAdapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEdit) {
                    createListener.create(text.getText().toString(), iconPath);
                } else {
                    createListener.edit(text.getText().toString(), iconPath);
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createListener.closeFragmentCreate();
            }
        });
        text.requestFocus();
        text.setFocusableInTouchMode(true);

        btnSelectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createListener.showSelectIconFragment();
            }
        });
    }

    public void setIcon(String path) {
        iconPath = path;
        Bitmap bitmap = null;
        AssetManager am = iconView.getContext().getAssets();
        InputStream is = null;
        try {
            is = am.open(path);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(is);
        iconView.setImageBitmap(bitmap);
        ImageBackgroundUtil.setBackgroundColor(iconView, R.attr.colorPrimary);
        if (path != null) {
            btnSelectIcon.setText(R.string.text_change_icon);
            iconView.setVisibility(View.VISIBLE);
        } else {
            btnSelectIcon.setText(R.string.select_icon);
            iconView.setVisibility(View.GONE);

        }
    }

    public void setText(String text) {
        this.text.setText(text);
        this.text.setSelection(text.length());
    }

    public void setEdit() {
        isEdit = true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        costCategoryAction = i;
        //@TODO получить инжект перезентера категории и создать новую категорию с иконкой и названием, в соответсвии с названием списка
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface CreateListContract {
        void create(String text, String iconPath);

        void edit(String text, String iconPath);

        void closeFragmentCreate();

        void showSelectIconFragment();
    }
}
