package com.kasandco.familyfinance.app.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    List<ItemModel> items;
    private int position;

    public ItemAdapter() {
        position=-1;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(items.get(position).getName());
        if(items.get(position).getQuantity()==null || items.get(position).getQuantity().isEmpty()){
            holder.quantity.setVisibility(View.GONE);
        }else {
            holder.quantity.setVisibility(View.VISIBLE);
            holder.quantity.setText(items.get(position).getQuantity());
        }
        if(items.get(position).getImagePath()==null || items.get(position).getImagePath().isEmpty()){
            holder.imageIcon.setVisibility(View.GONE);
        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPosition(holder.getBindingAdapterPosition());
                if (currentapiVersion >= android.os.Build.VERSION_CODES.N) {
                    holder.itemView.showContextMenu(holder.itemView.getWidth(), holder.itemView.getHeight()-3);
                } else {
                    holder.itemView.showContextMenu();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ItemModel> newList) {
        if (items.size()==0) {
            setItems(newList);
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult diffResult =
                    DiffUtil.calculateDiff(new ItemDiffUtil(items, newList));
            setItems(newList);
            diffResult.dispatchUpdatesTo(this);
        }

    }

    public void setItems(List<ItemModel> value) {
        items.clear();
        items.addAll(value);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setNewItem(ItemModel itemModel) {
        List<ItemModel> newList = new ArrayList<>();
        newList.addAll(items);
        newList.add(0, itemModel);
        updateList(newList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private ImageView imageIcon;
        private TextView name;
        private TextView quantity;
        private ImageButton btnMenu;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.rv_item_image);
            name = itemView.findViewById(R.id.rv_item_name);
            quantity = itemView.findViewById(R.id.rv_item_quantity);
            btnMenu = itemView.findViewById(R.id.rv_item_menu);
            itemView.setOnCreateContextMenuListener(this);

            itemView.setOnLongClickListener(null);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater menuInflater = new MenuInflater(view.getContext());
            menuInflater.inflate(R.menu.menu_context_item, contextMenu);
        }
    }
}
