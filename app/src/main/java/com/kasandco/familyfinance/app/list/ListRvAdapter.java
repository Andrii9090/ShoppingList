package com.kasandco.familyfinance.app.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.utils.ImageBackgroundUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ListRvAdapter extends RecyclerView.Adapter<ListRvAdapter.ViewHolder> {
    private List<ListModel> listItems;
    private Context context;
    private ListAdapterListener onClickListener;
    private int positionItem;

    @Inject
    public ListRvAdapter() {
        this.listItems = new ArrayList<>();
        positionItem = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        onClickListener = (ListAdapterListener) context;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_cell, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.bind(position);
    }

    public List<ListModel> getItems() {
        return listItems;
    }

    private void setPosition(int position) {
        positionItem = position;
    }

    public int getPosition() {
        return positionItem;
    }

    public void resetPosition() {
        positionItem = -1;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateItems(List<ListModel> listItems) {
        if (getItemCount() > 0) {
            DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffListItem(this.listItems, listItems));
            this.listItems.clear();
            this.listItems.addAll(listItems);
            diff.dispatchUpdatesTo(this);
        } else {
            this.listItems.clear();
            this.listItems.addAll(listItems);
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView icon;
        TextView name;
        ImageButton menu;
        ContextMenu contextMenu;
        ImageView imagePrivate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            icon = itemView.findViewById(R.id.list_item_icon);
            name = itemView.findViewById(R.id.list_item_name);
            menu = itemView.findViewById(R.id.list_item_menu);
            imagePrivate = itemView.findViewById(R.id.list_item_private);
            itemView.setOnLongClickListener(view -> true);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = new MenuInflater(view.getContext());
            inflater.inflate(R.menu.context_menu_list_item, contextMenu);
            this.contextMenu = contextMenu;
            if(listItems.get(positionItem).getIsPrivate()==1){
                contextMenu.getItem(0).setTitle(R.string.text_set_public);
            }

            if(listItems.get(getAbsoluteAdapterPosition()).getIsOwner()==0){
                contextMenu.getItem(0).setVisible(false);
                contextMenu.getItem(1).setVisible(false);
                contextMenu.getItem(5).setVisible(false);
                contextMenu.getItem(6).setVisible(false);
            }
            if(listItems.get(getAbsoluteAdapterPosition()).getFinanceCategoryId()==null || listItems.get(getAbsoluteAdapterPosition()).getFinanceCategoryId()<=0){
                contextMenu.getItem(2).setVisible(false);
            }
        }

        public void bind(int position) {
            if (listItems.get(position) != null && listItems.get(position).getIcon() == null) {
                icon.setVisibility(View.GONE);
            } else {
                icon.setVisibility(View.VISIBLE);
                final Bitmap[] bitmap = {null};
                AssetManager am = context.getAssets();
                final InputStream[] is = {null};
                class Async extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            is[0] = am.open(listItems.get(position).getIcon());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bitmap[0] = BitmapFactory.decodeStream(is[0]);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void unused) {
                        super.onPostExecute(unused);
                        icon.setImageBitmap(bitmap[0]);
                        TypedValue typedValue = new TypedValue();
                        Resources.Theme theme = icon.getContext().getTheme();
                        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                        int color = typedValue.data;
                        ImageBackgroundUtil.setBackgroundColor(icon, color);

                    }
                }
                Async async = new Async();
                async.execute();
            }
            if (listItems.get(position).getIsPrivate() == 1) {
                imagePrivate.setVisibility(View.VISIBLE);
            }else{
                imagePrivate.setVisibility(View.GONE);
            }
            name.setText(listItems.get(position).getName());
            View.OnClickListener menuListener;
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            menuListener = view -> {
                setPosition(getAbsoluteAdapterPosition());
                switch (view.getId()) {
                    case R.id.list_item_menu:
                        positionItem = getAbsoluteAdapterPosition();
                        if (currentapiVersion >= android.os.Build.VERSION_CODES.N) {
                            itemView.showContextMenu(itemView.getWidth(), itemView.getHeight());
                        } else {
                            itemView.showContextMenu();
                        }
                        break;
                }
            };

            menu.setOnClickListener(menuListener);

            itemView.setOnClickListener(view -> onClickListener.itemOnClick(ViewHolder.this));
        }
    }

    public interface ListAdapterListener {
        void itemOnClick(ViewHolder holder);
    }
}
