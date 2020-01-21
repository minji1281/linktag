package com.linktag.linkapp.API;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.linktag.linkapp.model.WTH_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.WthVO;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.ClsDateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WeatherAPI extends AsyncTask<Location, Void, Void>{
    protected InterfaceUser mUser;

    private final String weatherApiURL = "http://api.openweathermap.org/data/2.5/weather?";
    private final String APPID = "d1b1fd5152a3801abd36dc0e29bc47b4";

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mUser = InterfaceUser.getInstance();
    }

    @Override
    protected Void doInBackground(Location... locations) {
        if(locations.length > 0){
            openWeatherAPI(locations[0]);

        }

        return null;
    }

    @Override
    protected void onPostExecute(Void voids){
        //System.out.println(voids);
    }

    private void requestWTH_CONTROL(String result){
        try{
            JSONObject json = new JSONObject(result);

            JSONArray arr = json.getJSONArray("weather");
            JSONObject obj = arr.getJSONObject(0);

            String weather = obj.getString("main");

            obj = json.getJSONObject("main");
            String temp = obj.getString("temp");
            String humidity = obj.getString("humidity");

            obj = json.getJSONObject("wind");
            String speed = obj.getString("speed");

            String fall = "0";

            if(weather.equals("Rain") || weather.equals("Snow")){
                String tmp;

                obj = json.getJSONObject(weather);

                tmp = obj.getString("1h");
                fall = tmp != null ? tmp : fall;

                tmp = obj.getString("3h");
                fall = tmp != null ? tmp : fall;
            }

            String WTH_03 = weather.equals("Clear") ? "1" :
                    weather.equals("Clouds") ? "2" :
                            weather.equals("Rain") || weather.equals("Snow") ? "3" : "4";


            Call<WTH_Model> call = Http.wth(HttpBaseService.TYPE.POST).WTH_CONTROL(
                    BaseConst.URL_HOST,
                    "INSERT",
                    mUser.Value.CTM_01,
                    mUser.Value.RUTC_01,
                    ClsDateTime.getNow("yyyyMMdd"),
                    ClsDateTime.getNow("HH"),
                    WTH_03,
                    Float.valueOf(temp),
                    Float.valueOf(speed),
                    Float.valueOf(humidity),
                    Float.valueOf(fall),
                    0
            );

            call.enqueue(new Callback<WTH_Model>(){
                @SuppressLint("HandlerLeak")
                @Override
                public void onResponse(Call<WTH_Model> call, Response<WTH_Model> response){
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 100;

                    new Handler(){
                        @Override
                        public void handleMessage(Message msg){
                            if(msg.what == 100){

                                Response<WTH_Model> response = (Response<WTH_Model>) msg.obj;

                                callBack(response.body().Data.get(0));
                            }
                        }
                    }.sendMessage(msg);
                }

                @Override
                public void onFailure(Call<WTH_Model> call, Throwable t){
                    mUser.Value.WeatherTimeCHK = false;
                }
            });



        } catch (JSONException e) {
            mUser.Value.WeatherTimeCHK = false;
        }
    }

    private void callBack(WthVO data){
        mUser.Value.WeatherTime.add(Calendar.HOUR, Integer.parseInt(data.WTH_03));
        mUser.Value.WeatherTimeCHK = false;
    }

    private void openWeatherAPI(Location location){

        try {
            BufferedReader br = null;

            String urlstr = weatherApiURL + "lat=" + location.getLatitude() +
                    "&lon=" + location.getLongitude() +
                    "&APPID=" + APPID;

            URL url = new URL(urlstr);
            HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();

            urlconnection.setRequestMethod("GET");
            br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));

            String response = "";
            String line;

            while((line = br.readLine()) != null){
                response = response + line + "\n";
            }

            if(response != null){
                requestWTH_CONTROL(response);
            }

        } catch (Exception e) {
            System.out.println(e);
            mUser.Value.WeatherTimeCHK = false;
        }
    }

}