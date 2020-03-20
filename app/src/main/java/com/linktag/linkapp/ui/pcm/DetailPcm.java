package com.linktag.linkapp.ui.pcm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.LOG_Model;
import com.linktag.linkapp.model.PCDModel;
import com.linktag.linkapp.model.PCMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.master_log.MasterLog;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.LogVO;
import com.linktag.linkapp.value_object.PcdVO;
import com.linktag.linkapp.value_object.PcmVO;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPcm extends BaseActivity implements Serializable {


    private BaseHeader header;


    private RecyclerView recyclerView_hw;
    private RecyclerView recyclerView_sw;
    private LinearLayoutManager linearLayoutManager_HW;
    private LinearLayoutManager linearLayoutManager_SW;
    public static ArrayList<PcdVO> mList_HW;
    public static ArrayList<PcdVO> mList_SW;
    private PcdHwRecycleAdapter mHwAdapter;
    private PcdSwRecycleAdapter mSwAdapter;


    private EditText ed_name;
    private EditText ed_memo;

    private TextView tv_Log;

    private Spinner sp_sw;
    private EditText et_sw;
    private Spinner sp_hw;
    private EditText et_hw;
    private HashMap<String, String> map_hw = new HashMap<String, String>();
    private HashMap<String, String> map_sw = new HashMap<String, String>();

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private TextView tv_manageDay;

    public static TextView tv_hwCnt;
    public static TextView tv_swCnt;
    private Button btn_update;
    private Button bt_save;
    private Button btn_addItem_hw;
    private Button btn_addItem_sw;
    private PcmVO pcmVO;

    private Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    private CtdVO intentVO;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pcm);


        initLayout();

        initialize();

        if (getIntent().hasExtra("scanCode")) {
            intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
            btn_update.setVisibility(View.GONE);
            String date = formatDate.format(calendar.getTime());
            pcmVO.setPCM_04(date);
            tv_manageDay.setText(date.substring(0,4)+"."+date.substring(4,6)+"."+date.substring(6,8));
        }


    }

    public static void setmList_HW(ArrayList<PcdVO> mList_HW) {
        DetailPcm.mList_HW = mList_HW;
    }


    public void requestPCD_SELECT(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<PCDModel> call = Http.pcd(HttpBaseService.TYPE.POST).PCD_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                pcmVO.PCM_ID,
                pcmVO.PCM_01,
                ""
        );


        call.enqueue(new Callback<PCDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<PCDModel> call, Response<PCDModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<PCDModel> response = (Response<PCDModel>) msg.obj;

                            if (GUBUN.equals("LIST_HW")) {

                                mList_HW = response.body().Data;
                                if (mList_HW == null)
                                    mList_HW = new ArrayList<>();
                                tv_hwCnt.setText("("+mList_HW.size()+")");
                                mHwAdapter.updateData(mList_HW);
                                mHwAdapter.notifyDataSetChanged();
                            } else if (GUBUN.equals("LIST_SW")) {
                                mList_SW = response.body().Data;
                                if (mList_SW == null)
                                    mList_SW = new ArrayList<>();
                                tv_swCnt.setText("("+mList_SW.size()+")");
                                mSwAdapter.updateData(mList_SW);
                                mSwAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<PCDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    public void requestPCD_CONTROL(PcdVO pcdVO, String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<PCDModel> call = Http.pcd(HttpBaseService.TYPE.POST).PCD_CONTROL(
                BaseConst.URL_HOST,
                pcdVO.GUBUN,
                pcdVO.PCD_ID,
                pcdVO.PCD_01,
                pcdVO.PCD_02,
                pcdVO.PCD_03,
                pcdVO.PCD_04,
                pcdVO.PCD_05,
                pcdVO.PCD_98
        );


        call.enqueue(new Callback<PCDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<PCDModel> call, Response<PCDModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<PCDModel> response = (Response<PCDModel>) msg.obj;

                            if (GUBUN.equals("HW")) {
                                mList_HW = response.body().Data;
                                if (mList_HW == null)
                                    mList_HW = new ArrayList<>();
                                tv_hwCnt.setText("("+mList_HW.size()+")");
                                mHwAdapter.updateData(mList_HW);
                                mHwAdapter.notifyDataSetChanged();
                                et_hw.setText("");

                                requestLOG_CONTROL("2",getString(R.string.pcm_text5) +" " + pcdVO.PCD_05 + " " + getString(R.string.pcm_text7));

                            } else if (GUBUN.equals("SW")) {
                                mList_SW = response.body().Data;
                                if (mList_SW == null)
                                    mList_SW = new ArrayList<>();
                                tv_swCnt.setText("("+mList_SW.size()+")");
                                mSwAdapter.updateData(mList_SW);
                                mSwAdapter.notifyDataSetChanged();
                                et_sw.setText("");
                                requestLOG_CONTROL("2",getString(R.string.pcm_text6) +" "+ pcdVO.PCD_05 + " " + getString(R.string.pcm_text7));
                            }


                        }
                    }
                }.sendMessage(msg);

            }

            @Override
            public void onFailure(Call<PCDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
            }
        });

    }

    public void requestPCM_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(DetailPcm.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        Call<PCMModel> call = Http.pcm(HttpBaseService.TYPE.POST).PCM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                pcmVO.PCM_ID,
                pcmVO.PCM_01,
                ed_name.getText().toString(),
                ed_memo.getText().toString(),
                format1.format(calendar.getTime()),
                pcmVO.PCM_96,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                "N"
        );


        call.enqueue(new Callback<PCMModel>() {
            @Override
            public void onResponse(Call<PCMModel> call, Response<PCMModel> response) {


                if (GUBUN.equals("INSERT")) {
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, pcmVO.PCM_01);
                    ctds_control.requestCTDS_CONTROL();
                    tv_manageDay.setText(format1.format(calendar.getTime()));
                    onBackPressed();
                }
                if (GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")) {
                    Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "] " + getString(R.string.pcm_text1), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                if(GUBUN.equals("UPDATE_DATE")){
                    Toast.makeText(mContext,getString(R.string.pcm_text2), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PCMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


    public void onResume() {
        super.onResume();

        requestPCD_SELECT("LIST_HW");
        requestPCD_SELECT("LIST_SW");

    }


    @Override
    protected void initLayout() {


        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        recyclerView_hw = findViewById(R.id.recyclerView_hw);
        recyclerView_sw = findViewById(R.id.recyclerView_sw);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_memo = (EditText) findViewById(R.id.ed_memo);


        tv_hwCnt = findViewById(R.id.tv_hwCnt);
        tv_swCnt = findViewById(R.id.tv_swCnt);
        tv_Log = findViewById(R.id.tv_Log);


        sp_hw = (Spinner) findViewById(R.id.sp_hw);
        sp_sw = (Spinner) findViewById(R.id.sp_sw);

        String[] str = getResources().getStringArray(R.array.hw);
        final ArrayAdapter<String> adapter_hw = new ArrayAdapter<String>(mContext, R.layout.spinner_item, str);
        sp_hw.setAdapter(adapter_hw);


        map_hw.put(str[0], "0");
        map_hw.put(str[1], "1");
        map_hw.put(str[2], "2");
        map_hw.put(str[3], "3");
        map_hw.put(str[4], "4");
        map_hw.put(str[5], "5");
        map_hw.put(str[6], "6");
        map_hw.put(str[7], "7");
        map_hw.put(str[8], "8");
        map_hw.put(str[9], "9");
        map_hw.put(str[10], "10");

        str = getResources().getStringArray(R.array.sw);
        final ArrayAdapter<String> adapter_sw = new ArrayAdapter<String>(mContext, R.layout.spinner_item, str);
        sp_sw.setAdapter(adapter_sw);

        map_sw.put(str[0], "0");
        map_sw.put(str[1], "1");
        map_sw.put(str[2], "2");
        map_sw.put(str[3], "3");
        map_sw.put(str[4], "4");
        map_sw.put(str[5], "5");

        et_sw = (EditText) findViewById(R.id.et_sw);

        sp_hw = (Spinner) findViewById(R.id.sp_hw);
        et_hw = (EditText) findViewById(R.id.et_hw);

        btn_addItem_hw = (Button) findViewById(R.id.btn_addItem_hw);
        btn_addItem_sw = (Button) findViewById(R.id.btn_addItem_sw);
        tv_manageDay = (TextView) findViewById(R.id.tv_manageDay);
        btn_update = (Button) findViewById(R.id.btn_update);
        bt_save = (Button) findViewById(R.id.bt_save);

        pcmVO = (PcmVO) getIntent().getSerializableExtra("PcmVO");


        if (pcmVO.getPCM_04().equals("")) {
            tv_manageDay.setText("-");
        } else {
            String year = pcmVO.getPCM_04().substring(0, 4);
            String month = pcmVO.getPCM_04().substring(4, 6);
            String dayOfMonth = pcmVO.getPCM_04().substring(6, 8);
            tv_manageDay.setText(year + "." + month + "." + dayOfMonth);
        }


        ed_name.setText(pcmVO.getPCM_02());
        ed_memo.setText(pcmVO.getPCM_03());


        requestPCD_SELECT("LIST_HW");
        requestPCD_SELECT("LIST_SW");
    }

    @Override
    protected void initialize() {

        mList_HW = new ArrayList<>();
        linearLayoutManager_HW = new LinearLayoutManager(mContext);
        recyclerView_hw.setLayoutManager(linearLayoutManager_HW);
        mHwAdapter = new PcdHwRecycleAdapter(mContext, mList_HW);
        mHwAdapter.setmAdapter(mHwAdapter);

        recyclerView_hw.setAdapter(mHwAdapter);

        mList_SW = new ArrayList<>();
        linearLayoutManager_SW = new LinearLayoutManager(mContext);
        recyclerView_sw.setLayoutManager(linearLayoutManager_SW);
        mSwAdapter = new PcdSwRecycleAdapter(mContext, mList_SW);
        mSwAdapter.setmAdapter(mSwAdapter);

        recyclerView_sw.setAdapter(mSwAdapter);


        btn_addItem_hw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (map_hw.get(sp_hw.getSelectedItem()).equals("0")) {
                    Toast.makeText(mContext, getString(R.string.pcm_text3), Toast.LENGTH_SHORT).show();
                    return;
                }

                PcdVO pcdVO = new PcdVO();
                pcdVO.GUBUN = "INSERT";
                pcdVO.PCD_ID = pcmVO.PCM_ID;
                pcdVO.PCD_01 = pcmVO.PCM_01;
                pcdVO.PCD_02 = "";
                pcdVO.PCD_03 = "1";
                pcdVO.PCD_04 = map_hw.get(sp_hw.getSelectedItem());
                pcdVO.PCD_05 = et_hw.getText().toString();
                pcdVO.PCD_98 = mUser.Value.OCM_01;
                requestPCD_CONTROL(pcdVO, "HW");
            }
        });


        btn_addItem_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (map_sw.get(sp_sw.getSelectedItem()).equals("0")) {
                    Toast.makeText(mContext, getString(R.string.pcm_text3), Toast.LENGTH_SHORT).show();
                    return;
                }

                PcdVO pcdVO = new PcdVO();
                pcdVO.GUBUN = "INSERT";
                pcdVO.PCD_ID = pcmVO.PCM_ID;
                pcdVO.PCD_01 = pcmVO.PCM_01;
                pcdVO.PCD_02 = "";
                pcdVO.PCD_03 = "2";
                pcdVO.PCD_04 = map_sw.get(sp_sw.getSelectedItem());
                pcdVO.PCD_05 = et_sw.getText().toString();
                pcdVO.PCD_98 = mUser.Value.OCM_01;
                requestPCD_CONTROL(pcdVO, "SW");
            }
        });


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
                pcmVO.setPCM_04(format1.format(calendar.getTime()));
                requestPCM_CONTROL("UPDATE_DATE");
                String year = pcmVO.getPCM_04().substring(0, 4);
                String month = pcmVO.getPCM_04().substring(4, 6);
                String dayOfMonth = pcmVO.getPCM_04().substring(6, 8);
                String manageDay = year + "." + month + "." + dayOfMonth + ".";
                tv_manageDay.setText(manageDay);

                requestLOG_CONTROL("1",getString(R.string.pcm_text4));
            }
        });


        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( ed_name.getText().equals("")){
                    Toast.makeText(mContext,getString(R.string.validation_check1),Toast.LENGTH_LONG).show();
                    return;
                }

                if (getIntent().hasExtra("scanCode")) {
                    requestPCM_CONTROL("INSERT");
                } else {
                    requestPCM_CONTROL("UPDATE");
                }
            }
        });

        tv_Log.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LogVO LOG = new LogVO();
                LOG.LOG_ID = pcmVO.PCM_ID;
                LOG.LOG_01 = pcmVO.PCM_01;
                LOG.LOG_98 = mUser.Value.OCM_01;
                LOG.SP_NAME = "SP_PCML_CONTROL";

                Intent intent = new Intent(mContext, MasterLog.class);
                intent.putExtra("LOG", LOG);

                mContext.startActivity(intent);
            }
        });


        if (pcmVO.PCM_97.equals(mUser.Value.OCM_01)) { //작성자만 삭제버튼 보임
            header.btnHeaderRight1.setVisibility((View.VISIBLE));
            header.btnHeaderRight1.setMaxWidth(50);
            header.btnHeaderRight1.setMaxHeight(50);
            header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
            header.btnHeaderRight1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog();
                }
            });
        }
    }

    private void deleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(view);

        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        EditText etDeleteName = (EditText) view.findViewById(R.id.etDeleteName);

        AlertDialog dialog = builder.create();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(etDeleteName.getText().toString().equals(pcmVO.PCM_02)){
                    dialog.dismiss();
                    requestPCM_CONTROL("DELETE");
                }
                else{
                    Toast.makeText(mActivity, getResources().getString(R.string.common_confirm_delete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void requestLOG_CONTROL(String LOG_03, String LOG_04){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity,  getResources().getString(R.string.common_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Call<LOG_Model> call = Http.log(HttpBaseService.TYPE.POST).LOG_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                pcmVO.PCM_ID,
                pcmVO.PCM_01,
                "",
                LOG_03,
                LOG_04,
                "",
                mUser.Value.OCM_01,
                "SP_PCML_CONTROL"
        );

        call.enqueue(new Callback<LOG_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<LOG_Model> call, Response<LOG_Model> response){

            }

            @Override
            public void onFailure(Call<LOG_Model> call, Throwable t){
                Log.d("LOG_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });
    }


}
