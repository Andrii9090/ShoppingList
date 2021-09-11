package com.kasandco.familyfinance.app.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kasandco.familyfinance.R;

public class FragmentSelectCurrency extends Fragment {
    SelectCurrencyListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callback = (SelectCurrencyListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_currency, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ListView listView = view.findViewById(R.id.fragment_currency_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1);
        adapter.addAll(getResources().getStringArray(R.array.currency));

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((AdapterView.OnItemClickListener) (adapterView, v, i, l) -> callback.selectCurrency(adapterView.getAdapter().getItem(i).toString()));

        view.setOnClickListener((View v)->{
            callback.closeCurrencyFragment();
        });
    }

    public interface SelectCurrencyListener{
        void closeCurrencyFragment();
        void selectCurrency(String name);
    }
}
