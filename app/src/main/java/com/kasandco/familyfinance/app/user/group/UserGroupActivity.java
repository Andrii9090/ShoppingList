package com.kasandco.familyfinance.app.user.group;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.BaseActivity;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

public class UserGroupActivity extends BaseActivity implements ContractUserGroup.View, AdapterUserGroup.CallbackAdapterUserGroup {
    private Button btnAddToGroup, getBtnExitFromGroup;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    @Inject
    PresenterUserGroup presenter;
    @Inject
    AdapterUserGroup adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_group);
        App.getAppComponent().plus(new ModuleUserGroup()).inject(this);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.settings_toolbar);

        btnAddToGroup = findViewById(R.id.user_group_connect);
        btnAddToGroup.setOnClickListener(clickListener);
        getBtnExitFromGroup = findViewById(R.id.user_group_disconnect);
        recyclerView = findViewById(R.id.user_group_rv);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        if (activityClass != getClass()) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void setGroupName(String groupName) {

    }

    @Override
    public void setDataToAdapter(List<String> users) {
        adapter.setUsers(users);
    }

    @Override
    public void showToastErrorRemoveUser() {
        ToastUtils.showToast(getString(R.string.error_remove_user), this);
    }

    @Override
    public void userRemoved(String email) {
        adapter.removedUser(email);
    }

    @Override
    public void removeUser(int position) {
        DialogInterface.OnClickListener dialogListener = (dialogInterface, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                presenter.removeUser(position);
            } else {
                dialogInterface.cancel();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.delete_dialog)
                .setPositiveButton(R.string.text_positive_btn, dialogListener)
                .setNegativeButton(R.string.text_negative_btn, dialogListener);

        builder.show();
    }



    private void showEmailDialog() {
        EditText textEmail = new EditText(this);
        textEmail.setFocusable(true);
        textEmail.setHint(R.string.user_email);

        DialogInterface.OnClickListener dialogListener = (dialogInterface, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                presenter.newUserEmail(textEmail.getText().toString());
            } else {
                dialogInterface.cancel();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.title_add_new_user)
                .setView(textEmail)
                .setPositiveButton(R.string.text_add_user_to_group, dialogListener)
                .setNegativeButton(R.string.text_close_dialog, dialogListener);

        builder.show();
    }

    private View.OnClickListener clickListener = (view) -> {
        switch (view.getId()) {
            case R.id.user_group_connect:
                showEmailDialog();
                break;
        }
    };
}