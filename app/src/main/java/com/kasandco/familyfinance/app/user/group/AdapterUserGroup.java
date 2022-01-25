package com.kasandco.familyfinance.app.user.group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class AdapterUserGroup extends RecyclerView.Adapter<AdapterUserGroup.ViewHolder> {
    private List<String> users;
    private CallbackAdapterUserGroup callback;
    private boolean isMainUser;
    private SharedPreferenceUtil sharedPreference;

    @Inject
    public AdapterUserGroup(SharedPreferenceUtil sharedPreferenceUtil) {
        users = new ArrayList<>();
        isMainUser = true;
        sharedPreference = sharedPreferenceUtil;
    }

    public void setUsers(List<String> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void setCallback(CallbackAdapterUserGroup callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_user_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void removedUser(String email) {
        int index = users.indexOf(email);
        users.remove(index);
        notifyItemRemoved(index);
    }

    public void setMainUser(boolean mainUser) {
        isMainUser = mainUser;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton btnRemove;
        private TextView email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnRemove = itemView.findViewById(R.id.rv_user_group_btn_remove);
            email = itemView.findViewById(R.id.rv_user_group_email);
            if(!isMainUser){
                btnRemove.setVisibility(View.GONE);
            }
        }

        public void bind(String emailText) {
            email.setText(emailText);
            if(sharedPreference.getSharedPreferences().getString(Constants.EMAIL, "").equals(emailText)){
                btnRemove.setVisibility(View.GONE);
            }else {
                btnRemove.setOnClickListener(clickListener);
            }
        }

        private View.OnClickListener clickListener = (view -> callback.removeUser(getAbsoluteAdapterPosition()));
    }

    public interface CallbackAdapterUserGroup {
        void removeUser(int position);
    }
}
