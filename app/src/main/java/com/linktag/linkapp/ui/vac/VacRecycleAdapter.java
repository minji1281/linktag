package com.linktag.linkapp.ui.vac;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.VACModel;
import com.linktag.linkapp.model.VADModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.VacVO;
import com.linktag.linkapp.value_object.VadVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VacRecycleAdapter extends RecyclerView.Adapter<VacRecycleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<VacVO> mList;
    private ArrayList<VacVO> filteredmlist;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    Filter listFilter;

    VacRecycleAdapter(Context context, ArrayList<VacVO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
        filteredmlist = list;
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
                ArrayList<VacVO> itemList = new ArrayList<>();
                for (VacVO item : mList) {
                    if (item.VAC_02.toLowerCase().contains(constraint.toString().toLowerCase())) {
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

            filteredmlist = (ArrayList<VacVO>) results.values;

            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public VacRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_vac_list
                , parent, false);
        VacRecycleAdapter.ViewHolder viewHolder = new VacRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.tv_name.setText(filteredmlist.get(position).VAC_02);

        requestVAD_SELECT(viewHolder, filteredmlist, position);

        viewHolder.filteredmlist_vad = new ArrayList<>();
        viewHolder.linearLayoutManager_VAD = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        viewHolder.mAdapter_vad = new VadRecycleAdapter_horizontal(mContext, viewHolder.filteredmlist_vad);

        viewHolder.recyclerView_VAD.setLayoutManager(viewHolder.linearLayoutManager_VAD);
        viewHolder.recyclerView_VAD.setAdapter(viewHolder.mAdapter_vad);


        if (filteredmlist.get(position).ARM_03.equals("Y")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
        } else if (filteredmlist.get(position).ARM_03.equals("N")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
        }

        viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.filteredmlist_vad.size() == 0) {
                    Toast.makeText(mContext, mContext.getString(R.string.common_no_alarm_toast), Toast.LENGTH_LONG).show();
                    return;
                }

                if (filteredmlist.get(position).ARM_03.equals("Y")) {
                    filteredmlist.get(position).setARM_03("N");
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
                    Toast.makeText(mContext, "[" + filteredmlist.get(position).VAC_02 + "]- " +mContext.getString(R.string.common_alarm_off) , Toast.LENGTH_SHORT).show();
                } else {
                    filteredmlist.get(position).setARM_03("Y");
                    Toast.makeText(mContext, mContext.getString(R.string.vac_text6)+" " + viewHolder.filteredmlist_vad.get(0).VAD_04+"\n"+
                            stringTodateTimeFormat(viewHolder.filteredmlist_vad.get(0).VAD_96)+" " + mContext.getString(R.string.vac_text7),Toast.LENGTH_LONG).show();
                    viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
                }


                VacVO vacvo = new VacVO();
                vacvo.setVAC_ID(filteredmlist.get(position).VAC_ID);
                vacvo.setVAC_01(filteredmlist.get(position).VAC_01);
                vacvo.setVAC_02(filteredmlist.get(position).VAC_02);
                vacvo.setVAC_03(filteredmlist.get(position).VAC_03);
                vacvo.setVAC_04(filteredmlist.get(position).VAC_04);
                vacvo.setARM_03(filteredmlist.get(position).ARM_03);

                requestVAC_CONTROL(vacvo, position);

            }
        });

    }

    public void requestVAD_SELECT(ViewHolder viewHolder, ArrayList<VacVO> filteredmlist, int position) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        //openLoadingBar();

        //String strToday = ClsDateTime.getNow("yyyyMMdd");


        Call<VADModel> call = Http.vad(HttpBaseService.TYPE.POST).VAD_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                filteredmlist.get(position).VAC_ID,
                filteredmlist.get(position).VAC_01,
                ""
        );


        call.enqueue(new Callback<VADModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<VADModel> call, Response<VADModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<VADModel> response = (Response<VADModel>) msg.obj;

                            viewHolder.filteredmlist_vad = response.body().Data;
                            if (viewHolder.filteredmlist_vad == null)
                                viewHolder.filteredmlist_vad = new ArrayList<>();

                            if (viewHolder.filteredmlist_vad.size() == 0) {
                                viewHolder.tv_vamNone.setVisibility(View.VISIBLE);
                                viewHolder.recyclerView_VAD.setVisibility(View.GONE);
                            } else {
                                viewHolder.tv_vamNone.setVisibility(View.GONE);
                                viewHolder.recyclerView_VAD.setVisibility(View.VISIBLE);
                            }
                            viewHolder.mAdapter_vad.updateData(viewHolder.filteredmlist_vad);
                            viewHolder.mAdapter_vad.notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<VADModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }



    @Override
    public int getItemCount() {
        return filteredmlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView tv_name;
        TextView tv_week;

        private VadRecycleAdapter_horizontal mAdapter_vad;
        private LinearLayoutManager linearLayoutManager_VAD;
        private ArrayList<VadVO> filteredmlist_vad;

        RecyclerView recyclerView_VAD;

        TextView tv_vamNone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_week = itemView.findViewById(R.id.tv_week);

            recyclerView_VAD = itemView.findViewById(R.id.recyclerView_VAD);

            tv_vamNone = itemView.findViewById(R.id.tv_vamNone);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    VacVO vacvo = new VacVO();
                    vacvo.setVAC_ID(filteredmlist.get(position).VAC_ID);
                    vacvo.setVAC_01(filteredmlist.get(position).VAC_01);
                    vacvo.setVAC_02(filteredmlist.get(position).VAC_02);
                    vacvo.setVAC_03(filteredmlist.get(position).VAC_03);
                    vacvo.setVAC_04(filteredmlist.get(position).VAC_04);
                    vacvo.setVAC_97(filteredmlist.get(position).VAC_97);
                    vacvo.setARM_03(filteredmlist.get(position).ARM_03);

                    Intent intent = new Intent(mContext, VacDetail.class);
                    intent.putExtra("VacVO", vacvo);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    public void updateData(ArrayList<VacVO> list) {
        mList = list;
    }


    public void requestVAC_CONTROL(VacVO vacVO, int position) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<VACModel> call = Http.vac(HttpBaseService.TYPE.POST).VAC_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE",
                vacVO.VAC_ID,
                vacVO.VAC_01,
                vacVO.VAC_02,
                vacVO.VAC_03,
                vacVO.VAC_04,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                vacVO.ARM_03
        );


        call.enqueue(new Callback<VACModel>() {
            @Override
            public void onResponse(Call<VACModel> call, Response<VACModel> response) {

            }

            @Override
            public void onFailure(Call<VACModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }

    public String stringTodateTimeFormat(String str) {
        String retStr = "";
        //yyyy.MM.dd
        retStr = str.substring(0, 4) + "." + str.substring(4, 6) + "." + str.substring(6, 8) +" " + str.substring(8,10) + " : " + str.substring(10,12);
        return retStr;
    }

}
