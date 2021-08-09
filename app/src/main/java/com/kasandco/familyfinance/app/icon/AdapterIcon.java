package com.kasandco.familyfinance.app.icon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.utils.ImageBackgroundUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;

public class AdapterIcon extends RecyclerView.Adapter<AdapterIcon.ViewHolder> {
    List<IconModel> icons;
    Context context;
    Fragment fragment;

    public AdapterIcon(List<IconModel> icons, Fragment fragment) {
        this.icons = icons;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_icon_element, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Bitmap bitmap = null;
        AssetManager am = context.getAssets();
        InputStream is = null;
        OnClickIconListener listener = (OnClickIconListener) fragment;
        try {
            is = am.open(icons.get(position).path);
            bitmap = BitmapFactory.decodeStream(is);
            holder.btnIcon.setImageBitmap(bitmap);
            ImageBackgroundUtil.setBackgroundColor(holder.btnIcon, R.attr.colorPrimary);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.btnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickIcon(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton btnIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnIcon = itemView.findViewById(R.id.rv_icon_element);
        }
    }

    public interface OnClickIconListener{
        void onClickIcon(int position);
    }
}
