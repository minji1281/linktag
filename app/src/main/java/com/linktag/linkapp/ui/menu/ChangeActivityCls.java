package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CtdVO;

public class ChangeActivityCls {
    private Context mContext;
    private CtdVO ctdVO;

    private InterfaceUser mUser;

    // 서비스 생성할때 LINK.DBO.T_SVCL 테이블에 추가 해주기
    public ChangeActivityCls(Context mContext, CtdVO ctdVO){
        this.mContext = mContext;
        this.ctdVO = ctdVO;
        mUser = InterfaceUser.getInstance();
    }

    // Menu에서 change
    public void changeService(){
        if(!ctdVO.SVCL_04.equals("") && ctdVO.SVCL_04 != null)
        {
            String packageName = mContext.getPackageName();

            try{
                Class cls = Class.forName(packageName + ctdVO.SVCL_04);
                Intent intent = new Intent(mContext, cls);

                intent.putExtra("intentVO", ctdVO);
                //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);

            } catch (Exception e){
                e.printStackTrace();
            }

        }
        else {
            Toast.makeText(mContext, mContext.getString(R.string.alert_service_location_error) + "\n" + mContext.getString(R.string.common_call_admin), Toast.LENGTH_LONG).show();
        }
    }

    //ChooseOne에서 change
    public void changeServiceWithScan(String scanCode){

            String packageName = mContext.getPackageName();

            try{
                // List 액티비티 실행
                Class clsList = Class.forName(packageName + ctdVO.SVCL_04);

                Intent intent = new Intent(mContext, clsList);

                intent.putExtra("scanCode", scanCode);
                intent.putExtra("intentVO", ctdVO);
                //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                mContext.startActivity(intent);

                /*
                // Detail 액티비티 실행
                Class clsDetail = Class.forName(packageName + ctdVO.SVCL_05);
                intent.setClass(mContext, clsDetail);

                mContext.startActivity(intent);
*/
            } catch (Exception e){
                e.printStackTrace();
            }

//        else {
//            Toast.makeText(mContext, mContext.getString(R.string.alert_service_location_error) + "\n" + mContext.getString(R.string.common_call_admin), Toast.LENGTH_LONG).show();
//        }

    }

}
