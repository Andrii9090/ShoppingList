package com.kasandco.shoplist.app.item;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.shoplist.core.BaseContract;

import java.io.IOException;

public interface ItemContract extends BaseContract {
    void startAdapter(RecyclerView.Adapter<?> adapter);

    void showEditForm(ItemModel item);

    void showGallery();

    void showCamera(Uri photoURI);

    void showToast(int no_camera);

    void showZoomFragment(String imagePath) throws IOException;

    void closeZoomFragment();

    long getListId();

    void showEmptyText(boolean b);

    ContentResolver getContentResolver();

    long getServerListId();

    void showSnackBarToast(int error_upload_image, int len);
}
