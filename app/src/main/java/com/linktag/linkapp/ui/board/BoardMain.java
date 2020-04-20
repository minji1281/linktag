package com.linktag.linkapp.ui.board;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.linktag.linkapp.R;

import com.linktag.linkapp.model.BRDModel;
import com.linktag.linkapp.model.NOTModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.main.Main;
import com.linktag.linkapp.value_object.BRD_VO;
import com.linktag.linkapp.value_object.NOT_VO;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BoardMain extends BaseActivity {

    //======================
    // Layout
    //======================
    private BaseHeader header;
    private Spinner spinnerCity;
    private Spinner spinnerStreet;
    private EditText etSearch;
    private ListView listView;
    private ImageView btnSearch;
    private ImageButton btnMove;

    //======================
    // Variable
    //======================
    private BoardBRDAdapter BRDAdapter;
    private ArrayList<BRD_VO> BRDList;

    private BoardNOTAdapter NOTAdapter;
    private ArrayList<NOT_VO> NOTList;

    String GUBUN="";



    //======================
    // Initialize
    //======================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_main);

        if(getIntent().getStringExtra("DSH_GB").equals("BRD")){ GUBUN = "BRD";}
        else if(getIntent().getStringExtra("DSH_GB").equals("NOT")){ GUBUN = "NOT";}

        initLayout();
        initialize();

    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());
        header.layoutHeader.setBackgroundColor(getResources().getColor(R.color.header_background));
        header.headerSpacer.setVisibility(View.INVISIBLE);
        header.tvHeaderTitle.setTextColor(Color.WHITE);
        header.btnHeaderLeft.setSelected(true);

        if(GUBUN.equals("BRD")){ header.tvHeaderTitle.setText(R.string.dash_02);}
        else if(GUBUN.equals("NOT")){ header.tvHeaderTitle.setText(R.string.dash_01);}

        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerStreet = findViewById(R.id.spinnerStreet);
        etSearch = findViewById(R.id.etSearch);
        etSearch.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){

                    if(GUBUN.equals("BRD")){requestBRD_SELECT();}
                    else if(GUBUN.equals("NOT")){ requestNOT_SELECT();}

//                    requestBRD_SELECT();
                    return true;
                }
                return false;
            }
        });
        btnSearch = findViewById(R.id.btnSearch);

        if(GUBUN.equals("BRD")){  btnSearch.setOnClickListener(v -> requestBRD_SELECT());
            listView = findViewById(R.id.listView);
            listView.setOnItemClickListener((parent, view, position, id) -> goBRDRecord(position));
        }
        else if(GUBUN.equals("NOT")){ btnSearch.setOnClickListener(v -> requestNOT_SELECT());
            listView = findViewById(R.id.listView);
            listView.setOnItemClickListener((parent, view, position, id) -> goNOTRecord(position));
        }






    }

    @Override
    protected void initialize() {
        if(GUBUN.equals("BRD")){  BRDList = new ArrayList<>();
            BRDAdapter = new BoardBRDAdapter(mContext, BRDList);
            listView.setAdapter(BRDAdapter);}
        else if(GUBUN.equals("NOT")){ NOTList = new ArrayList<>();
            NOTAdapter = new BoardNOTAdapter(mContext, NOTList);
            listView.setAdapter(NOTAdapter);}


//        BRDList = new ArrayList<>();
//        BRDAdapter = new BoardBRDAdapter(mContext, BRDList);
//        listView.setAdapter(BRDAdapter);
    }

    //확인 버튼 클릭
    public void New(View v){
            Intent intent = new Intent(mContext, BoardDetail.class);
            intent.putExtra("DSH_01", "");
            intent.putExtra("DSH_04", "");
            intent.putExtra("DSH_05", "");
            intent.putExtra("DSH_09", "0");
            intent.putExtra("DSH_97","");
            intent.putExtra("DSH_GB", GUBUN);

            mContext.startActivity(intent);
            // Log.d("***********************",String.valueOf(BRDList.get(position).RUTC_01));

//        Toast.makeText(mActivity, "일단 테스트 합니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(GUBUN.equals("BRD")){ requestBRD_SELECT();}
        else if(GUBUN.equals("NOT")){ requestNOT_SELECT();}

    }

    private void requestBRD_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //openLoadingBar();

        String GUBUN = "LIST";
        String BRD_ID = mUser.Value.CTM_01;
        String BRD_01 = "";
        String BRD_02 = "";
        String BRD_03 = "";
        String BRD_04 = etSearch.getText().toString();
        String BRD_06 = "";
        Call<BRDModel> call = Http.commute(HttpBaseService.TYPE.POST).BRD_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                BRD_ID,
                BRD_01,
                BRD_02,
                BRD_03,
                BRD_04,
                BRD_06
        );


        call.enqueue(new Callback<BRDModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<BRDModel> call, Response<BRDModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){

                        if(msg.what == 100){
                            Response<BRDModel> response = (Response<BRDModel>) msg.obj;

                            BRDList = response.body().Data;
                            if(BRDList == null)
                                BRDList = new ArrayList<>();

                            BRDAdapter.updateData(BRDList);
                            BRDAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<BRDModel> call, Throwable t){
                Log.d("BRD_SELECT", t.getMessage());
                //closeLoadingBar();
            }
        });
    }


    private void requestNOT_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //openLoadingBar();

        String GUBUN = "LIST";
        String NOT_ID = mUser.Value.CTM_01;
        String NOT_01 = "";
        String NOT_02 = "";
        String NOT_03 = "";
        String NOT_04 = etSearch.getText().toString();
        String NOT_06 = "";
        Call<NOTModel> call = Http.commute(HttpBaseService.TYPE.POST).NOT_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                NOT_ID,
                NOT_01,
                NOT_02,
                NOT_03,
                NOT_04,
                NOT_06
        );


        call.enqueue(new Callback<NOTModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<NOTModel> call, Response<NOTModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){

                        if(msg.what == 100){
                            Response<NOTModel> response = (Response<NOTModel>) msg.obj;

                            NOTList = response.body().Data;
                            if(NOTList == null)
                                NOTList = new ArrayList<>();

                            NOTAdapter.updateData(NOTList);
                            NOTAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<NOTModel> call, Throwable t){
                Log.d("NOT_SELECT", t.getMessage());
                //closeLoadingBar();
            }
        });
    }

    private void goBRDRecord(int position) {
        Intent intent = new Intent(mContext, BoardDetail.class);
        intent.putExtra(BoardDetail.WORK_STATE, BRDList.get(position));
        intent.putExtra("DSH_01", BRDList.get(position).BRD_01);
        intent.putExtra("DSH_04", BRDList.get(position).BRD_04);
        intent.putExtra("DSH_05", BRDList.get(position).BRD_05);
        intent.putExtra("DSH_09", BRDList.get(position).BRD_09);
        intent.putExtra("DSH_97", BRDList.get(position).BRD_97);
        intent.putExtra("DSH_GB", GUBUN);

        mContext.startActivity(intent);
        // Log.d("***********************",String.valueOf(BRDList.get(position).RUTC_01));
    }


    private void goNOTRecord(int position) {
        Intent intent = new Intent(mContext, BoardDetail.class);
        intent.putExtra(BoardDetail.WORK_STATE, NOTList.get(position));
        intent.putExtra("DSH_01", NOTList.get(position).NOT_01);
        intent.putExtra("DSH_04", NOTList.get(position).NOT_04);
        intent.putExtra("DSH_05", NOTList.get(position).NOT_05);
        intent.putExtra("DSH_02", NOTList.get(position).NOT_02);
        intent.putExtra("DSH_03", NOTList.get(position).NOT_03);
        intent.putExtra("DSH_GB", GUBUN);
        mContext.startActivity(intent);
       // Log.d("***********************",String.valueOf(BRDList.get(position).RUTC_01));
    }




}
