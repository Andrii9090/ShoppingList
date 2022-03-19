package com.kasandco.shoplist.utils;

import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.kasandco.shoplist.R;

public class ImageBackgroundUtil {
    public static void setBackgroundColor(View view, int resource){
        if(view instanceof ImageView) {
            ((ImageView) view).setColorFilter(resource, PorterDuff.Mode.SRC_ATOP);
        }
        if(view instanceof ImageButton) {
            ((ImageButton) view).setColorFilter(resource, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
