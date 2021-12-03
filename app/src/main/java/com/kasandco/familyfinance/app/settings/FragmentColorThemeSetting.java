package com.kasandco.familyfinance.app.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.user.settings.UserSettingsModule;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.Objects;

import javax.inject.Inject;

public class FragmentColorThemeSetting extends Fragment {
    private ImageButton btnDefault, btnRose, btnGreen, btnOrange, btnBlue, btnBordo;

    @Inject
    public SharedPreferenceUtil sharedPreferenceUtil;

    public FragmentColorThemeSetting() {
        App.appComponent.plus(new UserSettingsModule()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color_theme_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnDefault=view.findViewById(R.id.color_settings_fr_default);
        btnRose=view.findViewById(R.id.color_settings_fr_rose);
        btnGreen=view.findViewById(R.id.color_settings_fr_green);
        btnOrange=view.findViewById(R.id.color_settings_fr_orange);
        btnBlue=view.findViewById(R.id.color_settings_fr_blue);
        btnBordo=view.findViewById(R.id.color_settings_fr_bordo);

        btnBlue.setOnClickListener(clickListener);
        btnDefault.setOnClickListener(clickListener);
        btnGreen.setOnClickListener(clickListener);
        btnOrange.setOnClickListener(clickListener);
        btnBordo.setOnClickListener(clickListener);
        btnRose.setOnClickListener(clickListener);
        ColorThemeListener callback = (ColorThemeListener) view.getContext();
        view.setOnClickListener((View v)-> callback.onClickClose());
    }

    private void saveColorTheme(int themeResource,  View view){
        sharedPreferenceUtil.getEditor().putInt(Constants.COLOR_THEME, themeResource).apply();
    }

    View.OnClickListener clickListener = view -> {
        switch (view.getId()){
            case R.id.color_settings_fr_rose:
                saveColorTheme(R.style.Theme_FamilyFinanceRose, view);
                break;
            case R.id.color_settings_fr_blue:
                saveColorTheme(R.style.Theme_FamilyFinanceBlue, view);
                break;
            case R.id.color_settings_fr_bordo:
                saveColorTheme(R.style.Theme_FamilyFinanceBordo, view);
                break;
            case R.id.color_settings_fr_orange:
                saveColorTheme(R.style.Theme_FamilyFinanceOrange, view);
                break;
            case R.id.color_settings_fr_green:
                saveColorTheme(R.style.Theme_FamilyFinanceGreen, view);
                break;
            default:
                saveColorTheme(R.style.Theme_FamilyFinance, view);
                break;
        }
        ColorThemeListener callback = (ColorThemeListener) view.getContext();
        callback.onClickClose();
    };

    public interface ColorThemeListener{
        void onClickClose();
    }
}