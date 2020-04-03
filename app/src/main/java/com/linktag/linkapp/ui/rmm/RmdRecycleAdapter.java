package com.linktag.linkapp.ui.rmm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.RMRModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.RMD_VO;
import com.linktag.linkapp.value_object.RMR_VO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RmdRecycleAdapter extends RecyclerView.Adapter<RmdRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<RMD_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    private String RMR_03;
    private String RMR_04ST;
    private String RMR_04ED;

//    public static String RMR_02; //예약
//    public static List<String> RMR_04_list = new ArrayList<>(); //예약
//    public static HashMap<String, String> RMR_04_list = new HashMap<>(); //예약
    public static ArrayList<ReserveList> RMR_04_list = new ArrayList<>(); //예약
//    private Boolean reserveBool = true;

    RmdRecycleAdapter(Context context, ArrayList<RMD_VO> list, String RMR_03_tmp, String RMR_04ST_tmp, String RMR_04ED_tmp) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();

        RMR_03 = RMR_03_tmp;
        RMR_04ST = RMR_04ST_tmp;
        RMR_04ED = RMR_04ED_tmp;
    }

    @NonNull
    @Override
    public RmdRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_rmd
                , parent, false);
        RmdRecycleAdapter.ViewHolder viewHolder = new RmdRecycleAdapter.ViewHolder(view);

//        viewHolder.mList_RMR = new ArrayList<>();
////        viewHolder.mList_RMR_tmp = new ArrayList<>();
//        viewHolder.linearLayoutManager_RMR = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//        viewHolder.recyclerView_RMR.setLayoutManager(viewHolder.linearLayoutManager_RMR);
//        viewHolder.mAdapter_RMR = new RmrRecycleAdapter(mContext, viewHolder.mList_RMR);
//        viewHolder.recyclerView_RMR.setAdapter(viewHolder.mAdapter_RMR);

//        viewHolder.recyclerView_RMR.getRecycledViewPool().setMaxRecycledViews(0, 0);

//        requestRMR_SELECT(viewHolder, mList, position);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.tvName.setText(mList.get(position).RMD_03);
        viewHolder.tvEquip.setText(mList.get(position).RMD_04);

//        int position = getAdapterPosition();
        if(mList.get(position).RMD_97.equals(mUser.Value.OCM_01)){ //master일때 - 연습실편집
            viewHolder.btnReserve.setText(R.string.list_edit);
            viewHolder.btnReserve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RMD_VO RMD = mList.get(position);

                    Intent intent = new Intent(mContext, RmdDetail.class);
                    intent.putExtra("RMD", RMD);

                    mContext.startActivity(intent);
                }
            });
        }
        else{ //user일때 - 예약
            viewHolder.btnReserve.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    RMD_VO RMD = mList.get(position);

//                    Collections.sort(RMR_04_list);
                    Collections.sort(RMR_04_list);
                    //값 테스트해보고 SP타도록...
                    if(RMR_04_list.size()>0){
                        for(int i=0; i<RMR_04_list.size(); i++){
                            if(RMR_04_list.get(i).getCode().equals(RMD.RMD_02)){ //해당연습실만 예약
                                String RMD_04 = RMR_04_list.get(i).getTime();
                                requestRMR_CONTROL(RMD.RMD_ID, RMD.RMD_01, RMD.RMD_02, RMD_04);
                            }
                        }

//                        RMR_04_list.clear();
                        Predicate<ReserveList> condition = reserveList -> reserveList.getCode().equals(RMD.RMD_02);
                        RMR_04_list.removeIf(condition);
                        requestRMR_SELECT(viewHolder, mList, position);
                    }
                    else{
                        Toast.makeText(mContext, mContext.getString(R.string.rmm_list_reserve_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        requestRMR_SELECT(viewHolder, mList, position);

        viewHolder.mList_RMR = new ArrayList<>();
        viewHolder.linearLayoutManager_RMR = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        viewHolder.mAdapter_RMR = new RmrRecycleAdapter(mContext, viewHolder.mList_RMR);

        viewHolder.recyclerView_RMR.setLayoutManager(viewHolder.linearLayoutManager_RMR);
        viewHolder.recyclerView_RMR.setAdapter(viewHolder.mAdapter_RMR);



    }

    private void requestRMR_SELECT(ViewHolder viewHolder, ArrayList<RMD_VO> mList, int position){
        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

//        openLoadingBar();

        String GUBUN = "LIST";
        String RMR_ID = mList.get(position).RMD_ID; //컨테이너
        String RMR_01 = mList.get(position).RMD_01; //Master일련번호(RMM_01)
        String RMR_02 = mList.get(position).RMD_02; //연습실일련번호(RMD_02)
//        String RMR_03 = ""; //일자

//        String RMR_04ST = ""; //시작시간
//        String RMR_04ED = ""; //종료시간
        String RMR_05 = "";

        Call<RMRModel> call = Http.rmr(HttpBaseService.TYPE.POST).RMR_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                RMR_ID,
                RMR_01,
                RMR_02,
                RMR_03,

                RMR_04ST,
                RMR_04ED,
                RMR_05
        );

        call.enqueue(new Callback<RMRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RMRModel> call, Response<RMRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<RMRModel> response = (Response<RMRModel>) msg.obj;

//                            viewHolder.mList_RMR_tmp = response.body().Data;
//                            if (viewHolder.mList_RMR_tmp == null)
//                                viewHolder.mList_RMR_tmp = new ArrayList<>();

//                            viewHolder.mList_RMR = response.body().Data;
//                            if (viewHolder.mList_RMR == null)
//                                viewHolder.mList_RMR = new ArrayList<>();

                            int i = 0;
                            Calendar time_c = Calendar.getInstance();
                            time_c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(RMR_04ST.substring(0,2)));
                            time_c.set(Calendar.MINUTE, Integer.parseInt(RMR_04ST.substring(2)));
                            time_c.set(Calendar.SECOND, 0);
                            time_c.set(Calendar.MILLISECOND, 0);

                            Calendar time_c_ed = Calendar.getInstance();
                            time_c_ed.set(Calendar.HOUR_OF_DAY, Integer.parseInt(RMR_04ED.substring(0,2)));
                            time_c_ed.set(Calendar.MINUTE, Integer.parseInt(RMR_04ED.substring(2)));
                            time_c_ed.set(Calendar.SECOND, 0);
                            time_c_ed.set(Calendar.MILLISECOND, 0);

//                            viewHolder.mList_RMR_tmp.clear();
                            viewHolder.mList_RMR.clear();
//                            viewHolder.mAdapter_RMR.notifyDataSetChanged();
                            while(time_c.compareTo(time_c_ed) < 0){
                                String time_s = (time_c.get(Calendar.HOUR_OF_DAY)<10 ? "0" + String.valueOf(time_c.get(Calendar.HOUR_OF_DAY)) : String.valueOf(time_c.get(Calendar.HOUR_OF_DAY))) + (time_c.get(Calendar.MINUTE)<10 ? "0" + String.valueOf(time_c.get(Calendar.MINUTE)) : String.valueOf(time_c.get(Calendar.MINUTE)));
                                if(response.body().Data.size() > 0){
                                    if(time_s.equals(response.body().Data.get(i).RMR_04)){
                                        response.body().Data.get(i).boolChange = false;
                                        viewHolder.mList_RMR.add(response.body().Data.get(i));
//                                    viewHolder.mAdapter_RMR.notifyDataSetChanged();
                                        if(i < response.body().Data.size() - 1){
                                            i++;
                                        }
                                    }
                                    else{
                                        RMR_VO RMR_tmp = new RMR_VO();
                                        RMR_tmp.RMR_ID = RMR_ID;
                                        RMR_tmp.RMR_01 = RMR_01;
                                        RMR_tmp.RMR_02 = RMR_02;
                                        RMR_tmp.RMR_03 = RMR_03;
                                        RMR_tmp.RMR_04 = time_s;
                                        RMR_tmp.RMR_05 = "";
                                        RMR_tmp.RMR_05_NM = "";
                                        RMR_tmp.boolChange = false;
                                        viewHolder.mList_RMR.add(RMR_tmp);
                                    }
                                }
                                else{
                                    RMR_VO RMR_tmp = new RMR_VO();
                                    RMR_tmp.RMR_ID = RMR_ID;
                                    RMR_tmp.RMR_01 = RMR_01;
                                    RMR_tmp.RMR_02 = RMR_02;
                                    RMR_tmp.RMR_03 = RMR_03;
                                    RMR_tmp.RMR_04 = time_s;
                                    RMR_tmp.RMR_05 = "";
                                    RMR_tmp.RMR_05_NM = "";
                                    RMR_tmp.boolChange = false;
                                    viewHolder.mList_RMR.add(RMR_tmp);
//                                    viewHolder.mAdapter_RMR.notifyDataSetChanged();
                                }

                                time_c.add(Calendar.MINUTE, 30); //30분 고정
                            }


                            viewHolder.mAdapter_RMR.updateData(viewHolder.mList_RMR);
                            viewHolder.mAdapter_RMR.notifyDataSetChanged();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMRModel> call, Throwable t){
                Log.d("RMD_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    private void requestRMR_CONTROL(String RMR_ID, String RMR_01, String RMR_02, String RMR_04) {

        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

//        openLoadingBar();

        String GUBUN = "RESERVE";
        String RMR_05 = mUser.Value.OCM_01; //사용자
        String RMR_98 = mUser.Value.OCM_01; //최종수정자

        Call<RMRModel> call = Http.rmr(HttpBaseService.TYPE.POST).RMR_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                RMR_ID,
                RMR_01,
                RMR_02,
                RMR_03,

                RMR_04,
                RMR_05,
                RMR_98
        );

        call.enqueue(new Callback<RMRModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<RMRModel> call, Response<RMRModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<RMRModel> response = (Response<RMRModel>) msg.obj;

                            if(response.body().Data.size()==0){
                                Toast.makeText(mContext, RMR_04.substring(0,2) + ":" + RMR_04.substring(2) + " " + mContext.getString(R.string.rmm_list_reserve_fail), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<RMRModel> call, Throwable t){
                Log.d("RMR_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnReserve;
        TextView tvEquip;

        RecyclerView recyclerView_RMR;
        LinearLayoutManager linearLayoutManager_RMR;
        RmrRecycleAdapter mAdapter_RMR;
        ArrayList<RMR_VO> mList_RMR;
        ArrayList<RMR_VO> mList_RMR_tmp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            btnReserve = itemView.findViewById(R.id.btnReserve);
            tvEquip = itemView.findViewById(R.id.tvEquip);

            recyclerView_RMR = itemView.findViewById(R.id.recyclerView_RMR);

//            mList_RMR = new ArrayList<>();
//            linearLayoutManager_RMR = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//            recyclerView_RMR.setLayoutManager(linearLayoutManager_RMR);
//            mAdapter_RMR = new RmrRecycleAdapter(mContext, mList_RMR);
//            recyclerView_RMR.setAdapter(mAdapter_RMR);

        }
    }

    public void updateData(ArrayList<RMD_VO> list) {
        mList = list;
    }

}
