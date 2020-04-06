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
    private String RMM_03;
    private String RMM_04;
    private String RMM_98;

    public static ArrayList<ReserveList> RMR_04_list = new ArrayList<>(); //예약

    RmdRecycleAdapter(Context context, ArrayList<RMD_VO> list, String RMR_03_tmp, String RMR_04ST_tmp, String RMR_04ED_tmp, String RMM_03_tmp, String RMM_04_tmp, String RMM_98_tmp) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();

        RMR_03 = RMR_03_tmp;
        RMR_04ST = RMR_04ST_tmp;
        RMR_04ED = RMR_04ED_tmp;
        RMM_03 = RMM_03_tmp;
        RMM_04 = RMM_04_tmp;
        RMM_98 = RMM_98_tmp;
    }

    @NonNull
    @Override
    public RmdRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_rmd
                , parent, false);
        RmdRecycleAdapter.ViewHolder viewHolder = new RmdRecycleAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        clearCalTime(viewHolder.TODAY);
        clearCalTime(viewHolder.RMR_03);
        viewHolder.RMR_03.set(Calendar.YEAR, Integer.valueOf(RMR_03.substring(0,4)));
        viewHolder.RMR_03.set(Calendar.DAY_OF_MONTH, Integer.valueOf(RMR_03.substring(4,6)));
        viewHolder.RMR_03.set(Calendar.DATE, Integer.valueOf(RMR_03.substring(6)));

        viewHolder.tvName.setText(mList.get(position).RMD_03);
        viewHolder.tvEquip.setText(mList.get(position).RMD_04);

        if(mList.get(position).RMD_97.equals(mUser.Value.OCM_01)){ //master일때 - 연습실편집
            viewHolder.btnReserve.setText(R.string.list_edit);
            viewHolder.btnReserve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RMD_VO RMD = mList.get(position);
                    RMD.RMM_03 = RMM_03;
                    RMD.RMM_04 = RMM_04;
                    RMD.RMM_98 = RMM_98;

                    Intent intent = new Intent(mContext, RmdDetail.class);
                    intent.putExtra("RMD", RMD);

                    mContext.startActivity(intent);
                }
            });
        }
        else{ //user일때 - 예약
            if(viewHolder.RMR_03.compareTo(viewHolder.TODAY) < 0){
                viewHolder.btnReserve.setVisibility(View.GONE);
            }
            else{
                viewHolder.btnReserve.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        RMD_VO RMD = mList.get(position);

                        Collections.sort(RMR_04_list);
                        if(RMR_04_list.size()>0){
                            for(int i=0; i<RMR_04_list.size(); i++){
                                if(RMR_04_list.get(i).getCode().equals(RMD.RMD_02)){ //해당연습실만 예약
                                    String RMD_04 = RMR_04_list.get(i).getTime();
                                    ReserveList tmp = new ReserveList(RMR_04_list.get(i).getCode(), RMR_04_list.get(i).getTime());
                                    RMR_04_list.remove(tmp);
                                    i--;

                                    String EndGub = "Y";
                                    for(int j=0; j<RMR_04_list.size(); j++){
                                        if(RMR_04_list.get(j).getCode().equals(RMD.RMD_02)){
                                            EndGub = "N"; //마지막 아니라는뜻
                                            break;
                                        }
                                    }

                                    requestRMR_CONTROL(RMD.RMD_ID, RMD.RMD_01, RMD.RMD_02, RMD_04, EndGub, viewHolder, position);
                                }
                            }
                        }
                        else{
                            Toast.makeText(mContext, mContext.getString(R.string.rmm_list_reserve_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
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

                            viewHolder.mList_RMR.clear();
                            while(time_c.compareTo(time_c_ed) < 0){
                                String time_s = (time_c.get(Calendar.HOUR_OF_DAY)<10 ? "0" + String.valueOf(time_c.get(Calendar.HOUR_OF_DAY)) : String.valueOf(time_c.get(Calendar.HOUR_OF_DAY))) + (time_c.get(Calendar.MINUTE)<10 ? "0" + String.valueOf(time_c.get(Calendar.MINUTE)) : String.valueOf(time_c.get(Calendar.MINUTE)));
                                if(response.body().Data.size() > 0){
                                    if(time_s.equals(response.body().Data.get(i).RMR_04)){
                                        response.body().Data.get(i).boolChange = false;
                                        viewHolder.mList_RMR.add(response.body().Data.get(i));
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

    private void requestRMR_CONTROL(String RMR_ID, String RMR_01, String RMR_02, String RMR_04, String EndGub, ViewHolder viewHolder, int position) {

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

                            if(EndGub.equals("Y")){ //마지막 CONTROL
                                requestRMR_SELECT(viewHolder, mList, position);
                            }
                            else{
                                if(response.body().Data.size()==0){
                                    Toast.makeText(mContext, RMR_04.substring(0,2) + ":" + RMR_04.substring(2) + " " + mContext.getString(R.string.rmm_list_reserve_fail), Toast.LENGTH_SHORT).show();
                                }
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

        Calendar TODAY = Calendar.getInstance();
        Calendar RMR_03 = Calendar.getInstance();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            btnReserve = itemView.findViewById(R.id.btnReserve);
            tvEquip = itemView.findViewById(R.id.tvEquip);

            recyclerView_RMR = itemView.findViewById(R.id.recyclerView_RMR);
        }
    }

    public void updateData(ArrayList<RMD_VO> list) {
        mList = list;
    }

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

}
