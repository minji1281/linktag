package com.linktag.linkapp.API;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.TextView;

import com.linktag.linkapp.model.CFB_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.main.ClovaFaceAdapter;
import com.linktag.linkapp.ui.main.Main;
import com.linktag.linkapp.value_object.CfbVO;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.ClsDateTime;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ClovaFace extends AsyncTask<File, Void, Void> {
    private final String API_TYPE_FACE = "FACE";
    private final String API_TYPE_CELEB = "CELEB";

    private String clientId = "rpxfpac5od";        // 애플리케이션 클라이언트 아이디값";
    private String clientSecret = "gZNo7g47P9uUu17M9ezVJbYvI0eCOvcppRfYKJcF";    // 애플리케이션 클라이언트 시크릿값";
    private String faceApiURL = "https://naveropenapi.apigw.ntruss.com/vision/v1/face";      // 얼굴감지 URL
    private String celebApiURL = "https://naveropenapi.apigw.ntruss.com/vision/v1/celebrity";     // 유명인 얼굴인식
    private String paramName = "image";       // 파라미터명은 image로 지정

    protected InterfaceUser mUser;

    private String apiURL;
    private String type;

    private String date;    // ddHHMMSS
    private float lat = 0;
    private float lng = 0;

    private boolean isError = false;

    private ClovaFaceAdapter mAdapter;
    private ArrayList<CfbVO> mList;
    private TextView listviewCNT;

    private CombinedChart chart;

    public ClovaFace(){this.type = API_TYPE_FACE;}

    /**
     * @param FACEorCELEB   -> "FACE" : 얼굴감지,  "CELEB" : 유명인 얼굴 인식
     * @param mAdapter      -> 업데이트 할 어뎁터
     * @param listviewCNT   -> 업데이트 할 리스트뷰 카운터
     * @param chart         -> 업데이트 할 차트
     */
    public ClovaFace(String FACEorCELEB, ClovaFaceAdapter mAdapter, TextView listviewCNT, CombinedChart chart) {
        this.type = FACEorCELEB;
        this.mAdapter = mAdapter;
        this.listviewCNT = listviewCNT;
        this.chart = chart;

        if (type.equals(API_TYPE_CELEB))
            this.apiURL = celebApiURL;
        else
            this.apiURL = faceApiURL;

    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mUser = InterfaceUser.getInstance();
    }

    @Override
    protected Void doInBackground(File... files) {

        if (files.length > 0) {
            String result;

            //for(int i=0; i<files.length; i++){

            if (files != null) {
                String[] fName = files[0].getName().split("_");

                date = fName[0];
                lat = Float.parseFloat(fName[1]);
                lng = Float.parseFloat(fName[2]);

                CFRApi(files[0]);
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void voids) {
        //System.out.println(voids);

    }

    private void CFRApi(File file) {
        String result = null;

        try {
            //String imgFile = "이미지 파일 경로 ";
            //File uploadFile = new File(imgFile);

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(false);
            con.setDoOutput(true);
            con.setDoInput(true);

            // multipart request
            String boundary = "---" + System.currentTimeMillis() + "---";
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            OutputStream outputStream = con.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
            String LINE_FEED = "\r\n";

            // file 추가
            String fileName = file.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            FileInputStream inputStream = new FileInputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            inputStream.close();
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
            BufferedReader br = null;

            int responseCode = con.getResponseCode();

            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                System.out.println("error!!!!!!! responseCode= " + responseCode);
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }

            String inputLine;

            if (br != null) {
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();

                // Success
                isError = false;
                result = response.toString();

                if (result != null) {
                    requestCFB_CONTROL(result);
                    System.out.println("$$$$$$$$$$$$1");
                }

            } else {

                // Error
                isError = true;
                result = String.valueOf(responseCode);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void requestCFB_CONTROL(String result) {

        try {
            JSONObject json = new JSONObject(result);
            if (json != null) {

                //System.out.println("$$$$$$$$$$$$");
                System.out.println("$$$$$$$$$$$$2");
                JSONArray arr = json.getJSONArray("faces");

                System.out.println(arr);
                if (arr.length() > 0) {

                    JSONObject obj = arr.getJSONObject(0);

                    JSONObject val = obj.getJSONObject("gender");
                    String gender = val.getString("value");

                    val = obj.getJSONObject("age");
                    //System.out.println("$$$$$$$$$$$$1");
                    String[] age = val.getString("value").split("~");
                    //System.out.println("$$$$$$$$$$$$2");
                    int avgAge = Math.round(((Integer.parseInt(age[0])) + Integer.parseInt(age[1])) / 2);
                    //System.out.println("$$$$$$$$$$$$3");

//                    System.out.println("$$$$$$$$$$$$3");

                    if(!gender.equals("")) { // #016 클로버 분석 후 제대로 값이 안나오는 것을 막기 위해
                        Call<CFB_Model> call = Http.cfb(HttpBaseService.TYPE.POST).CFB_CONTROL(
                                BaseConst.URL_HOST,
                                "INSERT",
                                mUser.Value.CTM_01,
                                "",
                                mUser.Value.RUTC_01,
                                gender.equals("male") ? "M" : gender.equals("female") ? "F" : "",
                                avgAge,
                                ClsDateTime.getNow("yyyyMMdd"),
                                date.substring(2),
                                lat,
                                lng,
                                mUser.Value.OCM_01
                        );
                        System.out.println("$$$$$$$$$$$$4");
                        call.enqueue(new Callback<CFB_Model>() {
                            @SuppressLint("HandlerLeak")
                            @Override
                            public void onResponse(Call<CFB_Model> call, Response<CFB_Model> response) {
                                Message msg = new Message();
                                msg.obj = response;
                                msg.what = 100;
                                System.out.println("$$$$$$$$$$$$5");
                                new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (msg.what == 100) {

                                            Response<CFB_Model> response = (Response<CFB_Model>) msg.obj;

                                            mList = response.body().Data;
                                            System.out.println("$$$$$$$$$$$$6");
                                            if (mList != null) {
                                                mAdapter.addData(mList);
                                                mAdapter.notifyDataSetChanged();

                                                String cnt = "총 " + mAdapter.getCount() + "건";
                                                SpannableString sb = new SpannableString(cnt);
                                                sb.setSpan(new ForegroundColorSpan(Color.RED), 2, sb.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                                sb.setSpan(new RelativeSizeSpan(1.4f), 2, sb.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                                listviewCNT.setText(sb);
                                                System.out.println("$$$$$$$$$$$$7");
                                                addChartData(mList);
                                            }
                                        }
                                    }
                                }.sendMessage(msg);
                            }

                            @Override
                            public void onFailure(Call<CFB_Model> call, Throwable t) {

                            }
                        });
                    }
                }
            }


        } catch (JSONException e) {

        }
    }

    private void addChartData(ArrayList<CfbVO> list) {
        System.out.println("$$$$$$$$$$$$6");
        if (list.size() > 0) {
            List<ILineDataSet> line = chart.getLineData().getDataSets();
            List<IBarDataSet> bar = chart.getBarData().getDataSets();
            float YMax = line.get(0).getYMax();

            float hour = Float.valueOf(list.get(0).CFB_06.substring(0, 2));
            int group = list.get(0).CFB_04;
            if (group < 20)
                group = 1;
            else if (20 <= group && group < 40)
                group = 2;
            else if (40 <= group && group < 60)
                group = 3;
            else
                group = 4;

            // Line
            for (ILineDataSet e : line) {
                List<Entry> a = e.getEntriesForXValue(hour);

                if (!a.isEmpty()) {
                    a.get(0).setY(a.get(0).getY() + 1);
                    YMax = getYMax(a.get(0).getY() + 1);
                }
            }

            // Bar
            if (group == 1) {
                bar.get(0).getEntryForIndex((int) hour - 1).setY(bar.get(0).getEntryForIndex((int) hour - 1).getY() + 1);
            } else if (group == 2) {
                bar.get(1).getEntryForIndex((int) hour - 1).setY(bar.get(1).getEntryForIndex((int) hour - 1).getY() + 1);
            } else if (group == 3) {
                bar.get(2).getEntryForIndex((int) hour - 1).setY(bar.get(2).getEntryForIndex((int) hour - 1).getY() + 1);
            } else {
                bar.get(3).getEntryForIndex((int) hour - 1).setY(bar.get(3).getEntryForIndex((int) hour - 1).getY() + 1);
            }

            chart.getAxisLeft().setAxisMaximum(YMax);
            chart.getAxisRight().setAxisMaximum(YMax);

            chart.notifyDataSetChanged();
            chart.invalidate();
        }
    }

    private float getYMax(float dataYMax){
        float chartMaxY = chart.getAxisLeft().getAxisMaximum();

        if(dataYMax < chartMaxY)
            return chartMaxY;
        else return chartMaxY + 2;
    }

}

