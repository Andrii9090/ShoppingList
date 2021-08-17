package com.kasandco.familyfinance.app.item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textview.MaterialTextView;
import com.kasandco.familyfinance.App;
import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.app.FragmentZoomImage;
import com.kasandco.familyfinance.app.item.fragmentCreate.FragmentItemCreate;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.KeyboardUtil;
import com.kasandco.familyfinance.utils.ToastUtils;

import java.io.IOException;

import javax.inject.Inject;

public class ItemActivity extends AppCompatActivity implements ItemAdapter.ShowZoomImage, ItemAdapter.OnClickItemListener, FragmentZoomImage.ClickListenerZoomImage, ItemContract, Constants, FragmentItemCreate.ClickListenerCreateFragment {
    @Inject
    ItemPresenter presenter;

    @Inject
    FragmentItemCreate createFragment;

    @Inject
    ItemRepository repository;

    @Inject
    FragmentZoomImage fragmentZoomImage;

    long listId;
    String listName;
    long serverId;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listId = getIntent().getLongExtra(LIST_ITEM_ID, 0);
        listName = getIntent().getStringExtra(LIST_NAME);
        serverId = getIntent().getLongExtra(LIST_SERVER_ID, 0);
        App.getItemComponent(this).inject(this);
        setContentView(R.layout.activity_item);
        recyclerView = findViewById(R.id.item_activity_rv);
        emptyText = findViewById(R.id.item_activity_text_empty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Toolbar toolbar = findViewById(R.id.item_activity_toolbar);
        setSupportActionBar(toolbar);
        MaterialTextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(listName);
        swipeRefreshLayout = findViewById(R.id.item_activity_swipe);
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.viewReady(this);
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
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            showGallery();
        }
    }

    @Override
    public void showCamera(Uri photoURI) {
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }else {
            presenter.noCamara();
        }
    }

    @Override
    public void showToast(int resource) {
        ToastUtils.showToast(getString(resource), this);
    }

    @Override
    public void showZoomFragment(String imagePath) {
        if(!fragmentZoomImage.isAdded()){
            getSupportFragmentManager().beginTransaction().add(R.id.item_activity_fragment, fragmentZoomImage).commitNow();
            fragmentZoomImage.setImage(imagePath);
        }
    }

    @Override
    public void closeZoomFragment() {
        if(fragmentZoomImage.isAdded()){
            getSupportFragmentManager().beginTransaction().remove(fragmentZoomImage).commitNow();
        }
    }

    @Override
    public long getListId() {
        return listId;
    }

    @Override
    public void showEmptyText(boolean isShow) {
        if(isShow){
            emptyText.setVisibility(View.VISIBLE);
        }else{
            emptyText.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
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
        }
        return true;
    }

    private void showCreateFragment() {
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
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            clickCamera();
        }
    }

    private void removeItem() {
        presenter.removeItem();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            presenter.activityResult(requestCode, resultCode, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showZoomImage() {
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
}