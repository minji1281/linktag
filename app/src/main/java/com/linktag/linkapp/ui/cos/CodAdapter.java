package com.linktag.linkapp.ui.cos;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.COD_VO;

import java.util.ArrayList;

public class CodAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private ArrayList<COD_VO> mList;
    private LayoutInflater mInflater;

    private AlarmClickListener alarmClickListener;

    public interface AlarmClickListener{
        void onListAlarmClick(int position);
    }

    public CodAdapter(Context context, ArrayList<COD_VO> list, AlarmClickListener alarmClickListener){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.alarmClickListener = alarmClickListener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_find_cod, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvCodName = convertView.findViewById(R.id.tvCodName);
            viewHolder.tvCosName = convertView.findViewById(R.id.tvCosName);
            viewHolder.tvAlarmDay = convertView.findViewById(R.id.tvAlarmDay);

            viewHolder.imgCodIcon = (ImageView) convertView.findViewById(R.id.imgCodIcon);
            viewHolder.imgAlarmIcon = (ImageView) convertView.findViewById(R.id.imgAlarmIcon);

            viewHolder.layoutCod = (LinearLayout) convertView.findViewById(R.id.layoutCod);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(mList.get(position).COD_07.equals("")){
            //Image
            viewHolder.imgCodIcon.setImageResource(R.drawable.ic_cod);
            if(mList.get(position).ARM_03.equals("Y")){
                viewHolder.imgAlarmIcon.setImageResource(R.drawable.main_noti_selected);
            }
            else{ //N
                viewHolder.imgAlarmIcon.setImageResource(R.drawable.btn_noti_off_gray);
            }

            viewHolder.imgAlarmIcon.setTag(position);
            viewHolder.imgAlarmIcon.setOnClickListener(this);
            viewHolder.layoutCod.setBackgroundResource(R.drawable.shape_base_border);
            viewHolder.tvCodName.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.List1));
            viewHolder.tvCosName.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.List2));
            viewHolder.tvAlarmDay.setTextColor(ContextCompat.getColor(mContext.getApplicationContext(), R.color.List3));
        }
        else{
            viewHolder.imgCodIcon.setImageDrawable(null);
            viewHolder.imgAlarmIcon.setImageDrawable(null);
            viewHolder.layoutCod.setBackgroundResource(R.drawable.shape_base_border_back_gray);
            viewHolder.tvCodName.setTextColor(Color.GRAY);
            viewHolder.tvCosName.setTextColor(Color.GRAY);
            viewHolder.tvAlarmDay.setTextColor(Color.GRAY);
        }

        //Text
        viewHolder.tvCodName.setText(mList.get(position).COD_02);
        viewHolder.tvCosName.setText(mList.get(position).COS_02);
        if(mList.get(position).COD_07.equals("")){
            viewHolder.tvAlarmDay.setText(mList.get(position).COD_96.substring(2, 4) + "." + mList.get(position).COD_96.substring(4, 6) + "." + mList.get(position).COD_96.substring(6, 8) + "\n" + mList.get(position).COD_96.substring(8, 10) + ":" + mList.get(position).COD_96.substring(10, 12));
        }
        else{
            viewHolder.tvAlarmDay.setText(mList.get(position).COD_07.substring(2, 4) + "." + mList.get(position).COD_07.substring(4, 6) + "." + mList.get(position).COD_07.substring(6, 8));
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.alarmClickListener != null) {
            this.alarmClickListener.onListAlarmClick((int) v.getTag());
        }
    }

    public void updateData(ArrayList<COD_VO> list){ mList = list;}

    static class ViewHolder{
        TextView tvCodName;
        TextView tvCosName;
        TextView tvAlarmDay;

        ImageView imgCodIcon;
        ImageView imgAlarmIcon;

        LinearLayout layoutCod;

    }
}
