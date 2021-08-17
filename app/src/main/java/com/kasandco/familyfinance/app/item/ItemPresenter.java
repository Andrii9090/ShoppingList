package com.kasandco.familyfinance.app.item;

import android.content.Intent;
import android.net.Uri;

import com.kasandco.familyfinance.R;
import com.kasandco.familyfinance.core.BasePresenter;
import com.kasandco.familyfinance.core.Constants;
import com.kasandco.familyfinance.utils.SaveImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemPresenter extends BasePresenter<ItemContract> {

    ItemDao itemDao;

    ItemAdapter adapter;

    ItemRepository repository;

    SaveImageUtils imageUtils;

    long listId;
    private List<ItemModel> items;

    public ItemPresenter(ItemRepository repository, ItemDao dao, ItemAdapter adapter, SaveImageUtils imageUtils) {
        this.repository = repository;
        this.itemDao = dao;
        this.adapter = adapter;
        this.imageUtils = imageUtils;
        repository.setPresenter(this);
    }

    @Override
    public void viewReady(ItemContract view) {
        this.view = view;
        prepare();
    }

    private void prepare() {
        listId = view.getListId();
        repository.getAll(listId);
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
        setEmptyText();
    }

    private void setEmptyText() {
        if(adapter.getItemCount()>0){
            view.showEmptyText(false);
        }else {
            view.showEmptyText(true);
        }
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
        }else {
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
        if(requestCode== Constants.REQUEST_TAKE_GALLERY){
            Uri uri = data.getData();
            repository.saveImagePath(imageUtils.getRealPathFromURI(uri), adapter.items.get(adapter.getPosition()).getId());
        }
        if(requestCode==Constants.REQUEST_TAKE_PHOTO){
            Uri imageUri = imageUtils.copyImageToGallery(imageUtils.getCurrentFilePath());
            repository.saveImagePath(imageUri.getPath(), adapter.items.get(adapter.getPosition()).getId());
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        return imageUtils.getRealPathFromURI(contentUri);
    }

    public void clickShowZoomImage() {
        if(adapter.items.get(adapter.getPosition()).getImagePath()!=null) {
            view.showZoomFragment(adapter.items.get(adapter.getPosition()).getImagePath());
        }else{
            view.showToast(R.string.error_show_img);
        }
    }

    public void clickRemoveImage() {
        repository.saveImagePath("", adapter.items.get(adapter.getPosition()).getId());
        adapter.setPosition(-1);
    }

    public void clickCloseZoomFragment() {
        view.closeZoomFragment();
    }

    public void refreshData() {
        items.clear();
        repository.unSubscribeData();
        repository.getAll(listId);
    }

    public void clickToItem() {
        ItemModel item = items.get(adapter.getPosition());
        if(item.getStatus()==1){
            item.setStatus(0);
        }else{
            item.setStatus(1);
        }
        item.setDateMod(String.valueOf(System.currentTimeMillis()));

        repository.changeStatus(item);
    }
}
