package com.kasandco.familyfinance.app;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.kasandco.familyfinance.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

public class FragmentZoomImage extends Fragment {

    private ImageButton btnClose;
    private Button btnRemove;
    private SubsamplingScaleImageView image;
    private ConstraintLayout buttonsRoot;

    private ClickListenerZoomImage rootListener;

    @Inject
    public FragmentZoomImage(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zoom_image, container, false);
        view.setOnClickListener(null);
        rootListener = (ClickListenerZoomImage) container.getContext();
        btnClose = view.findViewById(R.id.fragment_zoom_image_btn_close);
        btnRemove = view.findViewById(R.id.fragment_zoom_image_btn_remove);
        buttonsRoot = view.findViewById(R.id.fragment_zoom_image_buttons);

        btnRemove.setOnClickListener(clickListener);
        btnClose.setOnClickListener(clickListener);
        image = view.findViewById(R.id.fragment_zoom_image_item);
        image.setVisibility(View.INVISIBLE);
        image.setOnClickListener(clickListener);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    public void setImage(String path){
        image.setImage(ImageSource.bitmap(BitmapFactory.decodeFile(path)));
        image.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.fragment_zoom_image_btn_close:
                    rootListener.closeZoomImage();
                    break;
                case R.id.fragment_zoom_image_btn_remove:
                    rootListener.removeImage();
                    rootListener.closeZoomImage();
                    break;
                case R.id.fragment_zoom_image_item:
                    if(buttonsRoot.getVisibility()==View.INVISIBLE){
                        buttonsRoot.setVisibility(View.VISIBLE);
                    }else {
                        buttonsRoot.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    };

    public interface ClickListenerZoomImage{
        void closeZoomImage();
        void removeImage();
    }
}
