package com.linktag.linkapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.ui.jdm.JdmDetail;
import com.linktag.linkapp.value_object.JdmVO;

import java.util.ArrayList;

public class JdmRecycleAdapter extends RecyclerView.Adapter<JdmRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<JdmVO> mList;
    private LayoutInflater mInflater;
    private View view;

    JdmRecycleAdapter(Context context, ArrayList<JdmVO> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public JdmRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_jdm_list, parent, false);
        JdmRecycleAdapter.ViewHolder viewHolder = new JdmRecycleAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String Format = mList.get(position).JDM_96;
        String dateFormat = Format.substring(2, 4) + "." + Format.substring(4, 6) + "." + Format.substring(6, 8);
        String timeFormat = Format.substring(8, 10) + ":" + Format.substring(10);

        viewHolder.tv_name.setText(mList.get(position).JDM_02);
        viewHolder.tv_memo.setText(mList.get(position).JDM_03);
        viewHolder.tv_date.setText(dateFormat);
        viewHolder.tv_time.setText(timeFormat);

        if (mList.get(position).ARM_03.equals("Y")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_on);
        } else if (mList.get(position).ARM_03.equals("N")) {
            viewHolder.imageview.setImageResource(R.drawable.alarm_state_off);
        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView tv_name;
        TextView tv_memo;
        TextView tv_date;
        TextView tv_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            int position = getAdapterPosition();
            //String alarm_state = mList.get(position).ARM_03;
            imageview = itemView.findViewById(R.id.imageView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_memo = itemView.findViewById(R.id.tv_memo);
            tv_date = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    JdmVO jdmvo = new JdmVO();
                    jdmvo.setJDM_01(mList.get(position).JDM_01);
                    jdmvo.setJDM_02(tv_name.getText().toString());
                    jdmvo.setJDM_03(tv_memo.getText().toString());
                    jdmvo.setJDM_04(mList.get(position).JDM_04);
                    jdmvo.setJDM_96(mList.get(position).JDM_96);
                    jdmvo.setARM_03(mList.get(position).ARM_03);

                    Intent intent = new Intent(mContext, JdmDetail.class);
                    intent.putExtra("JdmVO", jdmvo);
                    mContext.startActivity(intent);
                }
            });

            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (alarm_state.equals("Y")) {
//                        imageview.setImageResource(R.drawable.alarm_state_on);
//                    } else {
//                        imageview.setImageResource(R.drawable.alarm_state_off);
//                    }
                }
            });
        }
    }

    public void updateData(ArrayList<JdmVO> list) {
        mList = list;
    }


}
