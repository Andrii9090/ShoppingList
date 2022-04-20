package com.kasandco.shoplist.app.user.login;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.kasandco.shoplist.App;
import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.BaseActivity;
import com.kasandco.shoplist.app.list.ListActivity;
import com.kasandco.shoplist.utils.ToastUtils;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity implements LoginContract.View, View.OnClickListener {

    private CircularProgressIndicator loader;
    private FrameLayout frameLayout;
    private GoogleSignInClient mGoogleSignInClient;

    @Inject
    public LoginPresenter presenter;
    private ImageButton btnNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        App.getAppComponent().plus(new LoginModule()).inject(this);
        loader = findViewById(R.id.login_loader);
        frameLayout = findViewById(R.id.login_frame_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        btnNav = findViewById(R.id.login_nav_menu_btn);
        btnNav.setOnClickListener(btnClickListener);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    public void showLoading() {
        frameLayout.setOnClickListener(null);
        frameLayout.setVisibility(View.VISIBLE);
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        frameLayout.setVisibility(View.GONE);
        loader.setVisibility(View.GONE);
    }

    @Override
    public void showToast(int resource) {
        ToastUtils.showToast(getString(resource), this);
    }

    @Override
    public void startListActivity() {
        App.recreateDagger(getApplicationContext());
        startNewActivity(ListActivity.class);
    }


    private final View.OnClickListener btnClickListener = view -> {
        if (view.getId() == R.id.login_nav_menu_btn) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1244);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1244) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account.getIdToken()!=null){
                presenter.receivedIdToken(account.getIdToken());
            }
        } catch (ApiException e) {
            Log.e("Error", e.toString());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
    }
}