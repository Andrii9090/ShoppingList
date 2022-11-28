package com.kasandco.shoplist.app.item;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.kasandco.shoplist.R;
import com.kasandco.shoplist.core.BasePresenter;
import com.kasandco.shoplist.core.Constants;
import com.kasandco.shoplist.utils.SaveImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemPresenter extends BasePresenter<ItemContract> implements ItemRepository.ItemRepositoryCallback {

    private ItemAdapter adapter;
    private ItemRepository repository;
    private SaveImageUtils imageUtils;

    long listId;
    long serverListId;
    private List<ItemModel> items;

    public ItemPresenter(ItemRepository repository, ItemAdapter adapter, SaveImageUtils imageUtils) {
        this.repository = repository;
        this.adapter = adapter;
        this.imageUtils = imageUtils;
    }

    @Override
    public void viewReady(ItemContract view) {
        this.view = view;
        prepare();
    }

    private void prepare() {
        view.showLoading();
        listId = view.getListId();
        serverListId = view.getServerListId();
        repository.getAll(listId, serverListId,this);
        view.startAdapter(adapter);
    }

    public void removeItem() {
        repository.remove(adapter.items.get(adapter.getPosition()));
    }

    public void clickEdit() {
        view.showEditForm(adapter.items.get(adapter.getPosition()));
    }

    public void setItems(List<ItemModel> list) {
        items = new ArrayList<>();
        items.addAll(list);
        view.hideLoading();
        adapter.updateList(list);
        view.hideLoading();
        setEmptyText();
    }

    @Override
    public void errorLoadImage() {
        view.showSnackBarToast(R.string.error_upload_image, 0);
    }

    @Override
    public void noPerm() {
        view.showToast(R.string.text_no_permissions);
    }

    @Override
    public void error() {
        view.showToast(R.string.text_error);
    }

    private void setEmptyText() {
        view.showEmptyText(adapter.getItemCount() <= 0);
    }

    public void clickCamera() {
        prepareFileForCamera();
    }

    public void prepareFileForCamera() {
        File photoFile = null;
        try {
            photoFile = imageUtils.createImageFile();
        } catch (IOException ex) {
            errorCamera();
        }

        if (photoFile != null) {
            Uri photoURI = imageUtils.getPhotoUri();
            view.showCamera(photoURI);
        } else {
            view.showToast(R.string.error_todo_photo);
        }
    }

    public void clickGallery() {
        view.showGallery();
    }

    public void noCamara() {
        view.showToast(R.string.no_camera);
    }

    public void errorCamera() {
        view.showToast(R.string.error_todo_photo);
    }

    public void activityResult(int requestCode, int resultCode, Intent data) throws IOException {
        if (requestCode == Constants.REQUEST_TAKE_GALLERY) {
            Uri uri = data.getData();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContentResolver(), uri);
            repository.saveImagePath(imageUtils.getRealPathFromURI(uri), bitmap, adapter.items.get(adapter.getPosition()).getId());
        }
        if (requestCode == Constants.REQUEST_TAKE_PHOTO) {
            Uri imageUri = imageUtils.copyImageToGallery(imageUtils.getCurrentFilePath());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContentResolver(), imageUri);
            repository.saveImagePath(imageUri.getPath(), bitmap, adapter.items.get(adapter.getPosition()).getId());
        }
    }

    public void clickShowZoomImage() throws IOException {
        if (adapter.items.get(adapter.getPosition()).getImagePath() != null) {
            view.showZoomFragment(adapter.items.get(adapter.getPosition()).getImagePath());
        } else {
            view.showToast(R.string.error_show_img);
        }
    }

    public void clickRemoveImage() {
        repository.saveImagePath("", null, adapter.items.get(adapter.getPosition()).getId());
        adapter.setPosition(-1);
    }

    public void clickCloseZoomFragment() {
        view.closeZoomFragment();
    }

    public void refreshData() {
        items.clear();
        repository.getAll(listId, serverListId, this);
    }

    public void clickToItem() {
        ItemModel item = items.get(adapter.getPosition());
        if (item.getStatus() == 1) {
            item.setStatus(0);
        } else {
            item.setStatus(1);
        }
        item.setDateMod(String.valueOf(System.currentTimeMillis()));

        repository.changeStatus(item);
    }

    public void createNewItem(String name, long listId) {
        if (!name.isEmpty()) {
            repository.create(name, listId, serverListId);
        }
        view.showToast(R.string.text_item_created);
    }

    public void destroy() {
        repository.unSubscribe();
    }

    @Override
    public void swipeRefresh() {

    }


}
