package com.linktag.linkapp.ui.car;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CADModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CAD_VO;
import com.linktag.linkapp.value_object.CtdVO;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadDetail extends BaseActivity {
    //======================
    // Final
    //======================

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private TextView tvDay;
    private TextView tvPreDay;
    private TextView tvPreKm;
    private TextView tvPreDayGap;
    private TextView tvName;

    private EditText etMemo;
    private EditText etMoney;
    private EditText etKm;

    private Spinner spGub1;
    private Spinner spGub2;

    private Button btnSave;

    private ImageView imgDayIcon;

    //======================
    // Variable
    //======================
    private ArrayList<CAD_VO> mList;

    //======================
    // Initialize
    //======================
    private CtdVO intentVO;
    private CAD_VO CAD;
    private String GUBUN;

    Calendar CAD_03_C = Calendar.getInstance(); //정비일자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_detail);

        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        if(getIntent().hasExtra("CAD")){
            CAD = (CAD_VO) getIntent().getSerializableExtra("CAD");
            GUBUN = "UPDATE";
        }
        else{
            CAD = new CAD_VO();
            CAD.CAD_01 = getIntent().getStringExtra("CAD_01");
            GUBUN = "INSERT";
        }

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        clearCalTime(CAD_03_C);

        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameDialog();
            }
        });
        if(GUBUN.equals("UPDATE")){
            requestCAD_SELECT();
        }

        spGub1 = (Spinner) findViewById(R.id.spGub1);
        ArrayList arrayList1 = new ArrayList();
        arrayList1.add(mContext.getString(R.string.cad_detail_replace));
        arrayList1.add(mContext.getString(R.string.cad_detail_check));
        ArrayAdapter arrayAdapter1 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_detail_item, arrayList1);
        spGub1.setAdapter(arrayAdapter1);
        spGub1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CAD.CAD_05 = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spGub2 = (Spinner) findViewById(R.id.spGub2);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(mContext.getString(R.string.cad_detail_self));
        arrayList2.add(mContext.getString(R.string.cad_detail_shop));
        ArrayAdapter arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_detail_item, arrayList2);
        spGub2.setAdapter(arrayAdapter2);
        spGub2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CAD.CAD_06 = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        imgDayIcon = (ImageView) findViewById(R.id.imgDayIcon);
        imgDayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayDialog();
            }
        });

        tvDay = (TextView) findViewById(R.id.tvDay);
        tvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayDialog();
            }
        });
        tvPreDay = (TextView) findViewById(R.id.tvPreDay);
        tvPreKm = (TextView) findViewById(R.id.tvPreKm);
        tvPreDayGap = (TextView) findViewById(R.id.tvPreDayGap);

        etMemo = (EditText) findViewById(R.id.etMemo);
        etMoney = (EditText) findViewById(R.id.etMoney);
        etKm = (EditText) findViewById(R.id.etKm);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Toast.makeText(mActivity, String.valueOf(CAD.CAD_07), Toast.LENGTH_SHORT).show();
                if(validationCheck()){
                    requestCAD_CONTROL(GUBUN);
                }
            }
        });

        if(GUBUN.equals("UPDATE")){
            if(CAD.CAD_97.equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
                header.btnHeaderRight1.setVisibility((View.VISIBLE));
                header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
                header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        deleteDialog();
                    }
                });
            }
        }
        else{ //INSERT
            getNewData();
        }

    }

    @Override
    protected void initialize() {

        if(GUBUN.equals("UPDATE")){
            getDetail();
        }

        etMoney.setText(String.valueOf(Math.round(CAD.CAD_07)));
        etKm.setText(String.valueOf(Math.round(CAD.CAD_08)));
        tvDay.setText(sDateFormat(CAD.CAD_03));
        tvName.setText(CAD.CAD_04);
    }

    private void getDetail() {
        CAD_03_C.set(Integer.parseInt(CAD.CAD_03.substring(0,4)), Integer.parseInt(CAD.CAD_03.substring(4,6))-1, Integer.parseInt(CAD.CAD_03.substring(6)));

        etMemo.setText(CAD.CAD_09);

        spGub1.setSelection(Integer.parseInt(CAD.CAD_05) - 1);
        spGub2.setSelection(Integer.parseInt(CAD.CAD_06) - 1);
    }

    private void getNewData(){
        //초기값
        CAD.CAD_04 = mContext.getString(R.string.cad_detail_select);
        CAD.CAD_02 = "";

        CAD.CAD_07 = 0;
        CAD.CAD_08 = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        CAD.CAD_03 = sdf.format(CAD_03_C.getTime());
    }

    private void requestCAD_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String GUBUN = "PRE";
        String CAD_ID = intentVO.CTN_02; //컨테이너
        String CAD_01 = CAD.CAD_01; //차량코드
        String CAD_02 = CAD.CAD_02; //일련번호
        String CAD_03 = CAD.CAD_03; //정비일자
        String CAD_04 = CAD.CAD_04; //내역

        Call<CADModel> call = Http.cad(HttpBaseService.TYPE.POST).CAD_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                CAD_ID,
                CAD_01,
                CAD_02,
                CAD_03,

                CAD_04
        );

        call.enqueue(new Callback<CADModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CADModel> call, Response<CADModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<CADModel> response = (Response<CADModel>) msg.obj;

                            mList = response.body().Data;
                            if (mList == null) mList = new ArrayList<>();

                            if(mList.size() == 0){
                                tvPreDay.setText("");
                                tvPreKm.setText("");
                                tvPreDayGap.setText("");
                            }
                            else{
                                tvPreDay.setText(sDateFormat(mList.get(0).CAD_03));
                                tvPreKm.setText(NumberFormat.getInstance().format(mList.get(0).CAD_08) + "km");

                                LocalDate oldDate = LocalDate.of(Integer.parseInt(mList.get(0).CAD_03.substring(0,4)), Integer.parseInt(mList.get(0).CAD_03.substring(4,6))-1, Integer.parseInt(mList.get(0).CAD_03.substring(6)));
                                LocalDate now = LocalDate.of(Integer.parseInt(CAD.CAD_03.substring(0,4)), Integer.parseInt(CAD.CAD_03.substring(4,6))-1, Integer.parseInt(CAD.CAD_03.substring(6)));
                                Period diff = Period.between(oldDate, now);
                                String gapText = "";
                                if(diff.getMonths() > 0){
                                    gapText += diff.getMonths() + mContext.getString(R.string.dialog_cycle_month) + " ";
                                }
                                if(diff.getDays() > 0){
                                    gapText += diff.getDays() + mContext.getString(R.string.dialog_cycle_day) + " " + mContext.getString(R.string.cad_detail_later);
                                }

                                tvPreDayGap.setText(gapText);
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CADModel> call, Throwable t){
                Log.d("CAD_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void requestCAD_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

//        openLoadingBar();

        String GUBUN = GUB;
        String CAD_ID = intentVO.CTN_02; //컨테이너
        String CAD_01 = CAD.CAD_01; //차량번호
        String CAD_02 = ""; //일련번호
        if(!GUBUN.equals("INSERT")){
            CAD_02 = CAD.CAD_02;
        }
        String CAD_03 = tvDay.getText().toString().replace(".", ""); //일자
        String CAD_04 = CAD.CAD_04; //내역
        String CAD_05 = CAD.CAD_05; //교체점검구분
        String CAD_06 = CAD.CAD_06; //셀프카센터구분
        double CAD_07 = 0; //비용
        if(!etMoney.getText().toString().equals("")){
            CAD_07 = Double.parseDouble(etMoney.getText().toString().replace(",", ""));
        }
        int CAD_08 = 0; //주행거리
        if(!etKm.getText().toString().equals("")){
            CAD_08 = Integer.parseInt(etKm.getText().toString().replace(",", ""));
        }
        String CAD_09 = etMemo.getText().toString(); //메모
        String CAD_98 = mUser.Value.OCM_01; //최종수정자

        Call<CADModel> call = Http.cad(HttpBaseService.TYPE.POST).CAD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CAD_ID,
                CAD_01,
                CAD_02,
                CAD_03,

                CAD_04,
                CAD_05,
                CAD_06,
                CAD_07,
                CAD_08,

                CAD_09,
                CAD_98
        );

        call.enqueue(new Callback<CADModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CADModel> call, Response<CADModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<CADModel> response = (Response<CADModel>) msg.obj;

//                            CadList.CAR.CAR_01 = CAD_01;
                            finish();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CADModel> call, Throwable t){
                Log.d("CAD_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "." + sDate.substring(4,6) + "." + sDate.substring(6,8);

        return result;
    }

    public boolean validationCheck(){
        boolean check = true;

        if(CAD.CAD_04.equals(mContext.getString(R.string.cad_detail_select))){
            check = false;
            Toast.makeText(mActivity, R.string.cad_validation_check1, Toast.LENGTH_SHORT).show();
        }

        return check;
    }

    private void dayDialog(){
        Locale locale = getResources().getConfiguration().locale;
        Locale.setDefault(locale);
        DatePickerDialog dialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                CAD_03_C.set(year, month, date);
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

                CAD.CAD_03 = tmp;
                tvDay.setText(sDateFormat(CAD.CAD_03));
            }
        }, CAD_03_C.get(Calendar.YEAR), CAD_03_C.get(Calendar.MONTH), CAD_03_C.get(Calendar.DATE));

        dialog.show();
    }

    private void nameDialog(){
        Dialog dialog = new Dialog(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_cad, null);

        LinearLayout layout1 = view.findViewById(R.id.layout1);
        LinearLayout layout2 = view.findViewById(R.id.layout2);

        TextView tvGub1 = view.findViewById(R.id.tvGub1);
        TextView tvGub2 = view.findViewById(R.id.tvGub2);
        tvGub1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                layout2.setVisibility(View.GONE);
                layout1.setVisibility(View.VISIBLE);

                tvGub1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tvGub2.setBackgroundColor(Color.parseColor("#F2F5F7"));
            }
        });
        tvGub2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);

                tvGub1.setBackgroundColor(Color.parseColor("#F2F5F7"));
                tvGub2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });

        ListView listView = view.findViewById(R.id.lvCadName);
        SearchView searchView = view.findViewById(R.id.svCadName);
        BaseHeader nameHeader = view.findViewById(R.id.header);
        nameHeader.btnHeaderRight1.setImageResource(R.drawable.btn_cancel_gray);
        nameHeader.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });

        EditText etCadName = (EditText) view.findViewById(R.id.etCadName);

        Button btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(etCadName.getText().toString().equals("")){
                    Toast.makeText(mActivity, R.string.cad_validation_check1, Toast.LENGTH_SHORT).show();
                }
                else{
                    CAD.CAD_04 = etCadName.getText().toString();
                    tvName.setText(CAD.CAD_04);
                    requestCAD_SELECT();

                    dialog.dismiss();
                }
            }
        });
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });

        String[] cadArray = getResources().getStringArray(R.array.cad);
        List<String> cadArrayList = Arrays.asList(cadArray);
        CadNameAdapter cadNameAdapter = new CadNameAdapter(mContext, cadArrayList);
        listView.setAdapter(cadNameAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CAD.CAD_04 = cadNameAdapter.getItem(position).toString();
                tvName.setText(CAD.CAD_04);
                requestCAD_SELECT();

                dialog.dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cadNameAdapter.getFilter().filter(newText);
                return false;
            }
        });
        dialog.setContentView(view);
        dialog.show();
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
                if(etDeleteName.getText().toString().equals(CAD.CAD_04)){
                    dialog.dismiss();
                    requestCAD_CONTROL("DELETE");
                }
                else{
                    Toast.makeText(mActivity, R.string.dialog_delete_check_text, Toast.LENGTH_SHORT).show();
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

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

}
