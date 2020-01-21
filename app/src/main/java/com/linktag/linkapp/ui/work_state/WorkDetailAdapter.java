package com.linktag.linkapp.ui.work_state;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.LED_VO;
import com.linktag.base.util.ClsDateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WorkDetailAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<LED_VO> mList;
    private LayoutInflater mInflater;
    private SimpleDateFormat sdfParse = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat sdfFormat;

    public WorkDetailAdapter(Context context, ArrayList<LED_VO> list){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.sdfFormat = new SimpleDateFormat(mContext.getString(R.string.commute_10));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View convertView = mInflater.inflate(R.layout.listitem_work_record_detail, parent, false);

        RecyclerView.ViewHolder holder = new ViewHolder(convertView);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if(position >= mList.size())
            return;

        final ViewHolder finalHolder = (ViewHolder) holder;

        String strDate = mList.get(position).LED_23;

        try {
            strDate = sdfFormat.format(sdfParse.parse(strDate));
        } catch (Exception e){
            e.printStackTrace();
        }
        finalHolder.tvDate.setText(strDate + " " + mList.get(position).LED_23_NM);

        /*
        finalHolder.tvUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
*/

        finalHolder.tvStart.setText(ClsDateTime.ConvertStringBuffer(mList.get(position).LED_07, ":", 2));
        finalHolder.tvEnd.setText(ClsDateTime.ConvertStringBuffer(mList.get(position).LED_08, ":", 2));
        finalHolder.tvWorkTime.setText(mList.get(position).LED_11.toString());
        finalHolder.tvWorkTime2.setText(mList.get(position).LED_12.toString());
        finalHolder.tvWorkTime3.setText(mList.get(position).LED_13.toString());
        finalHolder.tvState.setText(mList.get(position).LED_06.equals("3") ? "출장" : mList.get(position).LED_06.equals("2") ? "교육 참석" : "");

        finalHolder.tvComment.setText(mList.get(position).LED_97);
        if(mList.get(position).LED_97 == null || mList.get(position).LED_97 == "")
        {
            finalHolder.llComment.setVisibility(View.GONE);
        }
        else
        {
            finalHolder.llComment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount(){
        return mList.size();
    }

    public void updateData(ArrayList<LED_VO> list) { mList = list; }


    public class ViewHolder extends RecyclerView.ViewHolder {
        //TextView tvUpdate;
        TextView tvDate;
        TextView tvStart;
        TextView tvEnd;
        TextView tvWorkTime;
        TextView tvWorkTime2;
        TextView tvWorkTime3;
        TextView tvState;
        TextView tvComment;
        LinearLayout llComment;

        public ViewHolder(View itemView){
            super(itemView);

            //tvUpdate = itemView.findViewById(R.id.tvUpdate);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);
            tvWorkTime = itemView.findViewById(R.id.tvWorkTime);
            tvWorkTime2 = itemView.findViewById(R.id.tvWorkTime2);
            tvWorkTime3 = itemView.findViewById(R.id.tvWorkTime3);
            tvState = itemView.findViewById(R.id.tvState);
            tvComment = itemView.findViewById(R.id.tvComment);
            llComment = itemView.findViewById(R.id.llComment);

            itemView.setOnClickListener(view -> {


            });

        }
    }

}
