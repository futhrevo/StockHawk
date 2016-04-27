package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.HistoryColumns;
import com.sam_chordas.android.stockhawk.data.HistoryProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HistoryIntentService extends IntentService {
    private  String LOG_TAG = HistoryIntentService.class.getSimpleName();

    private OkHttpClient client = new OkHttpClient();
    public HistoryIntentService() {
        super("HistoryIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String symbol = intent.getStringExtra("symbol");
            if(symbol == null){
                return ;
            }
            Date mDate = new Date();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String endDate = dateFormat.format(mDate);
            c.add(Calendar.DATE, -9); // one week + weekend
            mDate.setTime(c.getTimeInMillis());
            String startDate = dateFormat.format(mDate);

            Log.i(LOG_TAG, String.format("getting historical data for %s from %s to %s", symbol,startDate, endDate));
            Cursor initQueryCursor;

            StringBuilder urlStringBuilder = new StringBuilder();
            try{
                // Base URL for the Yahoo query
                urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
                urlStringBuilder.append(
                        URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol = \""
                        + symbol + "\" and startDate = \"" + startDate + "\" and endDate = \"" + endDate + "\"",  "UTF-8"));
                // finalize the URL for the API query.
                urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                        + "org%2Falltableswithkeys&callback=");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String urlString = urlStringBuilder.toString();
            String getResponse;
            try {
                getResponse  = fetchData(urlString);

                ContentValues contentValues = new ContentValues();
                contentValues.put(HistoryColumns.SYMBOL, symbol);
                contentValues.put(HistoryColumns.CREATED, dateFormat.format(new Date()));
                contentValues.put(HistoryColumns.HISTORICAL_DATA, Utils.historyJsonToContentVals(this, getResponse));

                this.getContentResolver().insert(HistoryProvider.History.CONTENT_URI, contentValues);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
