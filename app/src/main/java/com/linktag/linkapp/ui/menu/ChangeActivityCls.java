package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.linktag.linkapp.value_object.CtdVO;

public class ChangeActivityCls {
    private Context mContext;
    private CtdVO ctdVO;
    Intent intent;


    public ChangeActivityCls(Context mContext, CtdVO ctdVO){
        this.mContext = mContext;
        this.ctdVO = ctdVO;
    }

    public void changeService(){
        if(!ctdVO.SVCL_04.equals("") && ctdVO.SVCL_04 != null)
        {
            String packageName = mContext.getPackageName();

            try{
                Class cls = Class.forName(packageName + ctdVO.SVCL_04);
                intent = new Intent(mContext, cls);

                intent.putExtra("CTM_01", ctdVO.CTD_01);
                intent.putExtra("CTN_02", ctdVO.CTN_02);
                intent.putExtra("SVCL_04", ctdVO.SVCL_04);
                mContext.startActivity(intent);

            } catch (Exception e){
                e.printStackTrace();
            }

        }
        else {
            Toast.makeText(mContext, "해당 서비스의 경로를 찾을 수 없습니다.\n관리자에게 문의 바랍니다.", Toast.LENGTH_LONG).show();
        }

    }

}
