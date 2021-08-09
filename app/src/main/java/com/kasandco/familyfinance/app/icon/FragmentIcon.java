package com.kasandco.familyfinance.app.icon;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;

import java.util.List;

import javax.inject.Inject;

public class FragmentIcon extends Fragment implements AdapterIcon.OnClickIconListener {
    @Inject
    IconDao iconDao;
    Context context;

    List<IconModel> icons;

    AdapterIcon adapterIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        App.appComponent.inject(this);
        context =  container.getContext();
        View view = inflater.inflate(R.layout.fragment_select_icon, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button removeIcon = view.findViewById(R.id.fragment_select_icon_remove);
        removeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnSelectIcon) context).removeIcon();
            }
        });
        class Async extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                icons = iconDao.getAllIcon();
                adapterIcon = new AdapterIcon(icons, FragmentIcon.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                RecyclerView recyclerView = view.findViewById(R.id.fragment_select_icon_rv);
                recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 7, RecyclerView.VERTICAL, false));
                recyclerView.setAdapter(adapterIcon);
            }
        }
        Async async = new Async();
        async.execute();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnSelectIcon) context).closeIconFragment();
            }
        });
    }

    @Override
    public void onClickIcon(int position) {
        ((OnSelectIcon) context).onSelectIcon(icons.get(position).path);
    }

    public interface OnSelectIcon {
        void onSelectIcon(String path);
        void closeIconFragment();
        void removeIcon();
    }
}
