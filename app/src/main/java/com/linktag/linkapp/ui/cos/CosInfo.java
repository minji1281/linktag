package com.linktag.linkapp.ui.cos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.COSModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.spinner.SpinnerList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CosInfo extends AsyncTask<Void, Void, Void> {
    protected InterfaceUser mUser;

    private Activity mActivity;
    private Spinner spCos;
    private String spName; //spinner명
    private String gubun; //sp구분값
    private String ID; //컨테이너
    private String value; //spinner선택값
    private String gubun2; //List,Detail구분(L,D)

    private ArrayList<SpinnerList> cosList;

    public CosInfo(ArrayList<SpinnerList> cosList, Activity mActivity, String spName, String gubun, String ID, String value, String gubun2){
        this.mActivity = mActivity;
        this.cosList = cosList;
        this.spName = spName;
        this.gubun = gubun;
        this.ID = ID;
        this.value = value;
        this.gubun2 = gubun2;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mUser = InterfaceUser.getInstance();
        spCos = mActivity.findViewById(mActivity.getResources().getIdentifier(spName, "id", mActivity.getPackageName()));
    }

    @Override
    protected Void doInBackground(Void... voids) {
        requestCOS_SELECT();

        return null;
    }

    @Override
    protected void onPostExecute(Void voids){
        //System.out.println(voids);
    }

    private void requestCOS_SELECT(){
        Call<COSModel> call = Http.cos(HttpBaseService.TYPE.POST).COS_SELECT(
                BaseConst.URL_HOST,
                gubun,
                ID,
                ""
        );

        call.enqueue(new Callback<COSModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<COSModel> call, Response<COSModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            Response<COSModel> response = (Response<COSModel>) msg.obj;
                            String[] ar = new String[response.body().Total];
                            String[] ar2 = new String[response.body().Total];
                            ArrayAdapter<String> adapter;
                            ArrayAdapter<String> adapter2;
                            cosList.clear();

                            if(response.body().Total > 0){
                                for(int i = 0; i < response.body().Total; i++){
                                    cosList.add(new SpinnerList(response.body().Data.get(i).COS_01, response.body().Data.get(i).COS_02, response.body().Data.get(i).COS_03));

                                    ar[i] = response.body().Data.get(i).COS_02;
                                    ar2[i] = response.body().Data.get(i).COS_01;
                                }
                            }


                            if(gubun2.equals("L")){
                                adapter = new ArrayAdapter<>(mActivity, R.layout.spinner_item_list, ar);
                                adapter2 = new ArrayAdapter<>(mActivity, R.layout.spinner_item_list, ar2);
                                adapter.setDropDownViewResource(R.layout.spinner_item_list);
                            }
                            else{ //D
                                adapter = new ArrayAdapter<>(mActivity, R.layout.spinner_detail_item, ar);
                                adapter2 = new ArrayAdapter<>(mActivity, R.layout.spinner_detail_item, ar2);
                                adapter.setDropDownViewResource(R.layout.spinner_detail_item);
                            }

                            spCos.setAdapter(adapter);
                            int i = adapter2.getPosition(value);
                            if(i != -1){
                                spCos.setSelection(i);
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<COSModel> call, Throwable t){

            }
        });
    }

}
