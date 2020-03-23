package com.linktag.linkapp.ui.settings_main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.settings.SettingsKey;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ExpandableHeightGridView;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.menu.ServiceAdapter;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Settings extends BaseActivity {
    //===================================
    // Layout
    //===================================
//    private Switch swIsOnline;
    private BaseHeader header;

    private Switch swIsNotice;
    private RelativeLayout layLanguage;
    private TextView tvLanguage;

    private ExpandableHeightGridView gridUseService;
    private ExpandableHeightGridView gridNotUseService;

    //===================================
    // Variable
    //===================================
    private ServiceAdapter mAdapter1;
    private ArrayList<CtdVO> mList1;

    private ServiceAdapter mAdapter2;
    private ArrayList<CtdVO> mList2;

    private String[] lan_name;
    private String[] lan_code;
    private int lan_index = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        initLayout();

        initialize();

        setTextLanguage();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

//        swIsOnline = findViewById(R.id.swIsOnline);
//        swIsOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked){
//                    mSettings.Value.isOnline = true;
//                    mSettings.putBooleanItem(SettingsKey.isOnline, mSettings.Value.isOnline);
//                } else {
//                    mSettings.Value.isOnline = false;
//                    mSettings.putBooleanItem(SettingsKey.isOnline, mSettings.Value.isOnline);
//                }
//            }
//        });
        swIsNotice = findViewById(R.id.swIsNotice);
        swIsNotice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                requestOCM_CONTROL(isChecked);
            }
        });

        layLanguage = findViewById(R.id.layLanguage);
        layLanguage.setOnClickListener(v -> onSelectLanguage());
        tvLanguage = findViewById(R.id.tvLanguage);

        gridUseService = findViewById(R.id.gridUseService);
        gridUseService.setExpanded(true);
        gridUseService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //onServiceGridClick(position);
            }
        });
        gridNotUseService = findViewById(R.id.gridNotUseService);
        gridNotUseService.setExpanded(true);
        gridNotUseService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //onServiceGridClick(position);
            }
        });
    }

    @Override
    protected void initialize() {
        if(mUser.Value.OCM_24.equals("Y"))
            swIsNotice.setChecked(true);
        else
            swIsNotice.setChecked(false);

        mList1 = new ArrayList<>();
        mList2 = new ArrayList<>();

        mAdapter1 = new ServiceAdapter(mContext, mList1);
        mAdapter2 = new ServiceAdapter(mContext, mList2);

        gridUseService.setAdapter(mAdapter1);
        gridNotUseService.setAdapter(mAdapter2);
    }

    @Override
    public void onResume() {
        super.onResume();

        requestCTD_SELECT();
    }

    private void onSelectLanguage(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.setting_07);
        alertDialogBuilder.setSingleChoiceItems(R.array.language_name, lan_index,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        if(lan_index != index){

                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setMessage(R.string.alert_lan_change1);
                            builder.setCancelable(true);
                            builder.setPositiveButton(R.string.common_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog2, int id) {
                                    setLanguage(index);

                                    dialog2.dismiss();
                                }
                            });
                            builder.setNegativeButton(R.string.common_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog2, int id) {
                                    dialog2.dismiss();
                                    dialog.dismiss();
                                }
                            });
                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog2) {
                                    dialog2.dismiss();
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                    }
                });
        alertDialogBuilder.create().show();
    }

    private void setLanguage(int index){
        mSettings.Value.myLocale = lan_code[index];
        mSettings.putStringItem(SettingsKey.myLocale, mSettings.Value.myLocale);

        lan_index = index;
        tvLanguage.setText(lan_name[index]);

        // 언어 변경
        Locale locale = new Locale(lan_code[index]);
        //System.out.println("@@@@@@@@@@@ :" + locale.getDisplayName());
        Configuration config = new Configuration();
        config.locale = locale;

        mActivity.getBaseContext().getResources().updateConfiguration(config, mActivity.getBaseContext().getResources().getDisplayMetrics());

        reStart();
    }

    private void reStart(){
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(mContext.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        mContext.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }


    private void setTextLanguage(){
        lan_name = getResources().getStringArray(R.array.language_name);
        lan_code = getResources().getStringArray(R.array.language_code);

        for (int i = 0; i<lan_code.length; i++){
            if(mSettings.Value.myLocale.equals(lan_code[i])){
                lan_index = i;
                break;
            }
        }

        tvLanguage.setText(lan_name[lan_index]);
    }

    private void requestOCM_CONTROL(boolean checked){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, R.string.common_network_error, Toast.LENGTH_SHORT).show();
            return;
        }

        //openLoadingBar();

        String GUBUN = "UPDATE_PUSH";
        String OCM_01 = mUser.Value.OCM_01;
        String OCM_02 ="";
        String OCM_03 = "";
        String OCM_24;
        if(checked)
            OCM_24 = "Y";
        else OCM_24 = "N";

        String OCM_51 = "";
        String OCM_52 = "";
        String OCM_98 = mUser.Value.OCM_01;

        Call<OCM_Model> call = Http.ocm(HttpBaseService.TYPE.POST).OCM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                OCM_01,
                OCM_02,
                OCM_03,
                OCM_24,
                OCM_51,
                OCM_52,
                OCM_98
        );

        call.enqueue(new Callback<OCM_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<OCM_Model> call, Response<OCM_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            //closeLoadingBar();

                            Response<OCM_Model> response = (Response<OCM_Model>) msg.obj;

                            mUser.Value.OCM_24 = OCM_24;

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<OCM_Model> call, Throwable t){
                Log.d("OCM_CONTROL", t.getMessage());
                //closeLoadingBar();
                swIsNotice.toggle();
            }
        });

    }

    private void requestCTD_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_SELECT(
                BaseConst.URL_HOST,
                "LIST_USE",
                "",
                "",
                mUser.Value.OCM_01,
                ""
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
                            //closeLoadingBar();

                            Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;

                            mList1 = new ArrayList<>();
                            mList2 = new ArrayList<>();

                            for(int i=0; i < response.body().Total; i++){
                                if(response.body().Data.get(i).Validation){
                                    mList1.add(response.body().Data.get(i));
                                } else {
                                    mList2.add(response.body().Data.get(i));
                                }
                            }

                            mAdapter1.updateData(mList1);
                            mAdapter1.notifyDataSetChanged();

                            mAdapter2.updateData(mList2);
                            mAdapter2.notifyDataSetChanged();
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
}
