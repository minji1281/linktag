package com.linktag.linkapp.ui.vot;

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
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.rmm.ReserveList;
import com.linktag.linkapp.value_object.VOT_VO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VotRecycleAdapter extends RecyclerView.Adapter<VotRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VOT_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    VotRecycleAdapter(Context context, ArrayList<VOT_VO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
    }

    @NonNull
    @Override
    public VotRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_rmd
                , parent, false);
        VotRecycleAdapter.ViewHolder viewHolder = new VotRecycleAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
//        clearCalTime(viewHolder.TODAY);
//        clearCalTime(viewHolder.RMR_03);
//        viewHolder.RMR_03.set(Calendar.YEAR, Integer.valueOf(RMR_03.substring(0,4)));
//        viewHolder.RMR_03.set(Calendar.DAY_OF_MONTH, Integer.valueOf(RMR_03.substring(4,6)));
//        viewHolder.RMR_03.set(Calendar.DATE, Integer.valueOf(RMR_03.substring(6)));
//
//        viewHolder.tvName.setText(mList.get(position).RMD_03);
//        viewHolder.tvEquip.setText(mList.get(position).RMD_04);
//
//        if(mList.get(position).RMD_97.equals(mUser.Value.OCM_01)){ //master일때 - 연습실편집
//            viewHolder.btnReserve.setText(R.string.list_edit);
//            viewHolder.btnReserve.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    VOT_VO RMD = mList.get(position);
//                    RMD.RMM_03 = RMM_03;
//                    RMD.RMM_04 = RMM_04;
//                    RMD.RMM_98 = RMM_98;
//
//                    Intent intent = new Intent(mContext, RmdDetail.class);
//                    intent.putExtra("RMD", RMD);
//
//                    mContext.startActivity(intent);
//                }
//            });
//        }
//        else{ //user일때 - 예약
//            if(viewHolder.RMR_03.compareTo(viewHolder.TODAY) < 0){
//                viewHolder.btnReserve.setVisibility(View.GONE);
//            }
//            else{
//                viewHolder.btnReserve.setOnClickListener(new View.OnClickListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.N)
//                    @Override
//                    public void onClick(View view) {
//                        VOT_VO RMD = mList.get(position);
//
//                        Collections.sort(RMR_04_list);
//                        if(RMR_04_list.size()>0){
//                            for(int i=0; i<RMR_04_list.size(); i++){
//                                if(RMR_04_list.get(i).getCode().equals(RMD.RMD_02)){ //해당연습실만 예약
//                                    String RMD_04 = RMR_04_list.get(i).getTime();
//                                    ReserveList tmp = new ReserveList(RMR_04_list.get(i).getCode(), RMR_04_list.get(i).getTime());
//                                    RMR_04_list.remove(tmp);
//                                    i--;
//
//                                    String EndGub = "Y";
//                                    for(int j=0; j<RMR_04_list.size(); j++){
//                                        if(RMR_04_list.get(j).getCode().equals(RMD.RMD_02)){
//                                            EndGub = "N"; //마지막 아니라는뜻
//                                            break;
//                                        }
//                                    }
//
//                                    requestRMR_CONTROL(RMD.RMD_ID, RMD.RMD_01, RMD.RMD_02, RMD_04, EndGub, viewHolder, position);
//                                }
//                            }
//                        }
//                        else{
//                            Toast.makeText(mContext, mContext.getString(R.string.rmm_list_reserve_fail), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }
//
//        requestRMR_SELECT(viewHolder, mList, position);
//
//        viewHolder.mList_RMR = new ArrayList<>();
//        viewHolder.linearLayoutManager_RMR = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//        viewHolder.mAdapter_RMR = new RmrRecycleAdapter(mContext, viewHolder.mList_RMR);
//
//        viewHolder.recyclerView_RMR.setLayoutManager(viewHolder.linearLayoutManager_RMR);
//        viewHolder.recyclerView_RMR.setAdapter(viewHolder.mAdapter_RMR);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnReserve;
        TextView tvEquip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            btnReserve = itemView.findViewById(R.id.btnReserve);
            tvEquip = itemView.findViewById(R.id.tvEquip);
        }
    }

    public void updateData(ArrayList<VOT_VO> list) {
        mList = list;
    }

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

}
