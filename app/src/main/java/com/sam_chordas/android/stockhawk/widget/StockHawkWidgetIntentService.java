package com.sam_chordas.android.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class StockHawkWidgetIntentService extends IntentService {
    private final String LOG_TAG = StockHawkWidgetIntentService.class.getSimpleName();

    private static final String[] QUOTE_COLUMNS = {
            QuoteColumns.SYMBOL,
            QuoteColumns.CHANGE,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.NAME
    };

    // these indices must match the projection
    private static final int INDEX_SYMBOL = 0;
    private static final int INDEX_CHANGE = 1;
    private static final int INDEX_BIDPRICE = 2;
    private static final int INDEX_PERCENT_CHANGE = 3;
    private static final int INDEX_SYMBOL_NAME = 4;



    public StockHawkWidgetIntentService() {
        super("StockHawkWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all widget Ids for single stock display
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockHawkWidgetProvider.class));

        for(int appWidgetId : appWidgetIds){

            int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
            int defaultWidth = 2;

            int layoutId;
            if(widgetWidth == defaultWidth){
                layoutId = R.layout.stock_hawk_widget_small;
            }else{
                layoutId = R.layout.stock_hawk_widget;
            }

            Bundle bundle = appWidgetManager.getAppWidgetOptions(appWidgetId);

            String symbol = bundle.getString("symbol", "TSLA");
            String selection = "symbol = '" + symbol + "'";
            Cursor data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, QUOTE_COLUMNS, selection, null, null );

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
            if (data == null) {
                views.setTextViewText(R.id.widget_item_symbol, this.getString(R.string.empty_symbol_list));

                return;
            }

            if (!data.moveToFirst()) {
                data.close();
                views.setTextViewText(R.id.widget_item_symbol, this.getString(R.string.empty_symbol_list));
                return;
            }
            String bidprice = data.getString(INDEX_BIDPRICE);
            String change = data.getString(INDEX_PERCENT_CHANGE);
            String name = data.getString(INDEX_SYMBOL_NAME);

            data.close();



            // Add the data to the RemoteViews
            views.setTextViewText(R.id.widget_item_symbol, symbol);
            views.setContentDescription(R.id.widget_item_symbol, this.getString(R.string.a11y_symbol, name));
            views.setTextViewText(R.id.widget_bid_price, bidprice);
            views.setContentDescription(R.id.widget_bid_price, this.getString(R.string.a11y_bid_price, bidprice));
            views.setTextViewText(R.id.widget_change, change);
            views.setContentDescription(R.id.widget_change, this.getString(R.string.a11y_percent_change, change));

            char first = change.charAt(0);
            if(first == '-'){
                views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }else if(first == '+'){
                views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            }

            // Create an Intent to launch MainActivity
            final Intent launchIntent = new Intent(this, DetailActivity.class);
            final Bundle intentBundle = new Bundle();
            if(!symbol.equals("Unknown")){
                intentBundle.putString(Utils.INTENT_EXTRA_SYMBOL,
                        symbol);
                intentBundle.putString(Utils.INTENT_EXTRA_NAME, name);
            }

            launchIntent.putExtras(intentBundle);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 3, launchIntent, 0);
            PendingIntent pendingIntent = TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(launchIntent)
                    .getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_item_display, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {

        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

            return getCellsForSize(minWidthDp);
        }
        return 2 ;
    }

    /**
     * Returns number of cells needed for given size of the widget.
     *
     * @param size Widget size in dp.
     * @return Size in number of cells.
     */
    private static int getCellsForSize(int size) {
        int n = 2;
        while (70 * n - 30 < size) {
            ++n;
        }
        return n - 1;
    }
}
