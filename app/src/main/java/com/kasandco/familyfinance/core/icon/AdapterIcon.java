package com.kasandco.familyfinance.core.icon;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.utils.ImageBackgroundUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = holder.btnIcon.getContext().getTheme();
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;
            ImageBackgroundUtil.setBackgroundColor(holder.btnIcon, color);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.btnIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedIconBackground(holder.getAbsoluteAdapterPosition());
                listener.onClickIcon(icons.get(holder.getAbsoluteAdapterPosition()));
            }
        });
    }

    private void setSelectedIconBackground(int absoluteAdapterPosition) {
        for (IconModel icon:icons) {
            if(icon.isSelect()){
                icon.setSelect(false);
                notifyItemChanged(icons.indexOf(icon));
            }
        }
        icons.get(absoluteAdapterPosition).setSelect(true);
        notifyItemChanged(absoluteAdapterPosition);
    }

    public void setDefaultBackground(){
        for (IconModel icon:icons) {
            if(icon.isSelect()){
                icon.setSelect(false);
                notifyItemChanged(icons.indexOf(icon));
                break;
            }
        }
    }

    public void setItems(List<IconModel> items){
        icons.clear();
        icons.addAll(items);
    }

    public void setListener(OnClickIconListener listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public void setIcon(String iconPath) {
        for (IconModel icon:icons) {
            if(icon.path.equals(iconPath)){
                icon.setSelect(true);
                notifyItemChanged(icons.indexOf(icon));
                break;
            }
        }
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
