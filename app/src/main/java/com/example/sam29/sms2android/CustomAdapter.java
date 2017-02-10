package com.example.sam29.sms2android;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;

/**
 * Created by sam29 on 08-Feb-17.
 */

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {
    public CustomAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public void onClick(View v) {

    }
}
