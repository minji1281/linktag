package com.linktag.linkapp.ui.icm;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.ICR_VO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class IcrRecycleAdapter extends RecyclerView.Adapter<IcrRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ICR_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    private ArrayList<String> mList_ICI;
    private ArrayList<String> mList_ICI_detail;

    IcrRecycleAdapter(Context context, ArrayList<ICR_VO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();

        mList_ICI = new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.ici)));
    }

    @NonNull
    @Override
    public IcrRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_icr
                , parent, false);
        IcrRecycleAdapter.ViewHolder viewHolder = new IcrRecycleAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String StartMM = mList.get(position).ICR_04.substring(0,2);
        String StartDD = mList.get(position).ICR_04.substring(2);
        String StartAP = "AM";
        if(Integer.valueOf(StartMM) >= 12){
            if(Integer.valueOf(StartMM) >= 13){
                int tmp = Integer.valueOf(StartMM) - 12;
                StartMM = tmp<10 ? "0" + String.valueOf(tmp) : String.valueOf(tmp);
            }
            StartAP = "PM";
        }

        viewHolder.tvStartTime.setText(StartMM + ":" + StartDD + " " + StartAP);

        if(mList.get(position).ICR_05.equals("")){
            viewHolder.tvEndTime.setText("");
        }
        else{
            String EndMM = mList.get(position).ICR_05.substring(0,2);
            String EndDD = mList.get(position).ICR_05.substring(2);
            if(Integer.valueOf(EndMM) >= 13){
                int tmp2 = Integer.valueOf(EndMM) - 12;
                EndMM = tmp2<10 ? "0" + String.valueOf(tmp2) : String.valueOf(tmp2);
            }

            viewHolder.tvEndTime.setText("~" + EndMM + ":" + EndDD);
        }

        String ICIName = mList_ICI.get(Integer.valueOf(mList.get(position).ICR_06));
        if(!mList.get(position).ICR_07.equals("")){
            if(mList.get(position).ICR_06.equals("0")){ //수유
                mList_ICI_detail = new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.ici_0)));
                ICIName += " (" + mList_ICI_detail.get(Integer.valueOf(mList.get(position).ICR_07)) + ")";
            }
            else if(mList.get(position).ICR_06.equals("1")){ //기저귀
                mList_ICI_detail = new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.ici_1)));
                ICIName += " (" + mList_ICI_detail.get(Integer.valueOf(mList.get(position).ICR_07)) + ")";
            }
            else if(mList.get(position).ICR_06.equals("2")){ //수면
                mList_ICI_detail = new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.ici_2)));
                ICIName += " (" + mList_ICI_detail.get(Integer.valueOf(mList.get(position).ICR_07)) + ")";
            }
        }
        String tmp = "ici_" + mList.get(position).ICR_06;
        viewHolder.imgIcon.setBackgroundResource(mContext.getResources().getIdentifier(tmp, "drawable", mContext.getPackageName()));
        viewHolder.tvIci.setText(ICIName);

        if(mList.get(position).MM_CNT > 0){
            viewHolder.tvRunTime.setText(String.valueOf(mList.get(position).MM_CNT) + "분");
        }
        else{
            viewHolder.tvRunTime.setText("");
        }
        viewHolder.tvMemo.setText(mList.get(position).ICR_08);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartTime;
        TextView tvEndTime;
        ImageView imgIcon;
        TextView tvIci;
        TextView tvRunTime;
        TextView tvMemo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvIci = itemView.findViewById(R.id.tvIci);
            tvRunTime = itemView.findViewById(R.id.tvRunTime);
            tvMemo = itemView.findViewById(R.id.tvMemo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    ICR_VO ICR = mList.get(position);
                    Intent intent = new Intent(mContext, IcrDetail.class);
                    intent.putExtra("ICR", ICR);

                    mContext.startActivity(intent);
                }
            });
        }
    }

    public void updateData(ArrayList<ICR_VO> list) {
        mList = list;
    }

    public void clearCalTime(Calendar c){
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

}
