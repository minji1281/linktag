package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.util.ClsBitmap;
import com.linktag.base.util.ClsDateTime;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ARM_Model;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.DSHModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.base.util.ExpandableHeightGridView;
import com.linktag.linkapp.ui.beacon.beaconMain;
import com.linktag.linkapp.ui.arclayout.arclayoutMain;
import com.linktag.linkapp.ui.board.BoardDetail;
import com.linktag.linkapp.ui.board.BoardMain;
import com.linktag.linkapp.ui.calendar.AlarmListAdapter;
import com.linktag.linkapp.ui.menu.ChangeActivityCls;
import com.linktag.linkapp.ui.menu.ChooseOne;
import com.linktag.linkapp.ui.sqllite.SqlMain;
import com.linktag.linkapp.value_object.ARM_VO;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment {
    private int BMK_STATE_NORMAL = 0;
    private int BMK_STATE_DELETE = 1;

    //===================================
    // Layout//===================================
    private View view;
    private ListView listView;
    private TextView tvNotice;
    private SwipeRefreshLayout swipeRefresh;

    private ImageView imgProfile;
    private TextView tvHomeName;
    private TextView tvHomeEmail;
    private TextView tvHomeDate;

    private ExpandableHeightGridView gridBMK;
    private ExpandableHeightGridView gridARM;
    private ImageView btnBmkAdd;
    private ImageView btnBmkDelete;
    private ImageView btnBmkCancel;

    private TextView BmkSpacer;
//    private TextView btnNotNew;
    private ImageView btnNotList;
    private TextView btnSql;
    private TextView btnBeacon;
    private TextView btnArc;

    private BookmarkAdapter mAdapter;
    private ArrayList<CtdVO> mList;

    private AlarmListAdapter mAdapter2;
    private ArrayList<ARM_VO> mList2;

    //===================================
    // Variable
    //===================================
    private int BMK_STATE;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        initLayout();

        initialize();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        BMK_STATE = BMK_STATE_NORMAL;

        ClsBitmap.setProfilePhoto(mContext, imgProfile, mUser.Value.OCM_01, mUser.Value.OCM_52, "", R.drawable.main_profile_no_image);

        requestCTD_SELECT();
        requestARM_SELECT("");
        requestDSH_SELECT();
    }

    private void initLayout() {
        gridBMK = (ExpandableHeightGridView) view.findViewById(R.id.gridBMK);
        gridBMK.setExpanded(true);
        gridBMK.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onBmkGridClick(position);
            }
        });

        gridARM = (ExpandableHeightGridView) view.findViewById(R.id.gridARM);
        gridARM.setExpanded(true);

        imgProfile = view.findViewById(R.id.imgProfile);
        imgProfile.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21)
            imgProfile.setClipToOutline(true);

        tvHomeName = view.findViewById(R.id.tvHomeName);
        tvHomeEmail = view.findViewById(R.id.tvHomeEmail);
        tvHomeDate = view.findViewById(R.id.tvHomeDate);

        btnBmkAdd = view.findViewById(R.id.btnBmkAdd);
        btnBmkAdd.setOnClickListener(v -> goBmkAdd());
        btnBmkDelete = view.findViewById(R.id.btnBmkDelete);
        btnBmkDelete.setOnClickListener(v -> setBmkState(BMK_STATE_DELETE));
        btnBmkCancel = view.findViewById(R.id.btnBmkCancel);
        btnBmkCancel.setOnClickListener(v -> setBmkState(BMK_STATE_NORMAL));

        listView = view.findViewById(R.id.listView);
        tvNotice = view.findViewById(R.id.tvNotice);
        // listView.setOnItemClickListener((parent, view, position, id) -> goWorkRecord(position));

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> requestARM_SELECT(""));

        btnNotList= view.findViewById(R.id.btnNotList);
        btnNotList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BoardMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("DSH_GB", "NOT");
                mContext.startActivity(intent);

            }
        });

//        btnNotNew = view.findViewById(R.id.btnNotNew);
//        btnNotNew.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, BoardDetail.class);
//                intent.putExtra("DSH_01", "");
//                intent.putExtra("DSH_04", "");
//                intent.putExtra("DSH_05", "");
//                intent.putExtra("DSH_09", "0");
//                intent.putExtra("DSH_97","");
//                intent.putExtra("DSH_GB", "NOT");
//
//                mContext.startActivity(intent);
//            }
//        });

        btnSql = view.findViewById(R.id.btnSql);
        btnSql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SqlMain.class);
//                intent.putExtra("DSH_01", "");
//                intent.putExtra("DSH_04", "");
//                intent.putExtra("DSH_05", "");
//                intent.putExtra("DSH_09", "0");
//                intent.putExtra("DSH_97","");
//                intent.putExtra("DSH_GB", "NOT");

                mContext.startActivity(intent);
            }
        });

        btnBeacon = view.findViewById(R.id.btnBeacon);
        btnBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, beaconMain.class);
//                intent.putExtra("DSH_01", "");
//                intent.putExtra("DSH_04", "");
//                intent.putExtra("DSH_05", "");
//                intent.putExtra("DSH_09", "0");
//                intent.putExtra("DSH_97","");
//                intent.putExtra("DSH_GB", "NOT");

                mContext.startActivity(intent);
            }
        });

        btnArc = view.findViewById(R.id.btnArc);
        btnArc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, arclayoutMain.class);
//                intent.putExtra("DSH_01", "");
//                intent.putExtra("DSH_04", "");
//                intent.putExtra("DSH_05", "");
//                intent.putExtra("DSH_09", "0");
//                intent.putExtra("DSH_97","");
//                intent.putExtra("DSH_GB", "NOT");

                mContext.startActivity(intent);
            }
        });
    }

    protected void initialize(){
        tvHomeName.setText(mUser.Value.OCM_02);
        tvHomeEmail.setText(mUser.Value.OCM_21);
        tvHomeDate.setText(ClsDateTime.getNow("yyyy. MM. dd"));

        mList = new ArrayList<>();
        mList2 = new ArrayList<>();

        mAdapter = new BookmarkAdapter(mContext, mList);
        gridBMK.setAdapter(mAdapter);

        mAdapter2 = new AlarmListAdapter(mContext, mList2);
        gridARM.setAdapter(mAdapter2);
    }


    public void requestCTD_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_SELECT(
                BaseConst.URL_HOST,
                "LIST_BOOKMARK",
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
                            closeLoadingBar();

                            Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;

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
            public void onFailure(Call<CTD_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    private void goBmkAdd(){
        Intent intent = new Intent(mContext, ChooseOne.class);
        intent.putExtra("type", "BMK");
        mContext.startActivity(intent);
    }

    private void setBmkState(int setBmkState){
        if(mList.size() > 0){
            BMK_STATE = setBmkState;

            if(setBmkState == BMK_STATE_NORMAL){
                mAdapter.setDelete(false);

                btnBmkCancel.setVisibility(View.GONE);
                btnBmkDelete.setVisibility(View.VISIBLE);
                btnBmkAdd.setVisibility(View.VISIBLE);

            } else if(setBmkState == BMK_STATE_DELETE) {
                mAdapter.setDelete(true);

                btnBmkCancel.setVisibility(View.VISIBLE);
                btnBmkDelete.setVisibility(View.GONE);
                btnBmkAdd.setVisibility(View.GONE);

            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onBmkGridClick(int position) {
        if(BMK_STATE == BMK_STATE_NORMAL){
            ChangeActivityCls changeActivityCls = new ChangeActivityCls(mContext, mList.get(position));
            changeActivityCls.changeService();
        } else if (BMK_STATE == BMK_STATE_DELETE){
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(R.string.alert_bookmark1);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.common_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    requestBMK_CONTROL(mList.get(position));
                }
            });
            builder.setNegativeButton(R.string.common_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            builder.create().show();
        }
    }

    public void requestDSH_SELECT() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

     //   openLoadingBar();

        String strToday = ClsDateTime.getNow("yyyyMMdd");

        Call<DSHModel> call = Http.commute(HttpBaseService.TYPE.POST).DSH_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                mUser.Value.CTM_01,
                "",
                "",
                "",
                ""
        );

        call.enqueue(new Callback<DSHModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<DSHModel> call, Response<DSHModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                        //    closeLoadingBar();

                            Response<DSHModel> response = (Response<DSHModel>) msg.obj;

                            //                            mList = response.body().Data;
//                            if(mList == null)
//                                mList = new ArrayList<>();
//
//                            mAdapter.updateData(mList);
//                            mAdapter.notifyDataSetChanged();
//                            swipeRefresh.setRefreshing(false);

                            if (response.body().Total > 0) {
                                tvNotice.setText(response.body().Data.get(0).DSH_04);
                            }

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<DSHModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }

    public void requestARM_SELECT(String getDate) {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        String strToday = ClsDateTime.getNow("yyyyMM");

        if(getDate.equals("")){ getDate =  strToday;  }
        Call<ARM_Model> call = Http.arm(HttpBaseService.TYPE.POST).ARM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                mUser.Value.CTM_01,
                "",
                mUser.Value.OCM_01,
                "Y",
                "",
                "",
                "",
                getDate, //선택날짜
                "",
                "",
                "",
                ""
        );

        call.enqueue(new Callback<ARM_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<ARM_Model> call, Response<ARM_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();
                            Response<ARM_Model> response = (Response<ARM_Model>) msg.obj;

                            mList2 = response.body().Data;
                            if(mList2 == null)
                                mList2 = new ArrayList<>();


                            mAdapter2.updateData(mList2);
                            mAdapter2.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ARM_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });


    }

    private void requestBMK_CONTROL(CtdVO ctdVO) {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext )){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        Call<CTD_Model> call = Http.bmk(HttpBaseService.TYPE.POST).BMK_CONTROL(
                BaseConst.URL_HOST,
                "DELETE",
                ctdVO.CTD_01,
                ctdVO.CTD_02,
                mUser.Value.OCM_01,
                "",
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

                            requestCTD_SELECT();
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
