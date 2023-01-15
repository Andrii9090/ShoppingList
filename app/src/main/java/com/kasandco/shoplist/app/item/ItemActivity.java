package com.kasandco.shoplist.app.item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.kasandco.shoplist.App;
import com.kasandco.shoplist.R;
import com.kasandco.shoplist.app.BaseActivity;
import com.kasandco.shoplist.app.FragmentZoomImage;
import com.kasandco.shoplist.app.item.create.FragmentItemCreate;
import com.kasandco.shoplist.app.list.ListActivity;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.utils.KeyboardUtil;
import com.kasandco.shoplist.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

public class ItemActivity extends BaseActivity implements ItemAdapter.ShowZoomImage, ItemAdapter.OnClickItemListener, FragmentZoomImage.ClickListenerZoomImage, ItemContract, Constants, FragmentItemCreate.ClickListenerCreateFragment {
    @Inject
    ItemPresenter presenter;
    FragmentItemCreate createFragment;
    FragmentZoomImage fragmentZoomImage;

    private long listId;
    private String listName;
    private long serverListId;

    private Snackbar snackbar;

    private RecyclerView recyclerView;
    private TextView emptyText;
    AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
        listId = getIntent().getLongExtra(LIST_ITEM_ID, 0);
        listName = getIntent().getStringExtra(LIST_NAME);
        serverListId = getIntent().getLongExtra(LIST_SERVER_ID, 0);
        App.getItemComponent(this).inject(this);
        setContentView(R.layout.activity_item);

        refreshLayout = findViewById(R.id.item_activity_swipe);
        refreshLayout.setOnRefreshListener(refreshListener);
        mAdView = findViewById(R.id.list_item_adView);
        recyclerView = findViewById(R.id.item_activity_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyText = findViewById(R.id.item_activity_text_empty);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.item_activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.toolbar_menu).setOnClickListener(view -> drawerLayout.openDrawer(Gravity.LEFT));
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(listName);
        if (sharedPreferenceUtil.isPro()) {
            mAdView.setVisibility(View.GONE);
        } else {
            showAdd();
        }
        showLoading();
    }

    private void showAdd() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-2199413045845818/1369858818");
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Inject
    public void getFragmentZoomImage(FragmentZoomImage fragmentZoomImage) {
        this.fragmentZoomImage = fragmentZoomImage;
    }

    @Inject
    public void getFragmentZoomImage(FragmentItemCreate fragmentItemCreate) {
        this.createFragment = fragmentItemCreate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
    }

    @Override
    protected void startNewActivity(Class<?> activityClass) {
        if (getClass() != activityClass) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        } else {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void startAdapter(RecyclerView.Adapter<?> adapter) {
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(positionStart);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(positionStart);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(positionStart);
            }
        });
    }

    @Override
    public void showEditForm(ItemModel item) {
        showCreateFragment();
        createFragment.edit(item);
    }

    @Override
    public void showGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo)), REQUEST_TAKE_GALLERY);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            showGallery();
        }
    }

    @Override
    public void showCamera(Uri photoURI) {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        } else {
            presenter.noCamara();
        }
    }

    @Override
    public void showToast(int resource) {
        ToastUtils.showToast(getString(resource), this);
    }

    @Override
    public void showZoomFragment(String imagePath) throws IOException {
        getSupportFragmentManager().beginTransaction().add(R.id.item_activity_fragment, fragmentZoomImage).commitNow();
        fragmentZoomImage.setImage(imagePath);
    }

    @Override
    public void closeZoomFragment() {
        if (fragmentZoomImage.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(fragmentZoomImage).commitNow();
        }
    }

    @Override
    public long getListId() {
        return listId;
    }

    @Override
    public void showEmptyText(boolean isShow) {
        if (isShow) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public long getServerListId() {
        return serverListId;
    }

    @Override
    public void showSnackBarToast(int text, int length) {
        if (snackbar == null || !snackbar.isShown()) {
            snackbar = Snackbar.make(this, recyclerView, getString(text), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_activity_add_new_list:
                showCreateFragment();
                break;
            case R.id.create_item_floating_btn:
                startVoiceRecognitionActivity();
                break;
        }
        return true;
    }

    private void showCreateFragment() {
        Bundle bundle = new Bundle();
        bundle.putLong("listId", listId);
        bundle.putLong("serverListId", serverListId);
        createFragment.setArguments(bundle);
        if (getSupportFragmentManager().findFragmentById(R.id.item_activity_fragment) != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.item_activity_fragment, createFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.item_activity_fragment, createFragment).commit();
        }
        getSupportFragmentManager().executePendingTransactions();
        KeyboardUtil.showKeyboard(this);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_context_item_edit:
                editItem();
                break;
            case R.id.menu_context_item_remove:
                removeItem();
                break;
            case R.id.menu_context_item_add_photo_camera:
                clickCamera();
                break;
            case R.id.menu_context_item_add_photo_gallery:
                clickGallery();
                break;
        }

        return true;
    }

    private void clickGallery() {
        presenter.clickGallery();
    }

    private void clickCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            presenter.clickCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            clickCamera();
        }
    }

    private void removeItem() {
        DialogInterface.OnClickListener dialogListener = (dialogInterface, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                presenter.removeItem();
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

    private void editItem() {
        presenter.clickEdit();
    }

    @Override
    public void close() {
        if (createFragment.isAdded()) {
            KeyboardUtil.hideKeyboard(this);
            getSupportFragmentManager().beginTransaction().remove(createFragment).commit();
        }
    }


    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.text_sound));
        startActivityForResult(intent, Constants.CODE_VOICE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CODE_VOICE_RESULT && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            Log.e("Voice", "activity");
            String textEntered = Character.toString(matches.get(0).charAt(0)).toUpperCase() + matches.get(0).substring(1);
            presenter.createNewItem(textEntered, listId);
        } else if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_TAKE_GALLERY && resultCode == RESULT_OK) {
            try {
                presenter.activityResult(requestCode, resultCode, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showZoomImage() throws IOException {
        presenter.clickShowZoomImage();
    }

    @Override
    public void closeZoomImage() {
        presenter.clickCloseZoomFragment();
    }

    @Override
    public void removeImage() {
        presenter.clickRemoveImage();
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            presenter.refreshData();
        }
    };

    @Override
    public void onClickItem() {
        presenter.clickToItem();
    }

    @Override
    protected void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startNewActivity(ListActivity.class);
    }
}