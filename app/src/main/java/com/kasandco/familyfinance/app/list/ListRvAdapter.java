package com.kasandco.familyfinance.app.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.icon.AdapterIcon;
import com.kasandco.familyfinance.app.icon.IconDao;
import com.kasandco.familyfinance.utils.ImageBackgroundUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

public class ListRvAdapter extends RecyclerView.Adapter<ListRvAdapter.ViewHolder> {
    private List<ListModel> listItems;
    private Context context;
    private ListAdapterListener onClickListener;
    private int positionItem;


    @Inject
    public ListRvAdapter(List<ListModel> listItems) {
        this.listItems = listItems;
        positionItem = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        onClickListener = (ListAdapterListener) context;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_element, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (listItems.get(position) != null && listItems.get(position).getIcon() == null) {
            holder.icon.setVisibility(View.GONE);
        } else {
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
                    holder.icon.setImageBitmap(bitmap[0]);
                    ImageBackgroundUtil.setBackgroundColor(holder.icon, R.attr.colorPrimary);

                }
            }
            Async async = new Async();
            async.execute();
        }
        holder.name.setText(listItems.get(position).getName());
        holder.quantity.setText(String.format("%d/%d", listItems.get(position).getQuantityActive(), listItems.get(position).getQuantityInactive()));
        View.OnClickListener menuListener;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        menuListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPosition(position);
                switch (view.getId()) {
                    case R.id.list_item_menu:
                        if (currentapiVersion >= android.os.Build.VERSION_CODES.N) {
                            holder.itemView.showContextMenu(holder.itemView.getWidth(), holder.itemView.getHeight());
                        } else {
                            holder.itemView.showContextMenu();
                        }
                        break;
                }
            }
        };
        holder.menu.setOnClickListener(menuListener);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.itemOnClick(holder);
            }
        });
    }

    private void setPosition(int position) {
        positionItem = position;
    }

    public int getPosition() {
        return positionItem;
    }

    public void nullPosition() {
        positionItem = -1;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView icon;
        TextView name;
        TextView quantity;
        ImageButton menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            icon = itemView.findViewById(R.id.list_item_icon);
            name = itemView.findViewById(R.id.list_item_name);
            quantity = itemView.findViewById(R.id.list_item_quantity);
            menu = itemView.findViewById(R.id.list_item_menu);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.e("Menu", "oklong");
                    return true;
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = new MenuInflater(view.getContext());
            inflater.inflate(R.menu.context_menu_list_item, contextMenu);
        }

    }

    public interface ListAdapterListener {
        void menuOnClick(ViewHolder holder);

        void itemOnClick(ViewHolder holder);
    }
}
