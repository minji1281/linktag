package com.linktag.linkapp.ui.bus;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.linktag.linkapp.R;

import com.linktag.linkapp.model.RUTC_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.main.Main;
import com.linktag.linkapp.value_object.RutcVO;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.network.ClsNetworkCheck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BusSelectMain extends BaseActivity {
    //======================
    // Layout
    //======================
    private Spinner spinnerCity;
    private Spinner spinnerStreet;
    private EditText etSearch;
    private ListView listView;
    private ImageView btnSearch;

    //======================
    // Variable
    //======================
    private BusSelectAdapter mAdapter;
    private ArrayList<RutcVO> mList;



    //======================
    // Initialize
    //======================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_main);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerStreet = findViewById(R.id.spinnerStreet);
        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    requestRUTC_SELECT();
                    return true;
                }
                return false;
            }
        });
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> requestRUTC_SELECT());

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener((parent, view, position, id) -> goWorkRecord(position));

    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new BusSelectAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestRUTC_SELECT();
    }

    private void requestRUTC_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //openLoadingBar();

        String GUBUN = "LIST";
        String RUTC_ID = mUser.Value.CTM_01;
        String RUTC_03 = etSearch.getText().toString();
        String RUTC_07 = "";
        String RUTC_08 = "";
        Call<RUTC_Model> call = Http.rutc(HttpBaseService.TYPE.POST).RUTC_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                RUTC_ID,
                RUTC_03,
                RUTC_07,
                RUTC_08
        );


        call.enqueue(new Callback<RUTC_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RUTC_Model> call, Response<RUTC_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){

                        if(msg.what == 100){
                            Response<RUTC_Model> response = (Response<RUTC_Model>) msg.obj;

                            mList = response.body().Data;
                            if(mList == null)
                                mList = new ArrayList<>();

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RUTC_Model> call, Throwable t){
                Log.d("RUTC_SELECT", t.getMessage());
                //closeLoadingBar();
            }
        });
    }

    private void goWorkRecord(int position) {
        Intent intent = new Intent(mContext, Main.class);
//        intent.putExtra(WorkRecordActivity.WORK_STATE, mList.get(position));
//        mContext.startActivity(intent);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate = sdfNow.format(date);

        mUser.Value.RUTC_01 = String.valueOf(mList.get(position).RUTC_01);  //   #008  --노선번호
        mUser.Value.RUTC_03 = String.valueOf(mList.get(position).RUTC_03);  //   #008  --노선번호
        mUser.Value.RUTC_04 = String.valueOf(mList.get(position).RUTC_04);  //   #008  --기점
        mUser.Value.RUTC_05 = String.valueOf(mList.get(position).RUTC_05);  //   #008  --종점
        mUser.Value.RUTC_ST = formatDate;  //   #008  --운행시작시간
        mContext.startActivity(intent);
       // Log.d("***********************",String.valueOf(mList.get(position).RUTC_01));
    }

}
