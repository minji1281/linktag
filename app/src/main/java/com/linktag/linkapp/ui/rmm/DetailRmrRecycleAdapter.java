package com.linktag.linkapp.ui.rmm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.RMR_VO;

import java.util.ArrayList;

public class DetailRmrRecycleAdapter extends RecyclerView.Adapter<DetailRmrRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<RMR_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    private String RMD_97;

    DetailRmrRecycleAdapter(Context context, ArrayList<RMR_VO> list, String RMD_97_tmp) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();

        RMD_97 = RMD_97_tmp;
    }

    @NonNull
    @Override
    public DetailRmrRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_detail_rmr
                , parent, false);
        DetailRmrRecycleAdapter.ViewHolder viewHolder = new DetailRmrRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.tvTime.setText(mList.get(position).RMR_04.substring(0,2) + ":" + mList.get(position).RMR_04.substring(2));
        viewHolder.tvName.setText(mList.get(position).RMR_05_NM);

        if(mList.get(position).RMR_05.equals("")){ //예약자가 없을때
            viewHolder.imgCheck.setImageResource(R.drawable.ic_check_off);
            viewHolder.imgCheck.setTag(R.drawable.ic_check_off);
            viewHolder.imgCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setCheckChange(viewHolder.imgCheck, viewHolder.tvTime, viewHolder.tvName, position);
                }
            });
            viewHolder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
            viewHolder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
        }
        else{ //예약자가 있을때
            viewHolder.tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.light_blue_500));
            viewHolder.tvName.setTextColor(ContextCompat.getColor(mContext, R.color.light_blue_500));

            if(mList.get(position).RMR_05.equals(mUser.Value.OCM_01)){ //예약자가 자신일때
                viewHolder.imgCheck.setImageResource(R.drawable.ic_check_on);
                viewHolder.imgCheck.setTag(R.drawable.ic_check_on);
                viewHolder.imgCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setCheckChange(viewHolder.imgCheck, viewHolder.tvTime, viewHolder.tvName, position);
                    }
                });
            }
            else{ //예약자가 다른사람일때
                viewHolder.imgCheck.setImageResource(0);
            }
        }

        if(RMD_97.equals(mUser.Value.OCM_01)){ //마스터는 예약할 수 없다.
            viewHolder.imgCheck.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCheck;
        TextView tvTime;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCheck = itemView.findViewById(R.id.imgCheck);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }

    public void updateData(ArrayList<RMR_VO> list) {
        mList = list;
    }

    private void setCheckChange(ImageView img, TextView tv1, TextView tv2, int position){
        if(img.getTag().equals(R.drawable.ic_check_on)) {
            img.setImageResource(R.drawable.ic_check_off);
            img.setTag(R.drawable.ic_check_off);
            mList.get(position).RMR_05 = "";
            tv1.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
            tv2.setTextColor(ContextCompat.getColor(mContext, R.color.listitem_text1));
        }
        else{
            img.setImageResource(R.drawable.ic_check_on);
            img.setTag(R.drawable.ic_check_on);
            mList.get(position).RMR_05 = mUser.Value.OCM_01;
            tv1.setTextColor(ContextCompat.getColor(mContext, R.color.light_blue_500));
            tv2.setTextColor(ContextCompat.getColor(mContext, R.color.light_blue_500));
        }

        ReserveList tmp = new ReserveList(mList.get(position).RMR_02, mList.get(position).RMR_04); //예약
        if(RmdDetail.RMR_04_list.contains(tmp)){
            RmdDetail.RMR_04_list.remove(tmp);
        }
        else{
            RmdDetail.RMR_04_list.add(tmp);
        }
    }

}
