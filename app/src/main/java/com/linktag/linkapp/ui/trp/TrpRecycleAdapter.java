package com.linktag.linkapp.ui.trp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ARMModel;
import com.linktag.linkapp.model.TRDModel;
import com.linktag.linkapp.model.TRPModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm_service.Alarm_Receiver;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.ArmVO;
import com.linktag.linkapp.value_object.TrdVO;
import com.linktag.linkapp.value_object.TrpVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrpRecycleAdapter extends RecyclerView.Adapter<TrpRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<TrpVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;


    TrpRecycleAdapter(Context context, ArrayList<TrpVO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
    }

    @NonNull
    @Override
    public TrpRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_trp_list
                , parent, false);
        TrpRecycleAdapter.ViewHolder viewHolder = new TrpRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.tv_name.setText(mList.get(position).TRP_02);
        viewHolder.tv_week.setText(patternToWeeks(mList.get(position).TRP_04));


        requestTRD_SELECT(viewHolder, mList, position);

        viewHolder.mList_trd = new ArrayList<>();
        viewHolder.linearLayoutManager_TRD = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        viewHolder.mAdapter_trd = new TrdRecycleAdapter_horizontal(mContext, viewHolder.mList_trd);

        viewHolder.recyclerView_TRD.setLayoutManager(viewHolder.linearLayoutManager_TRD);
        viewHolder.recyclerView_TRD.setAdapter(viewHolder.mAdapter_trd);


        if (mList.get(position).ARM_03.equals("Y")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
        } else if (mList.get(position).ARM_03.equals("N")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
        }

        viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.mList_trd.size() ==0){
                    Toast.makeText(mContext,"지정된 알림이 없습니다.",Toast.LENGTH_LONG).show();
                    return;
                }

                if (mList.get(position).ARM_03.equals("Y")) {
                    mList.get(position).setARM_03("N");
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
                    Toast.makeText(mContext, "[" + mList.get(position).TRP_02 + "]- 알림 OFF", Toast.LENGTH_SHORT).show();
                } else {
                    mList.get(position).setARM_03("Y");
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
                    Toast.makeText(mContext, "[" + mList.get(position).TRP_02 + "]- 알림 ON", Toast.LENGTH_SHORT).show();
                }

                TrpVO trpvo = new TrpVO();
                trpvo.setTRP_ID(mList.get(position).TRP_ID);
                trpvo.setTRP_01(mList.get(position).TRP_01);
                trpvo.setTRP_02(mList.get(position).TRP_02);
                trpvo.setTRP_03(mList.get(position).TRP_03);
                trpvo.setTRP_04(mList.get(position).TRP_04);
                trpvo.setTRP_05(mList.get(position).TRP_05);
                trpvo.setTRP_06(mList.get(position).TRP_06);
                trpvo.setTRP_07(mList.get(position).TRP_07);
                trpvo.setTRP_97(mList.get(position).TRP_97);
                trpvo.setARM_03(mList.get(position).ARM_03);

                requestTRP_CONTROL(trpvo, position);

            }
        });

    }

    public void requestTRD_SELECT(ViewHolder viewHolder, ArrayList<TrpVO> mList, int position) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<TRDModel> call = Http.trd(HttpBaseService.TYPE.POST).TRD_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                mList.get(position).TRP_ID,
                mList.get(position).TRP_01,
                ""
        );


        call.enqueue(new Callback<TRDModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<TRDModel> call, Response<TRDModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<TRDModel> response = (Response<TRDModel>) msg.obj;

                            viewHolder.mList_trd = response.body().Data;
                            if (viewHolder.mList_trd == null)
                                viewHolder.mList_trd = new ArrayList<>();

                            if (viewHolder.mList_trd.size() ==0){
                                viewHolder.tv_alarmNone.setVisibility(View.VISIBLE);
                                viewHolder.recyclerView_TRD.setVisibility(View.GONE);
                            }
                            else{
                                viewHolder.tv_alarmNone.setVisibility(View.GONE);
                                viewHolder.recyclerView_TRD.setVisibility(View.VISIBLE);
                            }
                            viewHolder.mAdapter_trd.updateData(viewHolder.mList_trd);
                            viewHolder.mAdapter_trd.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<TRDModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }

    public String patternToWeeks(String pattern) {

        String[] array_pattern;
        String result_Weeks = "";
        array_pattern = pattern.split("");
        if (pattern.equals("YYYYYYY")) {
            result_Weeks = "매일";
            return result_Weeks;
        } else {
            for (int i = 0; i < array_pattern.length; i++) {
                if (array_pattern[i].equals("Y") && i == 1) {
                    result_Weeks += "일 ";
                } else if (array_pattern[i].equals("Y") && i == 2) {
                    result_Weeks += "월 ";
                } else if (array_pattern[i].equals("Y") && i == 3) {
                    result_Weeks += "화 ";
                } else if (array_pattern[i].equals("Y") && i == 4) {
                    result_Weeks += "수 ";
                } else if (array_pattern[i].equals("Y") && i == 5) {
                    result_Weeks += "목 ";
                } else if (array_pattern[i].equals("Y") && i == 6) {
                    result_Weeks += "금 ";
                } else if (array_pattern[i].equals("Y") && i == 7) {
                    result_Weeks += "토 ";
                }
            }
        }
        return result_Weeks;
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView tv_name;
        TextView tv_week;

        private TrdRecycleAdapter_horizontal mAdapter_trd;
        private LinearLayoutManager linearLayoutManager_TRD;
        private ArrayList<TrdVO> mList_trd;

        RecyclerView recyclerView_TRD;

        TextView tv_alarmNone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_week = itemView.findViewById(R.id.tv_week);

            recyclerView_TRD = itemView.findViewById(R.id.recyclerView_TRD);

            tv_alarmNone = itemView.findViewById(R.id.tv_alarmNone);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    TrpVO trpvo = new TrpVO();
                    trpvo.setTRP_ID(mList.get(position).TRP_ID);
                    trpvo.setTRP_01(mList.get(position).TRP_01);
                    trpvo.setTRP_02(mList.get(position).TRP_02);
                    trpvo.setTRP_03(mList.get(position).TRP_03);
                    trpvo.setTRP_04(mList.get(position).TRP_04);
                    trpvo.setTRP_05(mList.get(position).TRP_05);
                    trpvo.setTRP_06(mList.get(position).TRP_06);
                    trpvo.setTRP_07(mList.get(position).TRP_07);
                    trpvo.setTRP_97(mList.get(position).TRP_97);
                    trpvo.setARM_03(mList.get(position).ARM_03);

                    Intent intent = new Intent(mContext, DetailTrp.class);
                    intent.putExtra("TrpVO", trpvo);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    public void updateData(ArrayList<TrpVO> list) {
        mList = list;
    }


    public void requestTRP_CONTROL(TrpVO trpVO, int position) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<TRPModel> call = Http.trp(HttpBaseService.TYPE.POST).TRP_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE_2",
                trpVO.TRP_ID,
                trpVO.TRP_01,
                trpVO.TRP_02,
                trpVO.TRP_03,
                trpVO.TRP_04,
                trpVO.TRP_05,
                trpVO.TRP_06,
                trpVO.TRP_07,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                trpVO.ARM_03
        );


        call.enqueue(new Callback<TRPModel>() {
            @Override
            public void onResponse(Call<TRPModel> call, Response<TRPModel> response) {

            }

            @Override
            public void onFailure(Call<TRPModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }

}
