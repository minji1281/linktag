package com.linktag.linkapp.ui.car;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CARModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CarInfo extends AsyncTask<Void, Void, Void> {
    protected InterfaceUser mUser;

    private Activity mActivity;
    private Spinner spCar;
    private String spName;
    private String ID;
    private String value;

    private ArrayList<CarSpinnerList> carList;

    public CarInfo(ArrayList<CarSpinnerList> carList, Activity mActivity, String spName, String ID, String value){
        this.mActivity = mActivity;
        this.carList = carList;
        this.spName = spName;
        this.ID = ID;
        this.value = value;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mUser = InterfaceUser.getInstance();
        spCar = mActivity.findViewById(mActivity.getResources().getIdentifier(spName, "id", mActivity.getPackageName()));
    }

    @Override
    protected Void doInBackground(Void... voids) {
        requestCAR_SELECT();

        return null;
    }

    @Override
    protected void onPostExecute(Void voids){
        //System.out.println(voids);
    }

    private void requestCAR_SELECT(){
        Call<CARModel> call = Http.car(HttpBaseService.TYPE.POST).CAR_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                ID,
                ""
        );

        call.enqueue(new Callback<CARModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CARModel> call, Response<CARModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            Response<CARModel> response = (Response<CARModel>) msg.obj;
                            String[] ar = new String[response.body().Total];
                            String[] ar2 = new String[response.body().Total];
                            ArrayAdapter<String> adapter;
                            ArrayAdapter<String> adapter2;
                            carList.clear();

                            if(response.body().Total > 0){
                                for(int i = 0; i < response.body().Total; i++){
                                    carList.add(new CarSpinnerList(response.body().Data.get(i).CAR_01, response.body().Data.get(i).CAR_02, response.body().Data.get(i).CAR_03, response.body().Data.get(i).CAR_04));

                                    ar[i] = response.body().Data.get(i).CAR_02;
                                    ar2[i] = response.body().Data.get(i).CAR_01;
                                }
                            }

                            adapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item5, ar);
                            adapter2 = new ArrayAdapter<>(mActivity, R.layout.spinner_item5, ar2);
                            adapter.setDropDownViewResource(R.layout.spinner_item5);
                            spCar.setAdapter(adapter);
                            int i = adapter2.getPosition(value);
                            if(i != -1){
                                spCar.setSelection(i);
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CARModel> call, Throwable t){

            }
        });
    }

}
