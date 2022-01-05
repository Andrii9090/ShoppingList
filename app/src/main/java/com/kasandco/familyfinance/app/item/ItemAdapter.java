package com.kasandco.familyfinance.app.item;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    List<ItemModel> items;
    private int position;
    private ShowZoomImage zoomImageListener;

    public ItemAdapter() {
        position = -1;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        zoomImageListener = (ShowZoomImage) parent.getContext();
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(items.get(position).getName());
        if (items.get(position).getStatus() == 0) {
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.inActive));
            holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_color));
            holder.name.setPaintFlags(0);
        }
        if (items.get(position).getImagePath() == null || items.get(position).getImagePath().isEmpty()) {
            holder.imageIcon.setVisibility(View.INVISIBLE);
        } else {

            Picasso.get()
                    .load(new File(items.get(holder.getAbsoluteAdapterPosition()).getImagePath()))
                    .centerCrop()
                    .resize(100, 100)
                    .into(holder.imageIcon);
            holder.imageIcon.setVisibility(View.VISIBLE);

        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        holder.btnMenu.setOnClickListener(view -> {
            setPosition(holder.getBindingAdapterPosition());
            if (currentapiVersion >= android.os.Build.VERSION_CODES.N) {
                holder.itemView.showContextMenu(holder.itemView.getWidth(), holder.itemView.getHeight() - 3);
            } else {
                holder.itemView.showContextMenu();
            }
        });
        holder.imageIcon.setOnClickListener(view -> {
            setPosition(holder.getBindingAdapterPosition());
            try {
                zoomImageListener.showZoomImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        holder.itemView.setOnClickListener(view -> {
            setPosition(holder.getAbsoluteAdapterPosition());
            OnClickItemListener listener = (OnClickItemListener) holder.imageIcon.getContext();
            listener.onClickItem();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<ItemModel> newList) {
        if (items.size() == 0) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private ImageView imageIcon;
        private TextView name;
        private ImageButton btnMenu;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.rv_item_image);
            name = itemView.findViewById(R.id.rv_item_name);
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

    public interface ShowZoomImage {
        void showZoomImage() throws IOException;
    }

    public interface OnClickItemListener {
        void onClickItem();
    }
}
