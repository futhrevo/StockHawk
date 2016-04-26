package com.sam_chordas.android.stockhawk.widget;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by rakeshkalyankar on 26/04/16.
 */
@SuppressLint("NewApi")
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        WidgetDataProvider dataProvider = new WidgetDataProvider(
                getApplicationContext(), intent);
        return dataProvider;
    }

}

