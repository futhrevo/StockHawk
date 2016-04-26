package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.google.common.primitives.Floats;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoryColumns;
import com.sam_chordas.android.stockhawk.data.HistoryProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.sam_chordas.android.stockhawk.service.HistoryIntentService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private LineChartView mChart;
    private final String[] mLabels= {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};
    private final float[] mValues = {3.5f, 4.7f, 4.3f, 8f, 6.5f, 9.9f, 7f, 8.3f, 7.0f};

    private Tooltip mTip;
    private String mSymbol;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_line_graph);

        String name = null;
        Intent intent = getIntent();
        Log.i(LOG_TAG, intent.toString());
        try{
            Bundle b =  intent.getExtras();
            if(b != null && b.containsKey(Utils.INTENT_EXTRA_SYMBOL)){
                mSymbol = getIntent().getExtras().getString(Utils.INTENT_EXTRA_SYMBOL);
                name = getIntent().getExtras().getString(Utils.INTENT_EXTRA_NAME);
            }else{
                final Intent originalIntent = (Intent) intent.getExtras().get( Intent.EXTRA_INTENT );
                Log.i(LOG_TAG, originalIntent.toString());
                mSymbol = originalIntent.getStringExtra(Utils.INTENT_EXTRA_SYMBOL);
                name = originalIntent.getStringExtra(Utils.INTENT_EXTRA_NAME);

            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

//
//        final String extra = originalIntent.getStringExtra("SomeExtra");


        addOneoffTask();
        getSupportLoaderManager().initLoader(1, null, this );
        final Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(name != null){
            getSupportActionBar().setTitle(mSymbol.toUpperCase() + " ( " + name +" )");
        }

        mChart = (LineChartView) findViewById(R.id.linechart);

    }

    private void addOneoffTask(){
        if(mSymbol != null ){
            // Immediately make network call.
            Intent nowIntent = new Intent(mContext, HistoryIntentService.class);
            nowIntent.putExtra("symbol", mSymbol);
            mContext.startService(nowIntent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = "symbol = '" + mSymbol + "'";
        return new CursorLoader(this, HistoryProvider.History.CONTENT_URI, null, selection, null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() > 0){
            data.moveToFirst();
            try {
                JSONArray jsonArray = new JSONArray(data.getString(data.getColumnIndex(HistoryColumns.HISTORICAL_DATA)));
                int length = jsonArray.length();
                String[] xaxis = new String[length];
                float[] yaxis = new float[length];
                if(jsonArray != null && length !=0){
                    for(int i=0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        xaxis[length-1 -i] = jsonObject.getString("Date");
                        yaxis[length-1 -i] = (float) jsonObject.getDouble("High");
                    }

                    // Data
                    LineSet dataset = new LineSet(xaxis, yaxis);

                    dataset.setColor(Color.parseColor("#758cbb"))
                            .setFill(Color.parseColor("#2d374c"))
                            .setDotsColor(Color.parseColor("#758cbb"))
                            .setThickness(4);
//                        .setDashed(new float[]{10f,10f});
//                .beginAt(5);
                    mChart.addData(dataset);

                    mChart.setAxisBorderValues((int) Floats.min(yaxis), (int) Floats.max(yaxis));

                    mChart.show();
                    mChart.setStep(5);



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(1, null, this);
    }
}
