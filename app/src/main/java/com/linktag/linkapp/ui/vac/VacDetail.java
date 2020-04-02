package com.linktag.linkapp.ui.vac;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CDS_Model;
import com.linktag.linkapp.model.LOG_Model;
import com.linktag.linkapp.model.VACModel;
import com.linktag.linkapp.model.VADModel;
import com.linktag.linkapp.model.VAMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.master_log.MasterLog;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.LogVO;
import com.linktag.linkapp.value_object.VacVO;
import com.linktag.linkapp.value_object.VamVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.linktag.linkapp.ui.vac.VamRecycleAdapter.VAM_03;

public class VacDetail extends BaseActivity {

    private BaseHeader header;

    public static RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<VamVO> mList;
    private VamRecycleAdapter mAdapter;


    public static ImageView imageView;
    private EditText ed_name;
    private EditText ed_memo;
    private EditText ed_item;

    private Button btn_vadEdit;
    public static Spinner vadSpinner;
    public static ArrayList<SpinnerList> mSpinnerList = new ArrayList<>();

    private Button btn_check;

    private TextView tv_vaccineLog;
    public static TextView tv_vam_nodata;
    public static boolean alarmState = false;

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HHmm");

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private Button bt_save;
    private Button btn_addItem;

    private TextView tv_Log;
    public static TextView tv_vamCnt;
    public static VacVO vacVO;

    private Calendar calendar = Calendar.getInstance();


    private CtdVO intentVO;

    private String VAD_02;
    private String VAD_03;
    private String VAD_04;
    private String[] str;

    private String scanCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_vac);


        initLayout();

        initialize();

        if (getIntent().hasExtra("scanCode")) {
            header.btnHeaderRight1.setVisibility((View.GONE));
            intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
            scanCode = getIntent().getStringExtra("scanCode");

            btn_addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "기본정보 저장후 입력가능합니다.", Toast.LENGTH_LONG).show();
                }
            });

        }
    }


    public void onResume() {
        super.onResume();

        requestVAM_SELECT();
        requestVAD_SELECT();

    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);


        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        linearLayout = findViewById(R.id.linearLayout);

        tv_vaccineLog = findViewById(R.id.tv_vaccineLog);

        imageView = findViewById(R.id.imageView);
        ed_name = (EditText) findViewById(R.id.ed_name);
        ed_memo = (EditText) findViewById(R.id.ed_memo);
        ed_item = findViewById(R.id.ed_item);

        btn_vadEdit = findViewById(R.id.btn_vadEdit);

        btn_check = findViewById(R.id.btn_check);

        tv_Log = findViewById(R.id.tv_Log);
        tv_vam_nodata = findViewById(R.id.tv_vam_nodata);
        tv_vamCnt = findViewById(R.id.tv_vamCnt);
        bt_save = (Button) findViewById(R.id.bt_save);
        btn_addItem = (Button) findViewById(R.id.btn_addItem);

        vadSpinner = findViewById(R.id.vadSpinner);

        vacVO = (VacVO) getIntent().getSerializableExtra("VacVO");

        tv_vaccineLog.setText(vacVO.VAC_04);

        if (vacVO.ARM_03.equals("Y")) {
            imageView.setImageResource(R.drawable.alarm_state_on);
            alarmState = true;
        } else {
            imageView.setImageResource(R.drawable.alarm_state_off);
            alarmState = false;
        }

        ed_name.setText(vacVO.getVAC_02());
        ed_memo.setText(vacVO.getVAC_03());

    }


    @Override
    protected void initialize() {

        mList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new VamRecycleAdapter(mContext, mList);
        mAdapter.setmAdapter(mAdapter);

        recyclerView.setAdapter(mAdapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!alarmState) {
                    Toast.makeText(mContext, getString(R.string.trp_text2), Toast.LENGTH_LONG).show();
                    return;
                }

                if (vacVO.ARM_03.equals("Y")) {
                    imageView.setImageResource(R.drawable.alarm_state_off);
                    vacVO.setARM_03("N");
                } else if (vacVO.ARM_03.equals("N")) {
                    imageView.setImageResource(R.drawable.alarm_state_on);
                    vacVO.setARM_03("Y");
                }
            }
        });


        btn_addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ed_item.getText().toString().equals("")) {
                    Toast.makeText(mContext, "접종기관정보를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                VamVO vamVO = new VamVO();
                vamVO.VAM_ID = vacVO.VAC_ID;
                vamVO.VAM_01 = vacVO.VAC_01;
                vamVO.VAM_02 = "";
                vamVO.VAM_03 = ed_item.getText().toString();
                vamVO.VAM_98 = mUser.Value.OCM_01;
                requestVAM_CONTROL("INSERT", vamVO);
                requestLOG_CONTROL("2", "접종기관 추가");
            }
        });


        tv_Log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogVO LOG = new LogVO();
                LOG.LOG_ID = vacVO.VAC_ID;
                LOG.LOG_01 = vacVO.VAC_01;
                LOG.LOG_98 = mUser.Value.OCM_01;
                LOG.SP_NAME = "SP_VACL_CONTROL";

                Intent intent = new Intent(mContext, MasterLog.class);
                intent.putExtra("LOG", LOG);

                mContext.startActivity(intent);
            }
        });

        btn_vadEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VadEditDetail.class);

                intent.putExtra("VAC_ID", vacVO.VAC_ID);
                intent.putExtra("VAC_01", vacVO.VAC_01);
                intent.putExtra("ARM_03", vacVO.ARM_03);
                mContext.startActivity(intent);

            }
        });


        if (vacVO.VAC_97.equals(mUser.Value.OCM_01)) { //작성자만 삭제버튼 보임
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

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VAD_04.equals("")) {
                    Toast.makeText(mContext, "접종정보를 선택하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                vacVO.VAC_04 = VAD_04;
                tv_vaccineLog.setText(VAD_04);
                requestVAC_CONTROL("UPDATE_VAC_04");
                requestVAD_CONTROL();
                requestLOG_CONTROL("2", VAD_04 + "접종 완료");
            }
        });

        vadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                vadSpinner.setSelection(position);
                VAD_02 = mSpinnerList.get(position).getVAD_02();
                VAD_03 = mSpinnerList.get(position).getVAD_03();
                VAD_04 = mSpinnerList.get(position).getVAD_04();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ed_name.getText().equals("")) {
                    Toast.makeText(mContext, getString(R.string.validation_check1), Toast.LENGTH_LONG).show();
                    return;
                }

                if (scanCode != null) {
                    requestCDS_CONTROL(
                            "INSERT",
                            intentVO.CTD_07,
                            scanCode,
                            "",
                            intentVO.CTD_01,
                            intentVO.CTD_02,
                            intentVO.CTD_09,
                            mUser.Value.OCM_01);
                } else {
                    requestVAC_CONTROL("UPDATE");
                }

            }
        });


    }

    public void requestVAD_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<VADModel> call = Http.vad(HttpBaseService.TYPE.POST).VAD_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                vacVO.VAD_ID,
                vacVO.VAD_01,
                "-1"
        );


        call.enqueue(new Callback<VADModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VADModel> call, Response<VADModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VADModel> response = (Response<VADModel>) msg.obj;

                            if (response.body().Total > 0) {
                                str = new String[response.body().Total];
                            } else {
                                str = new String[1];
                            }

                            mSpinnerList.clear();
                            str[0] = "접종기관을 선택하세요";
                            mSpinnerList.add(new VacDetail.SpinnerList("","",""));
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item_list, str);
                            vadSpinner.setAdapter(adapter);

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VADModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }


    public void requestVAD_CONTROL() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<VADModel> call = Http.vad(HttpBaseService.TYPE.POST).VAD_CONTROL(
                BaseConst.URL_HOST,
                "DELETE",
                vacVO.VAC_ID,
                vacVO.VAC_01,
                VAD_02,
                VAD_03,
                "",
                "",
                "",
                ""
        );


        call.enqueue(new Callback<VADModel>() {
            @Override
            public void onResponse(Call<VADModel> call, Response<VADModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VADModel> response = (Response<VADModel>) msg.obj;

                            if (response.body().Total > 0) {
                                str = new String[response.body().Total];
                            } else {
                                str = new String[1];
                            }

                            mSpinnerList.clear();

                            if (response.body().Total > 0) {
                                for (int i = 0; i < response.body().Total; i++) {
                                    str[i] =  stringTodateFormat(response.body().Data.get(i).VAD_96) +" "+ response.body().Data.get(i).VAD_04;
                                    mSpinnerList.add(new VacDetail.SpinnerList(response.body().Data.get(i).VAD_02,
                                            response.body().Data.get(i).VAD_03,
                                            "["+VAM_03+"]" +"\n"+ response.body().Data.get(i).VAD_04));
                                }
                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item_list, str);
                                vadSpinner.setAdapter(adapter);
                            } else {
                                str[0] = "정보 없음";
                                mSpinnerList.add(new VacDetail.SpinnerList("","",""));
                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item_list, str);
                                vadSpinner.setAdapter(adapter);
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VADModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }

    public void requestVAC_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(VacDetail.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        Call<VACModel> call = Http.vac(HttpBaseService.TYPE.POST).VAC_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                vacVO.VAC_ID,
                vacVO.VAC_01,
                ed_name.getText().toString(),
                ed_memo.getText().toString(),
                vacVO.VAC_04,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                vacVO.ARM_03
        );


        call.enqueue(new Callback<VACModel>() {
            @Override
            public void onResponse(Call<VACModel> call, Response<VACModel> response) {

//                if (GUBUN.equals("INSERT")) {
//                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, vacVO.VAC_01);
//                    ctds_control.requestCTDS_CONTROL();
//                    onBackPressed();
//                }
                if (GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")) {
                    Toast.makeText(mContext, "[" + ed_name.getText().toString() + "]" + " " + "해당 접종정보가 저장되었습니다.", Toast.LENGTH_LONG).show();
                    onBackPressed();
                }
                if (GUBUN.equals("UPDATE_VAC_04")) {
                    Toast.makeText(mContext, "[" + vacVO.VAC_04 + "] 접종완료 하였습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<VACModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


    private void deleteDialog() {
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
                if (etDeleteName.getText().toString().equals(vacVO.VAC_02)) {
                    dialog.dismiss();
                    requestVAC_CONTROL("DELETE");
                } else {
                    Toast.makeText(mActivity, getString(R.string.common_confirm_delete), Toast.LENGTH_SHORT).show();
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



    private void requestCDS_CONTROL(String GUBUN, String CTD_07, String scanCode, String CDS_03, String CTD_01, String CTD_02, String CTD_09, String OCM_01){
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<CDS_Model> call = Http.cds(HttpBaseService.TYPE.POST).CDS_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CTD_07,
                scanCode,
                CDS_03,
                CTD_01,
                CTD_02,
                CTD_09,
                OCM_01
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

                            if(GUBUN.equals("INSERT")){
                                vacVO.VAC_01 = response.body().Data.get(0).CDS_03;
                                requestVAC_CONTROL("INSERT");
                                requestLOG_CONTROL("1", getString(R.string.log_new_text));
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CDS_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                Toast.makeText(mContext, R.string.common_exception, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void requestLOG_CONTROL(String LOG_03, String LOG_04) {
        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            Toast.makeText(mActivity, getString(R.string.common_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Call<LOG_Model> call = Http.log(HttpBaseService.TYPE.POST).LOG_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                vacVO.VAC_ID,
                vacVO.VAC_01,
                "",
                LOG_03,
                LOG_04,
                "",
                mUser.Value.OCM_01,
                "SP_VACL_CONTROL"
        );

        call.enqueue(new Callback<LOG_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<LOG_Model> call, Response<LOG_Model> response) {

            }

            @Override
            public void onFailure(Call<LOG_Model> call, Throwable t) {
                Log.d("LOG_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });
    }


    public void requestVAM_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<VAMModel> call = Http.vam(HttpBaseService.TYPE.POST).VAM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                vacVO.VAC_ID,
                vacVO.VAC_01
        );


        call.enqueue(new Callback<VAMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VAMModel> call, Response<VAMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            closeLoadingBar();

                            Response<VAMModel> response = (Response<VAMModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();

                            tv_vamCnt.setText("(" + mList.size() + ")");
                            if (mList.size() == 0) {
                                tv_vam_nodata.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                alarmState = false;

                                imageView.setImageResource(R.drawable.alarm_state_off);
                                vacVO.setARM_03("N");

                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                tv_vam_nodata.setVisibility(View.GONE);
                                alarmState = true;
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


    public void requestVAM_CONTROL(String GUBUN, VamVO vamVO) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<VAMModel> call = Http.vam(HttpBaseService.TYPE.POST).VAM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                vamVO.VAM_ID,
                vamVO.VAM_01,
                "",
                vamVO.VAM_03,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<VAMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VAMModel> call, Response<VAMModel> response) {

                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VAMModel> response = (Response<VAMModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null)
                                mList = new ArrayList<>();
                            tv_vamCnt.setText("(" + mList.size() + ")");
                            if (mList.size() == 0) {
                                tv_vam_nodata.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                alarmState = false;
                                imageView.setImageResource(R.drawable.alarm_state_off);
                                vacVO.setARM_03("N");
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                tv_vam_nodata.setVisibility(View.GONE);
                                alarmState = true;
                            }

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
                if (!getIntent().hasExtra("scanCode")) {
                    Toast.makeText(mContext, getString(R.string.trp_text5), Toast.LENGTH_SHORT).show();
                }
                ed_item.setText("");
            }

            @Override
            public void onFailure(Call<VAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
            }
        });

    }

    public String stringTodateFormat(String str) {
        String retStr = "";
        //yyyy.MM.dd
        retStr = str.substring(0, 4) + "." + str.substring(4, 6) + "." + str.substring(6, 8);
        return retStr;
    }

    public static class SpinnerList {
        private String VAD_02;
        private String VAD_03;
        private String VAD_04;

        public SpinnerList(String VAD_02, String VAD_03, String VAD_04){
            this.VAD_02 = VAD_02;
            this.VAD_03 = VAD_03;
            this.VAD_04 = VAD_04;

        }
        public String getVAD_02() {
            return VAD_02;
        }

        public void setVAD_02(String VAD_02) {
            this.VAD_02 = VAD_02;
        }

        public String getVAD_03() {
            return VAD_03;
        }

        public void setVAD_03(String VAD_03) {
            this.VAD_03 = VAD_03;
        }

        public String getVAD_04() {
            return VAD_04;
        }

        public void setVAD_04(String VAD_04) {
            this.VAD_04 = VAD_04;
        }
    }


}
