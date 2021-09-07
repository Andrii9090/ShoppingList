package com.kasandco.familyfinance.app.expenseHistory.adapters;

import static com.kasandco.familyfinance.core.Constants.SHP_DEFAULT_CURRENCY;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.expenseHistory.DiffUtilFinanceAdapter;
import com.kasandco.familyfinance.app.expenseHistory.models.FinanceCategoryWithTotal;
import com.kasandco.familyfinance.utils.ImageBackgroundUtil;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FinanceCategoryAdapter extends RecyclerView.Adapter<FinanceCategoryAdapter.ViewHolder> {
    private List<FinanceCategoryWithTotal> items;
    private int currentPosition;
    private OnClickItemListener listener;

    @Inject
    public FinanceCategoryAdapter(){
        currentPosition = -1;
    }

    @NonNull
    @Override
    public FinanceCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_finance_category, parent, false);
        return new ViewHolder(view);
    }

    public List<FinanceCategoryWithTotal> getItems(){
        return items;
    }

    public void setItems(List<FinanceCategoryWithTotal> items){
        if(this.items==null) {
            this.items = new ArrayList<>();
            this.items.addAll(items);
        }else {

            DiffUtil.DiffResult diffResult =
                    DiffUtil.calculateDiff(new DiffUtilFinanceAdapter(this.items, items));
            this.items.clear();
            this.items.addAll(items);
            diffResult.dispatchUpdatesTo(this);
        }
    }

    public void setPosition(int current){
        currentPosition = current;
    }

    public void nullingPosition(){
        currentPosition = -1;
    }

    @Override
    public void onBindViewHolder(@NonNull FinanceCategoryAdapter.ViewHolder holder, int position) {
        holder.bind(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                setPosition(holder.getAbsoluteAdapterPosition());
                for (FinanceCategoryWithTotal item:items) {
                    if(item.isSelected()){
                        item.setSelected(false);
                        break;
                    }
                }
                items.get(holder.getAbsoluteAdapterPosition()).setSelected(true);
                listener.clickToItem();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int getCurrentPosition(){
        return currentPosition;
    }

    public void setOnClickListener(OnClickItemListener listener){
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView icon;
        TextView name, total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.rv_item_category_icon);
            name = itemView.findViewById(R.id.rv_item_category_name);
            total = itemView.findViewById(R.id.rv_item_category_total);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setPosition(getAbsoluteAdapterPosition());
                    return false;
                }
            });
        }

        @SuppressLint("DefaultLocale")
        public void bind(int position) {
            //TODO Сделать с Handler  и также в лист адаптере
            AssetManager am = icon.getContext().getAssets();
            if(items.get(position).isSelected()) {
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, ContextCompat.getColor(name.getContext(),R.color.selected));
                colorAnimation.setDuration(250); // milliseconds
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        itemView.setBackgroundColor((int) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
            }else {
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), ContextCompat.getColor(name.getContext(),R.color.selected), Color.WHITE);
                colorAnimation.setDuration(250); // milliseconds
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        itemView.setBackgroundColor((int) animator.getAnimatedValue());
                    }

                });
                colorAnimation.start();
            }
            InputStream is  = null;
            Bitmap bitmap = null;
            try {
                is = am.open(items.get(position).getCategory().getIconPath());
                bitmap= BitmapFactory.decodeStream(is);
                icon.setImageBitmap(bitmap);
                ImageBackgroundUtil.setBackgroundColor(icon, R.attr.colorPrimary);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }

            name.setText(items.get(position).getCategory().getName());
            total.setText(String.format("%.2f %s", items.get(position).getTotal(), new SharedPreferenceUtil(total.getContext()).getSharedPreferences().getString(SHP_DEFAULT_CURRENCY, "USD")));
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater menuInflater = new MenuInflater(view.getContext());
            menuInflater.inflate(R.menu.menu_context_finance_category, contextMenu);
        }
    }

    public interface OnClickItemListener{
        void clickToItem();
    }
}
