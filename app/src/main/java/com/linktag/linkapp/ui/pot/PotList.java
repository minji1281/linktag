package com.linktag.linkapp.ui.pot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PotVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PotList extends BaseActivity {
    //======================
    // Layout
    //======================
    private BaseHeader header;
    //private Spinner spinnerCity;
    //private Spinner spinnerStreet;
    //private EditText etSearch;
    private ListView listView;
    //private ImageView btnSearch;
    private TextView emptyText;

    //======================
    // Variable
    //======================
    private PotAdapter mAdapter;
    private ArrayList<PotVO> mList;


    //======================
    // Initialize
    //======================
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pot_list);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setVisibility((View.GONE));

        //spinnerCity = findViewById(R.id.spinnerCity);
        //spinnerStreet = findViewById(R.id.spinnerStreet);
//        etSearch = findViewById(R.id.etSearch);
//        etSearch.setOnKeyListener(new View.OnKeyListener(){
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
//                    requestPOT_SELECT();
//                    return true;
//                }
//                return false;
//            }
//        });
//        btnSearch = findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(v -> requestPOT_SELECT());

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, PotDetail.class);
                intent.putExtra("POT_81", mList.get(position).POT_81);
                intent.putExtra("POT_02", mList.get(position).POT_02);
                intent.putExtra("POT_03_T", mList.get(position).POT_03_T);
                intent.putExtra("POT_04", mList.get(position).POT_04);
                intent.putExtra("POT_05", mList.get(position).POT_05);
                intent.putExtra("ARM_03", mList.get(position).ARM_03);
                intent.putExtra("POT_96", mList.get(position).POT_96);
                intent.putExtra("POT_06", mList.get(position).POT_06);
                intent.putExtra("POT_01", mList.get(position).POT_01);
                intent.putExtra("POT_97", mList.get(position).POT_97);
                mContext.startActivity(intent);
            }
        });
        emptyText = findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
    }

    @Override
    protected void initialize() {
        mList = new ArrayList<>();
        mAdapter = new PotAdapter(mContext, mList); //, this
        listView.setAdapter(mAdapter);

        //requestPOT_SELECT();
    }

    @Override
    protected void onResume(){
        super.onResume();

        requestPOT_SELECT();
    }

    private void requestPOT_SELECT(){
        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = "LIST";
        String POT_ID = "1"; //컨테이너
        String POT_01 = " ";
        String OCM_01 = "M191100001"; //mUser.Value.OCM_01

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                OCM_01
        );

        call.enqueue(new Callback<POT_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<POT_Model> call, Response<POT_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

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
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("POT_SELECT", t.getMessage());
                closeLoadingBar();
            }
        });
    }

//    @Override
//    public void onListAlarmClick(String btnName, int position) {
//        String btn1 = "APPROVE";
//        String btn2 = "RETURN";
//        LED_VO data = mList.get(position);
//        String nm = data.LED_05.equals("1") ? "연차" : "연장";
//
//        if (btnName.equals(btn1)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//            builder.setMessage("해당 " + nm + "을/를 승인하시겠습니까?");
//            builder.setPositiveButton("예",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            requestBVA_CONTROL("UPDATE19", data.LED_02, "Y");
//                        }
//                    });
//            builder.setNegativeButton("취소",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//            builder.show();
//
//        } else if (btnName.equals(btn2)){
//            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//            builder.setMessage("해당 " + nm + "을/를 반려하시겠습니까?");
//            builder.setPositiveButton("예",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            requestBVA_CONTROL("UPDATE19", data.LED_02, "R");
//                        }
//                    });
//            builder.setNegativeButton("취소",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//            builder.show();
//        }
//
//    }

}
