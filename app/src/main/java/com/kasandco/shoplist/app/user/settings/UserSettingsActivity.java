package com.kasandco.shoplist.app.user.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kasandco.shoplist.App;
import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.BaseActivity;
import com.kasandco.shoplist.app.list.ListActivity;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.utils.ShowCaseUtil;
import com.kasandco.shoplist.utils.ToastUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class UserSettingsActivity extends BaseActivity implements UserSettingsView {

    private TextView email;
    private Button btnExitAll, copyUid;
    private Toolbar toolbar;
    private ImageView photo;
    private LinearProgressIndicator loader;
    private GoogleApiClient mGoogleApiClient;

    @Inject
    UserSettingsPresenter presenter;
    @Inject
    ShowCaseUtil showCaseUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        App.getAppComponent().plus(new UserSettingsModule(this)).inject(this);
        email = findViewById(R.id.user_settings_email);
        loader = findViewById(R.id.user_settings_loading);
        copyUid = findViewById(R.id.user_settings_copy_uid);
        toolbar = findViewById(R.id.user_settings_toolbar);
        btnExitAll = findViewById(R.id.user_settings_logout_all);
        photo = findViewById(R.id.user_settings_img_profile);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Picasso.get()
                .load(account.getPhotoUrl())
                .centerCrop()
                .placeholder(R.drawable.progress_animation)
                .resize(500, 500)
                .into(photo);

        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.user_settings_title);
        copyUid.setOnClickListener(listener);
        btnExitAll.setOnClickListener(listener);

        if (sharedPreferenceUtil.getSharedPreferences().getString(Constants.EMAIL, null) != null) {
            email.setText(sharedPreferenceUtil.getSharedPreferences().getString(Constants.EMAIL, ""));
        }

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        toolbar.findViewById(R.id.toolbar_menu).setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        new Handler().postDelayed(() -> {
            showCaseUtil.setCase(R.id.user_settings_copy_uid, R.string.title_copy_uuid, R.string.text_detail_copy_uid);
            showCaseUtil.show();
        }, 1000);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            googleLogout();
            presenter.clickLogOut();
        }
        return true;
    }

    private void googleLogout() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                status -> startListActivity());
    }

    private View.OnClickListener listener = view -> {
        switch (view.getId()) {
            case R.id.user_settings_copy_uid:
                presenter.clickBtnCopyUid();
                break;
            case R.id.user_settings_logout_all:
                presenter.clickLogOut(true);
        }
    };


    @Override
    public void showToast(int recourse) {
        ToastUtils.showToast(getString(recourse), this);
    }

    @Override
    public void startListActivity() {
        App.recreateDagger(getApplicationContext());
        startNewActivity(ListActivity.class);
    }

    @SuppressLint("HardwareIds")
    @Override
    public String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    @Override
    public void showLoader() {
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoader() {
        loader.setVisibility(View.GONE);
    }

    @Override
    public void copyToClipBoard(String uid) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("uid", uid);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void showDialog() {
        DialogInterface.OnClickListener dialogListener = (dialogInterface, i) -> {
            dialogInterface.cancel();
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.text_uid_copied)
                .setPositiveButton(R.string.text_positive_btn, dialogListener);

        builder.show();
    }
}