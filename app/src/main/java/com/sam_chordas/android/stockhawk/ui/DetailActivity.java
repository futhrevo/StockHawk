package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.sam_chordas.android.stockhawk.R;

public class DetailActivity extends Activity {

    private LineChartView mChart;
    private final String[] mLabels= {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};
    private final float[][] mValues = {{3.5f, 4.7f, 4.3f, 8f, 6.5f, 9.9f, 7f, 8.3f, 7.0f},
            {4.5f, 2.5f, 2.5f, 9f, 4.5f, 9.5f, 5f, 8.3f, 1.8f}};

    private Tooltip mTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mChart = (LineChartView) findViewById(R.id.linechart);

        // Data
        LineSet dataset = new LineSet(mLabels, mValues[0]);

        dataset.setColor(Color.parseColor("#758cbb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#758cbb"))
                .setThickness(4)
                .setDashed(new float[]{10f,10f});
//                .beginAt(5);
        mChart.addData(dataset);
        mChart.show();
    }
}
