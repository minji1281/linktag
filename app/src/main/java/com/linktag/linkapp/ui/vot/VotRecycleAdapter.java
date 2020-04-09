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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.VITModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.rmm.ReserveList;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.VIT_VO;
import com.linktag.linkapp.value_object.VOT_VO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VotRecycleAdapter extends RecyclerView.Adapter<VotRecycleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<VOT_VO> mList;
    private ArrayList<VOT_VO> filteredmlist;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
    private CtdVO intentVO;

    Filter listFilter;

    VotRecycleAdapter(Context context, ArrayList<VOT_VO> list, CtdVO intentVO_tmp) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
        filteredmlist = list;
        intentVO = intentVO_tmp;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null)
            listFilter = new ListFilter();

        return listFilter;
    }


    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            String charString = constraint.toString();
            if (charString.isEmpty()) {
                results.values = mList;
                results.count = mList.size();
            } else {
                ArrayList<VOT_VO> itemList = new ArrayList<>();
                for (VOT_VO item : mList) {
                    if (item.VOT_02.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        itemList.add(item);
                    }
                }
                results.values = itemList;
                results.count = itemList.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredmlist = (ArrayList<VOT_VO>) results.values;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public VotRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_vot
                , parent, false);
        VotRecycleAdapter.ViewHolder viewHolder = new VotRecycleAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.tvName.setText(filteredmlist.get(position).VOT_02);

        viewHolder.pbRate.setMax(filteredmlist.get(position).ALL_CNT);
        viewHolder.pbRate.setProgress(filteredmlist.get(position).VOT_07);
        if(filteredmlist.get(position).ALL_CNT == filteredmlist.get(position).VOT_07){
            viewHolder.pbRate.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_full_listitem));
        }
        else{
            viewHolder.pbRate.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_listitem));
        }

//        requestVIT_SELECT(viewHolder, mList, position);

//        viewHolder.mList_VIT = new ArrayList<>();
//        viewHolder.linearLayoutManager_VIT = new LinearLayoutManager(mContext);
//        viewHolder.mAdapter_VIT = new VitRecycleAdapter(mContext, viewHolder.mList_VIT);
//        viewHolder.recyclerView_VIT.setLayoutManager(viewHolder.linearLayoutManager_VIT);
//        viewHolder.recyclerView_VIT.setAdapter(viewHolder.mAdapter_VIT);

//        requestVIT_SELECT(viewHolder, mList, position);

        if(filteredmlist.get(position).VOTE.equals("Y")){ //투표했을때
            viewHolder.recyclerView_VIT.setVisibility(View.VISIBLE);
            viewHolder.tvVotPre.setVisibility(View.GONE);

            requestVIT_SELECT(viewHolder, filteredmlist, position);

            viewHolder.mList_VIT = new ArrayList<>();
            viewHolder.linearLayoutManager_VIT = new LinearLayoutManager(mContext);
            viewHolder.mAdapter_VIT = new VitRecycleAdapter(mContext, viewHolder.mList_VIT);
            viewHolder.recyclerView_VIT.setLayoutManager(viewHolder.linearLayoutManager_VIT);
            viewHolder.recyclerView_VIT.setAdapter(viewHolder.mAdapter_VIT);
        }
        else{ //투표전
            viewHolder.recyclerView_VIT.setVisibility(View.GONE);
            viewHolder.tvVotPre.setVisibility(View.VISIBLE);
        }

    }

    private void requestVIT_SELECT(ViewHolder viewHolder, ArrayList<VOT_VO> mList, int position){
        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

//        openLoadingBar();

        String GUBUN = "LIST";
        String VIT_ID = mList.get(position).VOT_ID; //컨테이너
        String VIT_01 = mList.get(position).VOT_01; //투표코드(VOT_01)

        Call<VITModel> call = Http.vit(HttpBaseService.TYPE.POST).VIT_SELECT(
                BaseConst.URL_HOST,
                GUBUN,
                VIT_ID,
                VIT_01
        );

        call.enqueue(new Callback<VITModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VITModel> call, Response<VITModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<VITModel> response = (Response<VITModel>) msg.obj;

                            viewHolder.mList_VIT = response.body().Data;
                            if(viewHolder.mList_VIT == null)
                                viewHolder.mList_VIT = new ArrayList<>();

//                            int i = 0;
//                            Calendar time_c = Calendar.getInstance();
//                            time_c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(RMR_04ST.substring(0,2)));
//                            time_c.set(Calendar.MINUTE, Integer.parseInt(RMR_04ST.substring(2)));
//                            time_c.set(Calendar.SECOND, 0);
//                            time_c.set(Calendar.MILLISECOND, 0);
//
//                            Calendar time_c_ed = Calendar.getInstance();
//                            time_c_ed.set(Calendar.HOUR_OF_DAY, Integer.parseInt(RMR_04ED.substring(0,2)));
//                            time_c_ed.set(Calendar.MINUTE, Integer.parseInt(RMR_04ED.substring(2)));
//                            time_c_ed.set(Calendar.SECOND, 0);
//                            time_c_ed.set(Calendar.MILLISECOND, 0);
//
//                            viewHolder.mList_RMR.clear();
//                            while(time_c.compareTo(time_c_ed) < 0){
//                                String time_s = (time_c.get(Calendar.HOUR_OF_DAY)<10 ? "0" + String.valueOf(time_c.get(Calendar.HOUR_OF_DAY)) : String.valueOf(time_c.get(Calendar.HOUR_OF_DAY))) + (time_c.get(Calendar.MINUTE)<10 ? "0" + String.valueOf(time_c.get(Calendar.MINUTE)) : String.valueOf(time_c.get(Calendar.MINUTE)));
//                                if(response.body().Data.size() > 0){
//                                    if(time_s.equals(response.body().Data.get(i).RMR_04)){
//                                        response.body().Data.get(i).boolChange = false;
//                                        viewHolder.mList_RMR.add(response.body().Data.get(i));
//                                        if(i < response.body().Data.size() - 1){
//                                            i++;
//                                        }
//                                    }
//                                    else{
//                                        RMR_VO RMR_tmp = new RMR_VO();
//                                        RMR_tmp.RMR_ID = RMR_ID;
//                                        RMR_tmp.RMR_01 = RMR_01;
//                                        RMR_tmp.RMR_02 = RMR_02;
//                                        RMR_tmp.RMR_03 = RMR_03;
//                                        RMR_tmp.RMR_04 = time_s;
//                                        RMR_tmp.RMR_05 = "";
//                                        RMR_tmp.RMR_05_NM = "";
//                                        RMR_tmp.boolChange = false;
//                                        viewHolder.mList_RMR.add(RMR_tmp);
//                                    }
//                                }
//                                else{
//                                    RMR_VO RMR_tmp = new RMR_VO();
//                                    RMR_tmp.RMR_ID = RMR_ID;
//                                    RMR_tmp.RMR_01 = RMR_01;
//                                    RMR_tmp.RMR_02 = RMR_02;
//                                    RMR_tmp.RMR_03 = RMR_03;
//                                    RMR_tmp.RMR_04 = time_s;
//                                    RMR_tmp.RMR_05 = "";
//                                    RMR_tmp.RMR_05_NM = "";
//                                    RMR_tmp.boolChange = false;
//                                    viewHolder.mList_RMR.add(RMR_tmp);
//                                }
//
//                                time_c.add(Calendar.MINUTE, 30); //30분 고정
//                            }


                            viewHolder.mAdapter_VIT.updateData(viewHolder.mList_VIT);
                            viewHolder.mAdapter_VIT.notifyDataSetChanged();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VITModel> call, Throwable t){
                Log.d("VIT_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredmlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnEnd;
        TextView tvVotPre;
        ProgressBar pbRate;

        RecyclerView recyclerView_VIT;
        LinearLayoutManager linearLayoutManager_VIT;
        VitRecycleAdapter mAdapter_VIT;
        ArrayList<VIT_VO> mList_VIT;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            btnEnd = itemView.findViewById(R.id.btnEnd);
            tvVotPre = itemView.findViewById(R.id.tvVotPre);
            pbRate = itemView.findViewById(R.id.pbRate);

            recyclerView_VIT = itemView.findViewById(R.id.recyclerView_VIT);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    VOT_VO VOT = filteredmlist.get(position);
                    Intent intent = new Intent(mContext, VotDetail.class);
                    intent.putExtra("VOT", VOT);
                    intent.putExtra("intentVO", intentVO);

                    mContext.startActivity(intent);
                }
            });
        }
    }

    public void updateData(ArrayList<VOT_VO> list) {
        mList = list;
    }

}
