package com.linktag.linkapp.ui.cos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.COSModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.linktag.linkapp.ui.air.CodDetail;


public class CosInfo extends AsyncTask<Void, Void, Void> {
    protected InterfaceUser mUser;

    private Activity mActivity;
    private Spinner spCos;

    private ArrayList<CosList> cosList;

    public CosInfo(ArrayList<CosList> cosList, Activity mActivity){
        this.mActivity = mActivity;
        this.cosList = cosList;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mUser = InterfaceUser.getInstance();
        spCos = mActivity.findViewById(mActivity.getResources().getIdentifier("spHeaderRight", "id", mActivity.getPackageName()));
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
                "LIST",
                "1", //컨테이너 수정해야돼!!!
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
//                            int EMP_08s = Integer.valueOf(EMP_08.getText().toString());

//                            String[] ar = new String[response.body().Total + 1];
//                            ar[0] = "";
                            String[] ar = new String[response.body().Total];
//                            ar[0] = "전체";
                            ArrayAdapter<String> adapter;
                            cosList.clear();

                            if(response.body().Total > 0){
                                for(int i = 0; i < response.body().Total; i++){
                                    cosList.add(new CosList(response.body().Data.get(i).COS_01, response.body().Data.get(i).COS_02));

                                    ar[i] = response.body().Data.get(i).COS_02;
                                }
                            }

                            adapter = new ArrayAdapter<String>(mActivity, R.layout.spinner_item2, ar){
//                                @Override
//                                public boolean isEnabled(int position){
//                                    if(iar[position] > EMP_08s)
//                                        return false;
//                                    else return true;
//                                }

//                                @Override
//                                public View getDropDownView(int position, View convertView, ViewGroup parent){
//                                    View view = super.getDropDownView(position, convertView, parent);
//                                    TextView tv = (TextView) view;
//
////                                    if(iar[position] > EMP_08s){
////                                        tv.setTextColor(Color.LTGRAY);
////                                    }
////                                    else {
////                                        tv.setTextColor(Color.BLACK);
////                                    }
////                                    return view;
//                                }

                            };
                            adapter.setDropDownViewResource(R.layout.spinner_item2);
                            spCos.setAdapter(adapter);

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
