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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;

public class AdapterIcon extends RecyclerView.Adapter<AdapterIcon.ViewHolder> {
    List<IconModel> icons;
    OnClickIconListener listener;

    @Inject
    public AdapterIcon(){
        icons = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_icon_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Bitmap bitmap = null;
        AssetManager am = holder.itemView.getContext().getAssets();
        InputStream is = null;

        if(icons.get(position).isSelect()){
            holder.btnIcon.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.selected));
        }else {
            holder.btnIcon.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
        }
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
                listener.onClickIcon(icons.get(holder.getAbsoluteAdapterPosition()));
            }
        });
    }

    public void setItems(List<IconModel> items){
        icons.addAll(items);
        notifyDataSetChanged();
    }

    public void setListener(OnClickIconListener listener){
        this.listener = listener;
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
        void onClickIcon(IconModel icon);
    }
}
