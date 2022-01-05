package com.kasandco.familyfinance.app.finance.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.finance.models.FinanceDetailModel;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FinanceDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DialogInterface.OnClickListener {
    private List<FinanceDetailModel> items;
    private SharedPreferenceUtil sharedPreferenceUtil;
    private int currentPosition;
    private OnClickDetailAdapterListener callback;
    @Inject
    public FinanceDetailAdapter(){
        items = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<FinanceDetailModel> _items){
        items.clear();
        items.addAll(_items);
        notifyDataSetChanged();
    }

    public void setCallbackListener(OnClickDetailAdapterListener _callback){
        callback = _callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        sharedPreferenceUtil = new SharedPreferenceUtil(parent.getContext());

        View view;
        if(viewType==0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_header, parent, false);
            return new HeaderViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_finance_detail, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String currency = sharedPreferenceUtil.getSharedPreferences().getString(Constants.SHP_DEFAULT_CURRENCY, Constants.DEFAULT_CURRENCY_VALUE);
        if(holder instanceof ItemViewHolder){
            ((ItemViewHolder) holder).bind(holder.getAbsoluteAdapterPosition(),items.get(position).getComment(), items.get(position).getUserEmail()!=null?items.get(position).getUserEmail():"", items.get(position).getTotal(), items.get(position).getTime(), currency);
        }else {
            ((HeaderViewHolder) holder).bind(items.get(position).getDate());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }else{
            if(items.get(position).getType()==-1){
                return 0;
            }else{
                return 1;
            }
        }
    }

    @Override
    public int getItemCount() {
        return items. size();
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i==DialogInterface.BUTTON_POSITIVE){
            callback.removeItem(currentPosition);
        }else{
            dialogInterface.cancel();
        }
        currentPosition = -1;
    }

    public void deleteItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView comment, total, time;
        private View lineView;
        private ImageButton btnRemove;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.rv_finance_detail_comment);
            total = itemView.findViewById(R.id.rv_finance_detail_total);
            lineView = itemView.findViewById(R.id.rv_finance_detail_view_line);
            time = itemView.findViewById(R.id.rv_finance_detail_time);
            btnRemove = itemView.findViewById(R.id.rv_finance_detail_remove);
        }

        public void bind(int position, String _comment, String _userEmail, double _total, String _time, String currency){
            if(_comment !=null){
                String textComment = itemView.getContext().getString(R.string.pattern_finance_comment,_userEmail.isEmpty()?"":_userEmail+":", _comment);

                CharSequence styledText = HtmlCompat.fromHtml(textComment, HtmlCompat.FROM_HTML_MODE_LEGACY);
                comment.setText(styledText);
            }else {
                comment.setText(R.string.empty_comment);
            }
            time.setText(_time);
            total.setText(String.format(itemView.getContext().getString(R.string.text_currency_format), _total, currency));
            View.OnClickListener clickListener = view -> {
                if (view.getId()==R.id.rv_finance_detail_remove){
                    currentPosition = position;
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                            .setMessage(R.string.delete_dialog)
                            .setPositiveButton(R.string.text_positive_btn, FinanceDetailAdapter.this)
                            .setNegativeButton(R.string.text_negative_btn, FinanceDetailAdapter.this);

                    builder.show();
                }
            };
            btnRemove.setOnClickListener(clickListener);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder{
        private TextView dateHeader;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateHeader = itemView.findViewById(R.id.rv_header_text);
        }

        public void bind(String date){
            dateHeader.setText(date);
        }
    }


    public interface OnClickDetailAdapterListener{
        void removeItem(int position);
    }
}
