package com.linktag.linkapp.ui.vot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.VITModel;
import com.linktag.linkapp.model.VOTModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.VIT_VO;
import com.linktag.linkapp.value_object.VOT_VO;

import java.util.ArrayList;

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
        if(filteredmlist.get(position).VOT_04.equals("")){ //투표 진행중
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.layout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape));
            } else {
                viewHolder.layout.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape));
            }

            if(filteredmlist.get(position).VOT_97.equals(mUser.Value.OCM_01)){ //작성자가 나일때 투표마감 가능
                viewHolder.btnEnd.setVisibility(View.VISIBLE);
                viewHolder.btnEnd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mContext)
                                .setMessage(R.string.vot_list_button_end_dialog_text)
                                .setPositiveButton(R.string.onPositive, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestVOT_CONTROL("VOT_END", filteredmlist.get(position), position);
                                    }
                                })
                                .setNegativeButton(R.string.onNegative, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                })
                                .show();
                    }
                });
            }
            else{
                viewHolder.btnEnd.setVisibility(View.GONE);
            }

            viewHolder.tvRate.setVisibility(View.VISIBLE);
            viewHolder.pbRate.setVisibility(View.VISIBLE);
            viewHolder.pbRate.setMax(filteredmlist.get(position).ALL_CNT);
            viewHolder.pbRate.setProgress(filteredmlist.get(position).VOT_07);
            if(filteredmlist.get(position).ALL_CNT == filteredmlist.get(position).VOT_07){
                viewHolder.pbRate.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_full_listitem));
            }
            else{
                viewHolder.pbRate.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_listitem));
            }

            if(filteredmlist.get(position).VOTE.equals("Y")){ //투표했을때
                viewHolder.recyclerView_VIT.setVisibility(View.VISIBLE);
                viewHolder.tvVotPre.setVisibility(View.GONE);

                requestVIT_SELECT(viewHolder, filteredmlist, position);

                viewHolder.mList_VIT = new ArrayList<>();
                viewHolder.linearLayoutManager_VIT = new LinearLayoutManager(mContext);
                viewHolder.mAdapter_VIT = new VitRecycleAdapter(mContext, viewHolder.mList_VIT, filteredmlist.get(position), intentVO);
                viewHolder.recyclerView_VIT.setLayoutManager(viewHolder.linearLayoutManager_VIT);
                viewHolder.recyclerView_VIT.setAdapter(viewHolder.mAdapter_VIT);
            }
            else{ //투표전
                viewHolder.recyclerView_VIT.setVisibility(View.GONE);
                viewHolder.tvVotPre.setVisibility(View.VISIBLE);
            }

            viewHolder.tvEndDay.setVisibility(View.GONE);
        }
        else{ //마감된 투표
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewHolder.layout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape_gray));
            } else {
                viewHolder.layout.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.list_round_shape_gray));
            }

            viewHolder.btnEnd.setVisibility(View.GONE);
            viewHolder.tvRate.setVisibility(View.GONE);
            viewHolder.pbRate.setVisibility(View.GONE);

            viewHolder.recyclerView_VIT.setVisibility(View.VISIBLE);
            viewHolder.tvVotPre.setVisibility(View.GONE);

            requestVIT_SELECT(viewHolder, filteredmlist, position);

            viewHolder.mList_VIT = new ArrayList<>();
            viewHolder.linearLayoutManager_VIT = new LinearLayoutManager(mContext);
            viewHolder.mAdapter_VIT = new VitRecycleAdapter(mContext, viewHolder.mList_VIT, filteredmlist.get(position), intentVO);
            viewHolder.recyclerView_VIT.setLayoutManager(viewHolder.linearLayoutManager_VIT);
            viewHolder.recyclerView_VIT.setAdapter(viewHolder.mAdapter_VIT);

            viewHolder.tvEndDay.setVisibility(View.VISIBLE);
            viewHolder.tvEndDay.setText(mContext.getString(R.string.vot_list_end_day) + " " + sDateFormat(filteredmlist.get(position).VOT_04));
        }

        viewHolder.tvName.setText(filteredmlist.get(position).VOT_02);

    }

    private void requestVOT_CONTROL(String GUB, VOT_VO data, int position){
        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

//        openLoadingBar();

        String VOT_ID = data.VOT_ID; //컨테이너
        String VOT_01 = data.VOT_01; //일련번호
        String VOT_02 = "";
        String VOT_03 = "";

        String VOT_04 = "";
        String VOT_05 = "";
        String VOT_06 = "";
        String VOT_98 = mUser.Value.OCM_01; //최종수정자

        Call<VOTModel> call = Http.vot(HttpBaseService.TYPE.POST).VOT_CONTROL(
                BaseConst.URL_HOST,
                GUB,
                VOT_ID,
                VOT_01,
                VOT_02,
                VOT_03,

                VOT_04,
                VOT_05,
                VOT_06,
                VOT_98
        );

        call.enqueue(new Callback<VOTModel>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VOTModel> call, Response<VOTModel> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
//                            closeLoadingBar();

                            Response<VOTModel> response = (Response<VOTModel>) msg.obj;

                            ArrayList<VOT_VO> responseData = response.body().Data;

                            if(responseData.get(0).Validation){
                                filteredmlist.get(position).VOT_04 = responseData.get(0).VOT_04;
                            }

                            updateData(filteredmlist);
                            notifyDataSetChanged();
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VOTModel> call, Throwable t){
                Log.d("VOT_SELECT", t.getMessage());
//                closeLoadingBar();
            }
        });
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



                            viewHolder.mList_VIT.clear();

                            if(response.body().Data.size() > 1){
                                for(int i=0; i<response.body().Data.size()-1; i++){
                                    if(response.body().Data.get(i).VIT_RANK == response.body().Data.get(i+1).VIT_RANK){
                                        response.body().Data.get(i).VIT_03 += ", " + response.body().Data.get(i+1).VIT_03;
                                        response.body().Data.remove(i+1);
                                        i--;
                                    }
                                }
                            }

                            viewHolder.mList_VIT = response.body().Data;

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
        LinearLayout layout;
        TextView tvName;
        Button btnEnd;
        TextView tvVotPre;
        TextView tvRate;
        ProgressBar pbRate;
        TextView tvEndDay;

        RecyclerView recyclerView_VIT;
        LinearLayoutManager linearLayoutManager_VIT;
        VitRecycleAdapter mAdapter_VIT;
        ArrayList<VIT_VO> mList_VIT;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.layout);
            tvName = itemView.findViewById(R.id.tvName);
            btnEnd = itemView.findViewById(R.id.btnEnd);
            tvVotPre = itemView.findViewById(R.id.tvVotPre);
            tvRate = itemView.findViewById(R.id.tvRate);
            pbRate = itemView.findViewById(R.id.pbRate);
            tvEndDay = itemView.findViewById(R.id.tvEndDay);

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

    private String sDateFormat(String sDate) {
        String result = sDate.substring(0,4) + "." + sDate.substring(4,6) + "." + sDate.substring(6,8);

        return result;
    }

}
