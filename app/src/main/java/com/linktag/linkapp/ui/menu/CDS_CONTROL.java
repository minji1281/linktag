package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CDS_Model;
import com.linktag.linkapp.model.CTDS_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;

import java.lang.reflect.Method;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CDS_CONTROL {
    /**
     *
     * @param mContext Context
     * @param mClass Class
     * @param retMethodName return Method Name
     * @param GUBUN GUBUN
     * @param adminID adminID(CTD_07)
     * @param scanCode scancode
     * @param CDS_03 emptyValue
     * @param CTD_01 Contract ID
     * @param CTD_02 Service ID
     * @param type S : shared, P : private, O : open
     * @param CDS_98 OCM_01
     */
    public static void requestCDS_CONTROL(Context mContext, Class mClass, String retMethodName, String GUBUN, String adminID, String scanCode, String CDS_03, String CTD_01, String CTD_02, String type, String CDS_98) {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CDS_Model> call = Http.cds(HttpBaseService.TYPE.POST).CDS_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                adminID,
                scanCode,
                CDS_03,
                CTD_01,
                CTD_02,
                type,
                CDS_98
        );

        call.enqueue(new Callback<CDS_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CDS_Model> call, Response<CDS_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            Response<CDS_Model> response = (Response<CDS_Model>) msg.obj;

                            getMethod(mContext, mClass, retMethodName, true, response.body(), "");

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CDS_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                getMethod(mContext, mClass, retMethodName, false, null, t.getMessage());
            }
        });

    }

    private static void getMethod(Context mContext, Class mClass, String retMethodName, boolean bool, CDS_Model data, String exceptionStr){
        try {
//            public
//            Method m = mClass.getMethod(strMethod, boolean.class, Object.class, String.class);
//            m.invoke(mClass.newInstance(), bool, data, exceptionStr);

            // private
            Method m = mClass.getDeclaredMethod(retMethodName, boolean.class, CDS_Model.class, String.class);
            m.setAccessible(true);
            m.invoke(mClass.newInstance(), bool, data, exceptionStr);

        } catch (Exception e){
//            System.out.println("@@@@@@@@@@@@@@@@@@@EXEXEXEXEX : " + e.getStackTrace());
//            System.out.println("@@@@@@@@@@@@@@@@@@@EXEXEXEXEX : " + e.getMessage());
//            System.out.println("@@@@@@@@@@@@@@@@@@@EXEXEXEXEX : " + e.getCause());
            Toast.makeText(mContext, R.string.common_exception, Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
