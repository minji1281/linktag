package com.linktag.linkapp.ui.scanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.ClsAES;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.ChangeActivityCls;
import com.linktag.linkapp.ui.menu.ChooseOne;
import com.linktag.linkapp.value_object.CtdVO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanResult {
    private Context mContext;
    private InterfaceUser mUser;

    private boolean validation = false;
    private String str;

    private CtdVO intentVO;

    public ScanResult(Context mContext, String resultStr, CtdVO intentVO){
        this.mContext = mContext;
        this.str = resultStr;
        this.intentVO = intentVO;
        mUser = InterfaceUser.getInstance();
    }

    public void run(){
        String scanCode = "";

        try{
            String[] split = str.split("\\?");
            if(split[0].equals("http://www.linktag.io/scan")) {
                Uri uri = Uri.parse(str);

                String t = uri.getQueryParameter("t");
                String u = uri.getQueryParameter("u");
                String s = uri.getQueryParameter("s");

                ClsAES aes = new ClsAES();
                String dec = aes.Decrypt(aes.Base64Decoding(s));

                if (t.length() == 2 && u.length() == 10 && dec.length() == 15) {
                    validation = true;
                    scanCode = t + u + dec;
                }
            }
        } catch (Exception e) {
            validation = false;
        }

        if(validation){
            requestCTD_SELECT(scanCode);
        } else {
            Toast.makeText(mContext, "유효하지 않은 코드 입니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void requestCTD_SELECT(String scanCode) {
        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_SELECT(
                BaseConst.URL_HOST,
                "DETAIL_CTDS",
                "",
                "",
                mUser.Value.OCM_01,
                scanCode
        );

        call.enqueue(new Callback<CTD_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CTD_Model> call, Response<CTD_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){

                            Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;

                            callBack(response.body(), scanCode);

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CTD_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                //closeLoadingBar();
            }
        });
    }

    private void callBack(CTD_Model model, String scanCode){
        System.out.println("###############");
        System.out.println(scanCode);
        if(model.Data.get(0).Validation){

            if (model.Data.get(0).ErrorCode.equals("002")) {
                Toast.makeText(mContext, " 사용중인 코드 입니다.", Toast.LENGTH_LONG).show();
            } else {
                // Detail
                // Detail 조회 페이지 이동
                ChangeActivityCls changeActivityCls = new ChangeActivityCls(mContext, model.Data.get(0));
                changeActivityCls.changeServiceWithScan(scanCode);
            }
        } else {
            // New
            // 선택 페이지 이동

            /*
            if(intentVO == null){

            } else {

            }


             */
            Intent intent = new Intent(mContext, ChooseOne.class);
            intent.putExtra("type", "SCAN");
            intent.putExtra("scanCode", scanCode);
            mContext.startActivity(intent);
        }

    }

}
