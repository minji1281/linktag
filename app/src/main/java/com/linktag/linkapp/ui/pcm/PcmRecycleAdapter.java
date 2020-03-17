package com.linktag.linkapp.ui.pcm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.ARMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm_service.AlarmHATT;
import com.linktag.linkapp.ui.alarm_service.Alarm_Receiver;
import com.linktag.linkapp.ui.jdm.JdmRecycleAdapter;
import com.linktag.linkapp.value_object.ArmVO;
import com.linktag.linkapp.value_object.JdmVO;
import com.linktag.linkapp.value_object.PcmVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PcmRecycleAdapter extends RecyclerView.Adapter<PcmRecycleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<PcmVO> mList;
    private ArrayList<PcmVO> filteredmlist;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

    Filter listFilter;

    PcmRecycleAdapter(Context context, ArrayList<PcmVO> list) {
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


    private class ListFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            String charString = constraint.toString();
            if(charString.isEmpty()){
                results.values = mList;
                results.count = mList.size();
            }else{
                ArrayList<PcmVO> itemList = new ArrayList<>();
                for(PcmVO item : mList){
                    if(item.PCM_02.toLowerCase().contains(constraint.toString().toLowerCase())){
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

            filteredmlist = (ArrayList<PcmVO>)results.values;

            if(results.count>0){
                notifyDataSetChanged();
            }
        }
    }

    @NonNull
    @Override
    public PcmRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_pcm_list, parent, false);
        PcmRecycleAdapter.ViewHolder viewHolder = new PcmRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.tv_name.setText(filteredmlist.get(position).PCM_02);
        viewHolder.tv_date.setText(filteredmlist.get(position).PCM_04.substring(0,4)+"."+filteredmlist.get(position).PCM_04.substring(4,6)+"."+filteredmlist.get(position).PCM_04.substring(6,8));
        viewHolder.tv_hwCount.setText("하드웨어 "+filteredmlist.get(position).PCD_HW_CNT + "건");
        viewHolder.tv_swCount.setText("소프트웨어 "+filteredmlist.get(position).PCD_SW_CNT + "건");

    }


    @Override
    public int getItemCount() {
        return filteredmlist.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView tv_name;
        TextView tv_date;

        TextView tv_hwCount;
        TextView tv_swCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_hwCount = itemView.findViewById(R.id.tv_hwCount);
            tv_swCount = itemView.findViewById(R.id.tv_swCount);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    PcmVO pcmvo = new PcmVO();
                    pcmvo.setPCM_ID(filteredmlist.get(position).PCM_ID);
                    pcmvo.setPCM_01(filteredmlist.get(position).PCM_01);
                    pcmvo.setPCM_02(filteredmlist.get(position).PCM_04);
                    pcmvo.setPCM_03(filteredmlist.get(position).PCM_03);
                    pcmvo.setPCM_04(filteredmlist.get(position).PCM_04);
                    pcmvo.setPCM_96(filteredmlist.get(position).PCM_96);
                    pcmvo.setPCM_97(filteredmlist.get(position).PCM_97);
                    pcmvo.setARM_03(filteredmlist.get(position).ARM_03);
                    pcmvo.setARM_04(filteredmlist.get(position).ARM_04);

                    Intent intent = new Intent(mContext, DetailPcm.class);
                    intent.putExtra("PcmVO", pcmvo);
                    mContext.startActivity(intent);
                }
            });

        }
    }

    public void updateData(ArrayList<PcmVO> list) {
        mList = list;
    }

}
