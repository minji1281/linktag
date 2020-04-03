package com.linktag.linkapp.ui.rmm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.RMR_VO;

import java.util.ArrayList;
import java.util.Calendar;

public class DetailRmrRecycleAdapter extends RecyclerView.Adapter<DetailRmrRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<RMR_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
//    private RmrRecycleAdapter mAdapter;

    DetailRmrRecycleAdapter(Context context, ArrayList<RMR_VO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
    }

//    public void setmAdapter(RmrRecycleAdapter mAdapter) {
//        this.mAdapter = mAdapter;
//    }

    @NonNull
    @Override
    public DetailRmrRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_my_rmr
                , parent, false);
        DetailRmrRecycleAdapter.ViewHolder viewHolder = new DetailRmrRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        clearCalTime(viewHolder.today);
        clearCalTime(viewHolder.RMR_03);
        viewHolder.RMR_03.set(Calendar.YEAR, Integer.valueOf(mList.get(position).RMR_03.substring(0,4)));
        viewHolder.RMR_03.set(Calendar.DAY_OF_MONTH, Integer.valueOf(mList.get(position).RMR_03.substring(4,6)));
        viewHolder.RMR_03.set(Calendar.DATE, Integer.valueOf(mList.get(position).RMR_03.substring(6)));

        if(viewHolder.today.equals(viewHolder.RMR_03)){
            viewHolder.btnToday.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.btnToday.setVisibility(View.GONE);
        }

        viewHolder.tvDay.setText(mList.get(position).RMR_03.substring(0,4) + "." + mList.get(position).RMR_03.substring(4,6) + "." + mList.get(position).RMR_03.substring(6));
        viewHolder.tvTime.setText(mList.get(position).RMR_04ST.substring(0,2) + ":" + mList.get(position).RMR_04ST.substring(2) + "~" + mList.get(position).RMR_04ED.substring(0,2) + ":" + mList.get(position).RMR_04ED.substring(2));
        viewHolder.tvRmdName.setText(mList.get(position).RMR_02_NM);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        TextView tvTime;
        Button btnToday;
        TextView tvRmdName;
        Calendar today = Calendar.getInstance();
        Calendar RMR_03 = Calendar.getInstance();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDay = itemView.findViewById(R.id.tvDay);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnToday = itemView.findViewById(R.id.btnToday);
            tvRmdName = itemView.findViewById(R.id.tvRmdName);
        }
    }

    public void updateData(ArrayList<RMR_VO> list) {
        mList = list;
    }

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

}
