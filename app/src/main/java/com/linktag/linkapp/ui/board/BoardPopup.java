package com.linktag.linkapp.ui.board;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.BRCModel;
import com.linktag.linkapp.model.NOCModel;
import com.linktag.linkapp.model.TKNModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.BRC_VO;
import com.linktag.linkapp.value_object.NOC_VO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BoardPopup extends BaseActivity {

    public static Context mContext5;

    //======================
    // Layout
    //======================
    private BaseHeader header;
    private Spinner spinnerCity;
    private Spinner spinnerStreet;
    private EditText etSearch;
    private EditText etComment;
    private ListView listView;
    private ImageView btnSearch;
    private ImageButton btnMove;


    //======================
    // Variable
    //======================
    private BoardBRCAdapter BRCAdapter;
    private ArrayList<BRC_VO> BRCList;

    private BoardNOCAdapter NOCAdapter;
    private ArrayList<NOC_VO> NOCList;

    String GUBUN="";
    String getDSH_01="";
    String getDSH_97="";
    String getDSH_04="";
    String getComment = "";
    String alarmTitle = "";



    //======================
    // Initialize
    //======================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 바 없애기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_popup);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getIntent().getStringExtra("DSH_GB").equals("BRD")){ GUBUN = "BRC";}
        else if(getIntent().getStringExtra("DSH_GB").equals("NOT")){ GUBUN = "NOC";}

        if(getIntent().getStringExtra("DSH_01").equals("")){ getDSH_01 = "";}else{getDSH_01 = getIntent().getStringExtra("DSH_01");}
        if(getIntent().getStringExtra("DSH_04").equals("")){ getDSH_04 = "";}else{getDSH_04 = getIntent().getStringExtra("DSH_04");}
        if(getIntent().getStringExtra("DSH_97").equals("")){ getDSH_97 = "";}else{getDSH_97 = getIntent().getStringExtra("DSH_97");}
//        if(GUBUN.equals("BRC")){ alarmTitle = getString(R.string.dash_21); }
//        else if(GUBUN.equals("NOC")){ alarmTitle = getString(R.string.dash_22);}

        if(getIntent().getStringExtra("DSH_GB").equals("BRD")){alarmTitle = getString(R.string.dash_21);}
        else if(getIntent().getStringExtra("DSH_GB").equals("NOT")){ alarmTitle = getString(R.string.dash_22);}

        mContext5 = this;

        etComment = findViewById(R.id.etComment);
        etComment.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override

            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                previousString= s.toString();
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (etComment.getLineCount() >= 5)
                {
                    etComment.setText(previousString);
                    etComment.setSelection(etComment.length());
                }
            }
        });

        initLayout();
        initialize();



    }

    @Override
    protected void initLayout() {
      //  header = findViewById(R.id.header);
      //  header.btnHeaderLeft.setOnClickListener(v -> finish());

   //     if(GUBUN.equals("BRC")){ header.tvHeaderTitle.setText(R.string.dash_01);}
   //     else if(GUBUN.equals("NOC")){ header.tvHeaderTitle.setText(R.string.dash_02);}

        spinnerCity = findViewById(R.id.spinnerCity);
        spinnerStreet = findViewById(R.id.spinnerStreet);
        etSearch = findViewById(R.id.etSearch);


        etSearch.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){

                    if(GUBUN.equals("BRC")){requestBRC_SELECT();}
                    else if(GUBUN.equals("NOC")){ requestNOC_SELECT();}

//                    requestBRC_SELECT();
                    return true;
                }
                return false;
            }
        });
        btnSearch = findViewById(R.id.btnSearch);

        if(GUBUN.equals("BRC")){  btnSearch.setOnClickListener(v -> requestBRC_SELECT());
            listView = findViewById(R.id.listView);
         //   listView.setOnItemClickListener((parent, view, position, id) -> goBRCRecord(position));
        }
        else if(GUBUN.equals("NOC")){ btnSearch.setOnClickListener(v -> requestNOC_SELECT());
            listView = findViewById(R.id.listView);
          //  listView.setOnItemClickListener((parent, view, position, id) -> goNOCRecord(position));
        }






    }

    @Override
    protected void initialize() {
        if(GUBUN.equals("BRC")){  BRCList = new ArrayList<>();
            BRCAdapter = new BoardBRCAdapter(mContext, BRCList);
            listView.setAdapter(BRCAdapter);}
        else if(GUBUN.equals("NOC")){ NOCList = new ArrayList<>();
            NOCAdapter = new BoardNOCAdapter(mContext, NOCList);
            listView.setAdapter(NOCAdapter);}


//        BRCList = new ArrayList<>();
//        BRCAdapter = new BoardBRCAdapter(mContext, BRCList);
//        listView.setAdapter(BRCAdapter);
    }

    public void AllRefresh(View v){  // 신규등록
        if(GUBUN.equals("BRC")){ requestBRC_SELECT();}
        else if(GUBUN.equals("NOC")){ requestNOC_SELECT();}
        listView.smoothScrollToPosition( 0 );
    }

    //확인 버튼 클릭
    public void New(View v){
            Intent intent = new Intent(mContext, BoardDetail.class);
            intent.putExtra("DSH_01", "");
            intent.putExtra("DSH_04", "");
            intent.putExtra("DSH_05", "");
            intent.putExtra("DSH_GB", GUBUN);

            mContext.startActivity(intent);
            // Log.d("***********************",String.valueOf(BRCList.get(position).RUTC_01));

//        Toast.makeText(mActivity, "일단 테스트 합니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(GUBUN.equals("BRC")){ requestBRC_SELECT();}
        else if(GUBUN.equals("NOC")){ requestNOC_SELECT();}

    }

    //확인 버튼 클릭
    public void AllClose(View v){

        finish();
    }

    //취소 버튼 클릭
    public void AllClear(View v){
        etComment.setText("");
    }

    public void AllNew(View v){  // 신규등록

        Onsubmit(GUBUN, "");
    }

    public void Onsubmit(String Gub, String CMT_01){
        // Toast.makeText(mActivity, "클릭", Toast.LENGTH_SHORT).show();
        String status = "";
        if(CMT_01.equals("")){status = "INSERT";}else{status = "DELETE";}

        if(Gub.equals("BRC")){ requestBRC_CONTROL(status, CMT_01);}
        else if(Gub.equals("NOC")){ requestNOC_CONTROL(status, CMT_01);}

    }


    private void requestBRC_CONTROL(String GUB, String CMT_02){
        if(!validationCheck(GUB))
            return;

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(this, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        getComment = etComment.getText().toString();

        String GUBUN = GUB;
        String BRC_ID = mUser.Value.CTM_01;
        String BRC_01 = getDSH_01;
        String BRC_02 = CMT_02;
        String BRC_03 = etComment.getText().toString();
        String BRC_98 = mUser.Value.OCM_01;

        Call<BRCModel> call = Http.commute(HttpBaseService.TYPE.POST).BRC_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                BRC_ID,
                BRC_01,
                BRC_02,
                BRC_03,
                BRC_98
        );

        call.enqueue(new Callback<BRCModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<BRCModel> call, Response<BRCModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<BRCModel> response = (Response<BRCModel>) msg.obj;

                            callBack_BRC(GUB, response.body().Data.get(0));
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<BRCModel> call, Throwable t){
                Log.d("OCM_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private void callBack_BRC(String GUB, BRC_VO data){
         requestBRC_SELECT();
        if(GUB.equals("INSERT")){etComment.setText(""); ((BoardDetail)BoardDetail.mBoard1).CalcCmt("+"); requestTKN_SELECT(getDSH_97);}
        else {((BoardDetail)BoardDetail.mBoard1).CalcCmt("-"); }
        listView.smoothScrollToPosition( 0 );

    }

    private void requestNOC_CONTROL(String GUB, String CMT_02){
        if(!validationCheck(GUB))
            return;

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(this, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        getComment = etComment.getText().toString();

        String GUBUN = GUB;
        String NOC_ID = mUser.Value.CTM_01;
        String NOC_01 = getDSH_01;
        String NOC_02 = CMT_02;
        String NOC_03 = etComment.getText().toString();
        String NOC_98 = mUser.Value.OCM_01;

        Call<NOCModel> call = Http.commute(HttpBaseService.TYPE.POST).NOC_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                NOC_ID,
                NOC_01,
                NOC_02,
                NOC_03,
                NOC_98
        );

        call.enqueue(new Callback<NOCModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<NOCModel> call, Response<NOCModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<NOCModel> response = (Response<NOCModel>) msg.obj;

                            callBack_NOC(GUB, response.body().Data.get(0));
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<NOCModel> call, Throwable t){
                Log.d("OCM_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    private void callBack_NOC(String GUB, NOC_VO data){
        requestNOC_SELECT();
        if(GUB.equals("INSERT")){etComment.setText(""); ((BoardDetail)BoardDetail.mBoard1).CalcCmt("+"); requestTKN_SELECT(getDSH_97);}
        else {((BoardDetail)BoardDetail.mBoard1).CalcCmt("-"); }
        listView.smoothScrollToPosition( 0 );

    }



    private boolean validationCheck(String GUB){
        if(GUB.equals("INSERT"))
        {
            if(etComment.getText().toString().length() == 0){
                etComment.requestFocus();
                Toast.makeText(mActivity, R.string.dash_11, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (GUB.equals("DELETE")) {

        }

        return true;
    }

    private void requestBRC_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //openLoadingBar();

        String GUBUN = "DETAIL";
        String BRC_ID = mUser.Value.CTM_01;
        String BRC_01 = getDSH_01;
        String BRC_02 = "";
        String BRC_03 = "";
        String BRC_98 = "";
        Call<BRCModel> call = Http.commute(HttpBaseService.TYPE.POST).BRC_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                BRC_ID,
                BRC_01,
                BRC_02,
                BRC_03,
                BRC_98
        );


        call.enqueue(new Callback<BRCModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<BRCModel> call, Response<BRCModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){

                        if(msg.what == 100){
                            Response<BRCModel> response = (Response<BRCModel>) msg.obj;

                            BRCList = response.body().Data;
                            if(BRCList == null)
                                BRCList = new ArrayList<>();

                            BRCAdapter.updateData(BRCList);
                            BRCAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<BRCModel> call, Throwable t){
                Log.d("BRC_SELECT", t.getMessage());
                //closeLoadingBar();
            }
        });
    }


    private void requestNOC_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //openLoadingBar();

        String GUBUN = "DETAIL";
        String NOC_ID = mUser.Value.CTM_01;
        String NOC_01 = getDSH_01;
        String NOC_02 = "";
        String NOC_03 = "";
        String NOC_98 = "";
        Call<NOCModel> call = Http.commute(HttpBaseService.TYPE.POST).NOC_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                NOC_ID,
                NOC_01,
                NOC_02,
                NOC_03,
                NOC_98
        );


        call.enqueue(new Callback<NOCModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<NOCModel> call, Response<NOCModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){

                        if(msg.what == 100){
                            Response<NOCModel> response = (Response<NOCModel>) msg.obj;

                            NOCList = response.body().Data;
                            if(NOCList == null)
                                NOCList = new ArrayList<>();

                            NOCAdapter.updateData(NOCList);
                            NOCAdapter.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<NOCModel> call, Throwable t){
                Log.d("NOC_SELECT", t.getMessage());
                //closeLoadingBar();
            }
        });
    }

    private void goBRCRecord(int position) {
        Intent intent = new Intent(mContext, BoardDetail.class);
        intent.putExtra(BoardDetail.WORK_STATE, BRCList.get(position));
        intent.putExtra("DSH_01", BRCList.get(position).BRC_01);
        intent.putExtra("DSH_04", BRCList.get(position).BRC_04);
        intent.putExtra("DSH_05", BRCList.get(position).BRC_05);
    //    intent.putExtra("DSH_09", BRCList.get(position).BRC_09);
        intent.putExtra("DSH_GB", GUBUN);

        mContext.startActivity(intent);
        // Log.d("***********************",String.valueOf(BRCList.get(position).RUTC_01));
    }


    private void goNOCRecord(int position) {
        Intent intent = new Intent(mContext, BoardDetail.class);
        intent.putExtra(BoardDetail.WORK_STATE, NOCList.get(position));
        intent.putExtra("DSH_01", NOCList.get(position).NOC_01);
        intent.putExtra("DSH_04", NOCList.get(position).NOC_04);
        intent.putExtra("DSH_05", NOCList.get(position).NOC_05);
      //  intent.putExtra("DSH_09", NOCList.get(position).NOC_09);
        intent.putExtra("DSH_GB", GUBUN);
        mContext.startActivity(intent);
       // Log.d("***********************",String.valueOf(BRCList.get(position).RUTC_01));
    }

    private void requestTKN_SELECT(String user){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //openLoadingBar();

        String GUBUN = "SEND";
        String TKN_ID = mUser.Value.CTM_01;
        String TKN_01 = "";
        String TKN_02 = user;
        String TKN_03 = "";
        String TKN_04 = "";
        Call<TKNModel> call = Http.push(HttpBaseService.TYPE.POST).TKN_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                TKN_ID,
                TKN_01,
                TKN_02,
                TKN_03,
                TKN_04
        );


        call.enqueue(new Callback<TKNModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<TKNModel> call, Response<TKNModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            String alarmText = "";
                            alarmText = getString(R.string.dash_20);

                            String result = alarmTitle.concat(getDSH_04);
                            String tagname = etComment.getText().toString();

                            Response<TKNModel> response = (Response<TKNModel>) msg.obj;
                            openLoadingBar();

                            if (response.body().Total > 0) {

                                int i = 0;

                                while(i < response.body().Total)
                                {
                                    requestTKN_CALL(response.body().Data.get(i).TKN_04, result, getComment);
                                    i++;
                                }
                                closeLoadingBar();

                            }
                            else{
                                return;
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<TKNModel> call, Throwable t){
                Log.d("TKN_SELECT", t.getMessage());
                //closeLoadingBar();
            }
        });
    }


    private void requestTKN_CALL(String device, String title, String content ){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //openLoadingBar();

        Call<TKNModel> call = Http.push(HttpBaseService.TYPE.POST).NotifyAsync(
                BaseConst.URL_HOST,
                device,
                title,
                content
        );

        call.enqueue(new Callback<TKNModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<TKNModel> call, Response<TKNModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;
            }

            @Override
            public void onFailure(Call<TKNModel> call, Throwable t){
                Log.d("TKN_SELECT", t.getMessage());
                //closeLoadingBar();
            }
        });
    }

}
