package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.content.Intent;

import com.linktag.linkapp.ui.jdm.JDMMain;
import com.linktag.linkapp.ui.jdm.JDMMain;
import com.linktag.linkapp.ui.pot.PotList;

public class ChangeActivityCls {
    private Context mContext;
    private String SVC_01;
    private String CTN_02;
    Intent intent;

    public ChangeActivityCls(Context mContext, String SVC_01, String CTN_02){
        this.mContext = mContext;
        this.SVC_01 = SVC_01;
        this.CTN_02 = CTN_02;
    }

    public void changeService(){
        if(!SVC_01.equals("") && !SVC_01.equals(null))
        {
            boolean chk = false;

            switch(SVC_01){
                case "JDM1":
                    intent = new Intent(mContext, JDMMain.class);
                    chk = true;
                    break;
                case "POT1":
                    intent = new Intent(mContext, PotList.class);
                    chk = true;
                    break;
                default:
                    break;
            }

            if(chk){
                intent.putExtra("SVC_01", SVC_01);
                intent.putExtra("CTN_02", CTN_02);
                mContext.startActivity(intent);
            }
        }

    }

}
