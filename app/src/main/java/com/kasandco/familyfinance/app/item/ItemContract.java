package com.kasandco.familyfinance.app.item;

import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.core.BasePresenterInterface;

public interface ItemContract extends BasePresenterInterface {
    void startAdapter(RecyclerView.Adapter<?> adapter);

    void showEditForm(ItemModel item);

    void showGallery();

    void showCamera(Uri photoURI);

    void showToast(int no_camera);

    void showZoomFragment(String imagePath);

    void closeZoomFragment();
}
