package com.kasandco.familyfinance.app.item;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import com.kasandco.familyfinance.core.BaseContract;

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
}
