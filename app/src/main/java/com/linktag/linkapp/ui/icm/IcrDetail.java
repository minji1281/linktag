package com.linktag.linkapp.ui.icm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ICRModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.ICR_VO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IcrDetail extends BaseActivity implements DetailIciRecycleAdapter.IciClickListener {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private TextView tvIcon;
    private TextView tvItem;
    private TextView tvDay;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvWave;

    private ImageView imgDay;
    private ImageView imgStartTime;
    private ImageView imgEndTime;

    private LinearLayout layoutDetail;

    private Button btnDetail0;
    private Button btnDetail1;
    private Button btnDetail2;

    private EditText etMemo;

    private View vArea;

    private Button btnSave;

    //======================
    // Variable
    //======================
    private DetailIciRecycleAdapter mAdapter_ICI;
    private ArrayList<String> mList_ICI;
    private ArrayList<String> mList_ICI_detail;

    //======================
    // Initialize
    //======================
//    private CtdVO intentVO;
    private ICR_VO ICR;
    private String GUBUN;

    Calendar ICR_03_C = Calendar.getInstance(); //일자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icr_detail);

        mList_ICI = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ici)));

        ICR = (ICR_VO) getIntent().getSerializableExtra("ICR");
        GUBUN = "UPDATE";

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        clearCalTime(ICR_03_C);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        tvIcon = (TextView) findViewById(R.id.tvIcon);
        tvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iciDialog();
            }
        });
        tvItem = (TextView) findViewById(R.id.tvItem);
        tvDay = (TextView) findViewById(R.id.tvDay);
        tvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayDialog();
            }
        });
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeDialog("S", ICR.ICR_04);
            }
        });
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeDialog("E", ICR.ICR_05);
            }
        });
        tvWave = (TextView) findViewById(R.id.tvWave);

        imgDay = (ImageView) findViewById(R.id.imgDay);
        imgDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dayDialog();
            }
        });
        imgStartTime = (ImageView) findViewById(R.id.imgStartTime);
        imgStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeDialog("S", ICR.ICR_04);
            }
        });
        imgEndTime = (ImageView) findViewById(R.id.imgEndTime);
        imgEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeDialog("E", ICR.ICR_05);
            }
        });

        layoutDetail = (LinearLayout) findViewById(R.id.layoutDetail);

        btnDetail0 = (Button) findViewById(R.id.btnDetail0);
        btnDetail0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btnDetail0.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                    btnDetail1.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                    btnDetail2.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                } else {
                    btnDetail0.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                    btnDetail1.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                    btnDetail2.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                }
                btnDetail0.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                btnDetail1.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
                btnDetail2.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
                ICR.ICR_07 = "0";
            }
        });
        btnDetail1 = (Button) findViewById(R.id.btnDetail1);
        btnDetail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btnDetail1.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                    btnDetail0.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                    btnDetail2.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                } else {
                    btnDetail1.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                    btnDetail0.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                    btnDetail2.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                }
                btnDetail1.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                btnDetail0.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
                btnDetail2.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
                ICR.ICR_07 = "1";
            }
        });
        btnDetail2 = (Button) findViewById(R.id.btnDetail2);
        btnDetail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    btnDetail2.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                    btnDetail0.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                    btnDetail1.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                } else {
                    btnDetail2.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_blue));
                    btnDetail0.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                    btnDetail1.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                }
                btnDetail2.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                btnDetail0.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
                btnDetail1.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
                ICR.ICR_07 = "2";
            }
        });

        etMemo = (EditText) findViewById(R.id.etMemo);

        vArea = (View) findViewById(R.id.vArea);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                requestICR_CONTROL("UPDATE");
            }
        });

        if(GUBUN.equals("UPDATE")){
            if(ICR.ICR_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
//                        deleteDialog();
                        new AlertDialog.Builder(mActivity)
                                .setMessage(R.string.icr_detail_delete)
                                .setPositiveButton(R.string.onPositive, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestICR_CONTROL("DELETE");
                                    }
                                })
                                .setNegativeButton(R.string.onNegative, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                })
                                .show();
                    }
                });
            }
        }
    }

    @Override
    protected void initialize() {
        getDetail();
    }

    private void getDetail() {
        tvIcon.setText(mList_ICI.get(Integer.valueOf(ICR.ICR_06)));
        tvItem.setText(mList_ICI.get(Integer.valueOf(ICR.ICR_06)));

        ICR_03_C.set(Integer.valueOf(ICR.ICR_03.substring(0,4)), Integer.valueOf(ICR.ICR_03.substring(4,6)) - 1, Integer.valueOf(ICR.ICR_03.substring(6)));
        tvDay.setText(sDateFormat(ICR.ICR_03));

        tvStartTime.setText(sTimeFormat(ICR.ICR_04));

        setIciDetail();

        etMemo.setText(ICR.ICR_08);
    }

    private void requestICR_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String ICR_ID = ICR.ICR_ID; //컨테이너
        String ICR_01 = ICR.ICR_01; //Master일련번호(ICM_01)
        String ICR_02 = ICR.ICR_02; //일련번호
        String ICR_03 = ICR.ICR_03; //일자(YYYYMMDD)

        String ICR_04 = ICR.ICR_04; //시작시간(HHMM)
        String ICR_05 = ICR.ICR_05; //종료시간(HHMM)
        String ICR_06 = ICR.ICR_06; //항목(ici)
        String ICR_07 = ICR.ICR_07; //상세
        String ICR_08 = etMemo.getText().toString(); //메모

        String ICR_98 = mUser.Value.OCM_01; //최종수정자

        Call<ICRModel> call = Http.icr(HttpBaseService.TYPE.POST).ICR_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                ICR_ID,
                ICR_01,
                ICR_02,
                ICR_03,

                ICR_04,
                ICR_05,
                ICR_06,
                ICR_07,
                ICR_08,

                ICR_98
        );

        call.enqueue(new Callback<ICRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<ICRModel> call, Response<ICRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<ICRModel> response = (Response<ICRModel>) msg.obj;

                            finish();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ICRModel> call, Throwable t){
                Log.d("ICR_CONTROL", t.getMessage());

//                closeLoadingBar();
            }
        });

    }

    private void dayDialog(){
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                ICR_03_C.set(year, month, date);
                String tmp = String.valueOf(year);

                month++;
                if(month<10){
                    tmp += "0" + String.valueOf(month);
                }
                else{
                    tmp += String.valueOf(month);
                }

                if(date<10){
                    tmp += "0" + String.valueOf(date);
                }
                else{
                    tmp += String.valueOf(date);
                }

                ICR.ICR_03 = tmp;
                tvDay.setText(sDateFormat(ICR.ICR_03));
            }
        }, ICR_03_C.get(Calendar.YEAR), ICR_03_C.get(Calendar.MONTH), ICR_03_C.get(Calendar.DATE));

        dialog.show();
    }

    private void timeDialog(String GUB, String t){ //GUB= S:Start E:End
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_time, null);
        builder.setView(view);

        TimePicker tpTime = (TimePicker) view.findViewById(R.id.tpTime);
        tpTime.setCurrentHour(Integer.valueOf(t.substring(0,2)));
        tpTime.setCurrentMinute(Integer.valueOf(t.substring(2)));

        Button btnPositive = (Button) view.findViewById(R.id.btnPositive);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnPositive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String hh = tpTime.getCurrentHour()<10 ? "0" + String.valueOf(tpTime.getCurrentHour()) : String.valueOf(tpTime.getCurrentHour());
                String mm = tpTime.getCurrentMinute()<10 ? "0" + String.valueOf(tpTime.getCurrentMinute()) : String.valueOf(tpTime.getCurrentMinute());
                if(GUB.equals("S")){
                    ICR.ICR_04 = hh + mm;
                    tvStartTime.setText(sTimeFormat(ICR.ICR_04));
                }
                else{
                    ICR.ICR_05 = hh + mm;
                    tvEndTime.setText(sTimeFormat(ICR.ICR_05));
                }
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onListIciClick(int position) {
        if(!ICR.ICR_06.equals(String.valueOf(position))){
            ICR.ICR_06 = String.valueOf(position);
            ICR.ICR_07 = "";
            tvIcon.setText(mList_ICI.get(Integer.valueOf(ICR.ICR_06)));
            tvItem.setText(mList_ICI.get(Integer.valueOf(ICR.ICR_06)));
            setIciDetail();
        }
    }

    private void iciDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ici, null);
        builder.setView(view);

        BaseHeader nameHeader = view.findViewById(R.id.header);
        nameHeader.btnHeaderRight1.setImageResource(R.drawable.btn_cancel_gray);
        nameHeader.btnHeaderRight1.setVisibility(View.VISIBLE);

        AlertDialog dialog = builder.create();

        RecyclerView recyclerView_DetailIci = (RecyclerView) view.findViewById(R.id.recyclerView_DetailIci);
        LinearLayoutManager linearLayoutManager_ICI = new LinearLayoutManager(mContext);
        recyclerView_DetailIci.setLayoutManager(linearLayoutManager_ICI);
        mAdapter_ICI = new DetailIciRecycleAdapter(mContext, mList_ICI, this, dialog);
        recyclerView_DetailIci.setAdapter(mAdapter_ICI);



        nameHeader.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "." + sDate.substring(4,6) + "." + sDate.substring(6,8);

        return result;
    }

    private String sTimeFormat(String sTime) {
        String hh = sTime.substring(0,2);
        String mm = sTime.substring(2);
        String ap = "AM";
        if(Integer.valueOf(hh) >= 12){
            ap = "PM";
            if(Integer.valueOf(hh) >= 13){
                int tmp = Integer.valueOf(hh) - 12;
                hh = tmp<10 ? "0" + String.valueOf(tmp) : String.valueOf(tmp);
            }
        }

        String result = hh + ":" + mm + " " + ap;

        return result;
    }

    private void setIciDetail(){
        if(Integer.valueOf(ICR.ICR_06) < 3){ //0:수유, 1:기저귀, 2:수면
            layoutDetail.setVisibility(View.VISIBLE);
            if(ICR.ICR_06.equals("0")){ //수유
                mList_ICI_detail = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ici_0)));
                btnDetail0.setText(mList_ICI_detail.get(0));
                vArea.setVisibility(View.GONE);
                btnDetail1.setVisibility(View.VISIBLE);
                btnDetail1.setText(mList_ICI_detail.get(1));
                btnDetail2.setText(mList_ICI_detail.get(2));
            }
            else if(ICR.ICR_06.equals("1")){ //기저귀
                mList_ICI_detail = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ici_1)));
                btnDetail0.setText(mList_ICI_detail.get(0));
                vArea.setVisibility(View.GONE);
                btnDetail1.setVisibility(View.VISIBLE);
                btnDetail1.setText(mList_ICI_detail.get(1));
                btnDetail2.setText(mList_ICI_detail.get(2));
            }
            else if(ICR.ICR_06.equals("2")){ //수면
                mList_ICI_detail = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ici_2)));
                btnDetail0.setText(mList_ICI_detail.get(0));
                btnDetail1.setVisibility(View.GONE);
                vArea.setVisibility(View.VISIBLE);
                btnDetail2.setText(mList_ICI_detail.get(2));
            }
        }
        else{
            layoutDetail.setVisibility(View.GONE);
        }

        if(ICR.ICR_07.equals("")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnDetail0.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                btnDetail1.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                btnDetail2.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
            } else {
                btnDetail0.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                btnDetail1.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
                btnDetail2.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.btn_round_gray_8dp));
            }
            btnDetail0.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
            btnDetail1.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
            btnDetail2.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
        }
        else{
            if(ICR.ICR_07.equals("0")){
                btnDetail0.callOnClick();
            }
            else if(ICR.ICR_07.equals("1")){
                btnDetail1.callOnClick();
            }
            else if(ICR.ICR_07.equals("2")){
                btnDetail2.callOnClick();
            }
        }

        setTime();
    }

    private void setTime(){
        if(ICR.ICR_06.equals("2")){ //수면
            if(ICR.ICR_05.equals("")){
                ICR.ICR_05 = ICR.ICR_04;
            }
            tvWave.setVisibility(View.VISIBLE);
            imgEndTime.setVisibility(View.VISIBLE);
            tvEndTime.setVisibility(View.VISIBLE);

            tvEndTime.setText(sTimeFormat(ICR.ICR_05));
        }
        else{
            tvWave.setVisibility(View.GONE);
            imgEndTime.setVisibility(View.GONE);
            tvEndTime.setVisibility(View.GONE);
            ICR.ICR_05 = "";
        }
    }

}
