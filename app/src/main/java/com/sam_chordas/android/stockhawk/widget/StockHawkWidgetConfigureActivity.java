package com.sam_chordas.android.stockhawk.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * The configuration screen for the {@link StockHawkWidgetProvider StockHawkWidgetProvider} AppWidget.
 */
public class StockHawkWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.sam_chordas.android.stockhawk.widget.StockHawkWidgetProvider";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private Spinner spinner;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private SimpleCursorAdapter simpleCursorAdapter;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = StockHawkWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            Cursor c = (Cursor) spinner.getSelectedItem();
            String widgetText = c.getString(c.getColumnIndex(QuoteColumns.SYMBOL));

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            Bundle bundle = new Bundle();
            bundle.putString("symbol", widgetText);
            appWidgetManager.updateAppWidgetOptions(mAppWidgetId, bundle);


            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            context.startService(new Intent(context, StockHawkWidgetIntentService.class));
            finish();
        }
    };

    public StockHawkWidgetConfigureActivity() {
        super();
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.stock_hawk_widget_configure);

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        spinner = (Spinner) findViewById(R.id.symbol_spinner);
        Cursor iQueryCursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                null, null, null, null);

        // THE DESIRED COLUMNS TO BE BOUND
        String[] columns = new String[] { QuoteColumns.SYMBOL};
        // THE XML DEFINED VIEWS WHICH THE DATA WILL BE BOUND TO
        int[] to = new int[] { R.id.spinner_text1};
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.simple_spinner_item, iQueryCursor,columns, to);
        spinner.setAdapter(simpleCursorAdapter);
        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

    }
}

