package com.example.shubhamr.kontakts;

import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public interface clickListenerInterface {

    public void itemClicked(View view, int position, CircleImageView sharedImage, TextView sharedText);

}
