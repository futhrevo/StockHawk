package com.sam_chordas.android.stockhawk.widget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by rakeshkalyankar on 26/04/16.
 */
@SuppressLint("NewApi")
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {
    private final String LOG_TAG = WidgetDataProvider.class.getSimpleName();
    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;

    // these indices must match the projection
    private static final int INDEX_SYMBOL_ID = 0;
    private static final int INDEX_SYMBOL = 1;
    private static final int INDEX_CHANGE = 2;
    private static final int INDEX_BIDPRICE = 3;
    private static final int INDEX_PERCENT_CHANGE = 4;
    private static final int INDEX_SYMBOL_NAME = 5;

    private static final String[] QUOTE_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.CHANGE,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.NAME
    };

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        String symbol = "Unknown";
        String bidprice = "0";
        String change = "0";
        String name = "Unknown";
        if(mCursor.moveToPosition(position)){
            symbol = mCursor.getString(INDEX_SYMBOL);
            bidprice = mCursor.getString(INDEX_BIDPRICE);
            change = mCursor.getString(INDEX_PERCENT_CHANGE);
            name = mCursor.getString(INDEX_SYMBOL_NAME);
        }

        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                R.layout.stock_hawk_list_widget_item);
        mView.setTextViewText(R.id.widget_list_stock_symbol, symbol);
        mView.setCharSequence(R.id.widget_list_stock_symbol, "setContentDescription", mContext.getString(R.string.a11y_symbol, name));
        mView.setTextViewText(R.id.widget_list_bid_price,bidprice);
        mView.setCharSequence(R.id.widget_list_bid_price, "setContentDescription", mContext.getString(R.string.a11y_bid_price, bidprice));
        mView.setTextViewText(R.id.widget_list_change, change);
        mView.setCharSequence(R.id.widget_list_change, "setContentDescription", mContext.getString(R.string.a11y_percent_change, change));

        char first = change.charAt(0);
        if(first == '-'){
            mView.setInt(R.id.widget_list_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }else if(first == '+'){
            mView.setInt(R.id.widget_list_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        }

        final Intent fillInIntent = new Intent();
        final Bundle bundle = new Bundle();
        if(!symbol.equals("Unknown")){
            bundle.putString(Utils.INTENT_EXTRA_SYMBOL,
                    symbol);
            bundle.putString(Utils.INTENT_EXTRA_NAME, name);
        }
        fillInIntent.putExtras(bundle);
        mView.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
        return mView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        // Since we reload the cursor in onDataSetChanged() which gets called immediately after
        // onCreate(), we do nothing here.
    }

    @Override
    public void onDataSetChanged() {
        // Refresh the cursor
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, QUOTE_COLUMNS, null,
                null, null);
    }


    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

}
