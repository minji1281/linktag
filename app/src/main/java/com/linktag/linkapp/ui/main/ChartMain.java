package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.linktag.linkapp.model.BHM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.BhmVO;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.ClsDateTime;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartMain extends AsyncTask<Void, Void, Void> {
    protected InterfaceUser mUser;

    private CombinedChart chart;
    private ArrayList<Entry> V_SUM = new ArrayList<>();
    private ArrayList<BarEntry> V_A1 = new ArrayList<>();
    private ArrayList<BarEntry> V_A2 = new ArrayList<>();
    private ArrayList<BarEntry> V_A3 = new ArrayList<>();
    private ArrayList<BarEntry> V_A4 = new ArrayList<>();

    public ChartMain(CombinedChart chart){
        this.chart = chart;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mUser = InterfaceUser.getInstance();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        requestBHM_SELECT();

        return null;
    }

    @Override
    protected void onPostExecute(Void voids){
        //System.out.println(voids);

    }

    private void requestBHM_SELECT(){

        Call<BHM_Model> call = Http.bhm(HttpBaseService.TYPE.POST).BHM_SELECT(
                BaseConst.URL_HOST,
                "CHART",
                mUser.Value.CTM_01,
        "",
                mUser.Value.RUTC_01,
                //"20191210",
                ClsDateTime.getNow("yyyyMMdd"),
                ClsDateTime.getNow("yyyyMMdd"),
                ""
        );

        call.enqueue(new Callback<BHM_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<BHM_Model> call, Response<BHM_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            Response<BHM_Model> response = (Response<BHM_Model>) msg.obj;

                            System.out.println("@@@@@@@@@@@@@@@@@1111");
                            callBack(response.body());
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<BHM_Model> call, Throwable t){

            }
        });
    }

    private void callBack(BHM_Model data){
        for(BhmVO VO : data.Data){
            float hour = Float.valueOf(VO.BHM_07);

            V_SUM.add(new Entry(hour, VO.A1 + VO.A2 + VO.A3 + VO.A4));

            V_A1.add(new BarEntry(hour, VO.A1));
            V_A2.add(new BarEntry(hour, VO.A2));
            V_A3.add(new BarEntry(hour, VO.A3));
            V_A4.add(new BarEntry(hour, VO.A4));
        }

        drawChart();

    }

    private void drawChart(){

        //chart.setBackgroundColor(Color.WHITE);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);
        //chart.setTouchEnabled(true);
        //chart.setDragEnabled(true);
        //chart.setScaleEnabled(true);

        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis;
        {
            xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            //xAxis.setLabelCount(7);
            xAxis.setSpaceMin(0.5f);
            xAxis.setSpaceMax(0.5f);

            xAxis.enableGridDashedLine(10f, 10f, 0f);

            ValueFormatter custom = new MyValueFormatter("시");
            xAxis.setValueFormatter(custom);
        }

        YAxis leftAxis;
        {
            leftAxis = chart.getAxisLeft();

            //chart.getAxisLeft().setEnabled(false);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setGranularity(2f);
            leftAxis.setLabelCount(5);

            //leftAxis.setDrawGridLines(false);
            //leftAxis.enableAxisLineDashedLine(10f, 10f, 0f);
            //leftAxis.setAxisMaximum(200f);
        }

        YAxis rightAxis;
        {
            rightAxis = chart.getAxisRight();

//            rightAxis.setDrawGridLines(false);
//            rightAxis.setAxisMinimum(0f);
//            rightAxis.setGranularity(2f);
//            rightAxis.setLabelCount(5);

            rightAxis.setDrawLabels(false);
            rightAxis.setDrawAxisLine(false);
            rightAxis.setDrawGridLines(false);
            //chart.getAxisRight().setEnabled(false);
        }

        /**
         * DataSet
         */
        /////////////////////////////////////////////////////
        CombinedData data = new CombinedData();
        data.setData(generateBarData());
        data.setData(generateLineData());

        //xAxis.setAxisMaximum(data.getXMax() + 0.5f);
        /////////////////////////////////////////////////////
        chart.setData(data);
        chart.invalidate();

        System.out.println(chart.getYChartMax());
        chart.setY(chart.getYChartMax()+1);
        //chart.animateX(1000);

    }

    private LineData generateLineData(){
        LineData d = new LineData();

        LineDataSet set = new LineDataSet(V_SUM, "방문인원");

        //set.setDrawIcons(false);
        set.setDrawValues(true);

        // draw dashed line
        set.enableDashedLine(10f, 5f, 0f);

        // black lines and points
        set.setColor(Color.parseColor("#FFA1B4DC"));
        set.setCircleColor(Color.parseColor("#FFA1B4DC"));

        // line thickness and point size
        set.setLineWidth(2f);
        set.setCircleRadius(3f);


        // draw points as solid circles
        set.setDrawCircleHole(false);

        // customize legend entry
        set.setFormLineWidth(1f);
        //set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        //set.setFormSize(15.f);

        // text size of values
        set.setValueTextSize(9f);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData(){
        BarDataSet set1 = new BarDataSet(V_A1, "10 이하");
        set1.setColor(Color.rgb(255, 102, 51));
        set1.setValueTextColor(Color.rgb(255, 102, 51));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setDrawValues(false);

        BarDataSet set2 = new BarDataSet(V_A2, "2~30대");
        set2.setColors(Color.rgb(165, 165, 165));
        set2.setValueTextColor(Color.rgb(165, 165, 165));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setDrawValues(false);

        BarDataSet set3 = new BarDataSet(V_A3, "4~50대");
        set3.setColors(Color.rgb(255, 192, 0));
        set3.setValueTextColor(Color.rgb(255, 192, 0));
        set3.setValueTextSize(10f);
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);
        set3.setDrawValues(false);

        BarDataSet set4 = new BarDataSet(V_A4, "60 이상");
        set4.setColors(Color.rgb(68, 114, 196));
        set4.setValueTextColor(Color.rgb(68, 114, 196));
        set4.setValueTextSize(10f);
        set4.setAxisDependency(YAxis.AxisDependency.LEFT);
        set4.setDrawValues(false);

        float groupSpace = 0.2f;
        float barSpace = 0.02f; // x4 dataset
        float barWidth = 0.18f; // x4 dataset
        // groupSpace + (barSpace * dataset.count) + (barWidth * dataset.count) == 1


        BarData d = new BarData(set1, set2, set3, set4);
        d.setBarWidth(barWidth);

        d.groupBars(0.5f, groupSpace, barSpace); // start at x = 0



        return d;
    }

}
