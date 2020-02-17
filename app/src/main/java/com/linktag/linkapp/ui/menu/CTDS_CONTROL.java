package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTDS_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CTDS_CONTROL {
    private Context mContext;
    private InterfaceUser mUser;

    private String CTM_01;
    private String CTN_02;
    private String scanCode;

    public CTDS_CONTROL(Context mContext, String CTM_01, String CTN_02, String scanCode){
        this.mContext = mContext;
        this.CTM_01 = CTM_01;
        this.CTN_02 = CTN_02;
        this.scanCode = scanCode;
        mUser = InterfaceUser.getInstance();
    }

    public void requestCTDS_CONTROL() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTDS_Model> call = Http.ctds(HttpBaseService.TYPE.POST).CTDS_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                CTM_01,
                CTN_02,
                scanCode,
                "",
                mUser.Value.OCM_01
        );

        call.enqueue(new Callback<CTDS_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CTDS_Model> call, Response<CTDS_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            //Response<CTDS_Model> response = (Response<CTDS_Model>) msg.obj;

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CTDS_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }
}
