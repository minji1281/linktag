package com.linktag.linkapp.ui.air;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.AIR_VO;

import java.util.ArrayList;

public class AirAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private ArrayList<AIR_VO> mList;
    private LayoutInflater mInflater;

    private AlarmClickListener alarmClickListener;

    public interface AlarmClickListener{
        void onListAlarmClick(int position);
    }

    public AirAdapter(Context context, ArrayList<AIR_VO> list, AlarmClickListener alarmClickListener){
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
            convertView = mInflater.inflate(R.layout.listitem_find_air, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvAirName = convertView.findViewById(R.id.tvAirName);
            viewHolder.tvAirMemo = convertView.findViewById(R.id.tvAirMemo);
            viewHolder.tvAlarmDay = convertView.findViewById(R.id.tvAlarmDay);

            viewHolder.imgAirIcon = (ImageView) convertView.findViewById(R.id.imgAirIcon);
            viewHolder.imgAlarmIcon = (ImageView) convertView.findViewById(R.id.imgAlarmIcon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Text
        viewHolder.tvAirName.setText(mList.get(position).AIR_02);
        viewHolder.tvAirMemo.setText(mList.get(position).AIR_07);
        viewHolder.tvAlarmDay.setText(mList.get(position).AIR_96.substring(2, 4) + "." + mList.get(position).AIR_96.substring(4, 6) + "." + mList.get(position).AIR_96.substring(6, 8) + "\n" + mList.get(position).AIR_96.substring(8, 10) + ":" + mList.get(position).AIR_96.substring(10, 12));

        //Image
        viewHolder.imgAirIcon.setImageResource(R.drawable.ic_launcher);
//        if(mList.get(position).ARM_03.equals("Y")){
//            viewHolder.imgAlarmIcon.setImageResource(R.drawable.main_noti_selected);
//        }
//        else{ //N
//            viewHolder.imgAlarmIcon.setImageResource(R.drawable.btn_noti_off_gray);
//        }
        viewHolder.imgAlarmIcon.setImageResource(R.drawable.btn_noti_off_gray); //서버안올려서 테스트
        viewHolder.imgAlarmIcon.setTag(position);
        viewHolder.imgAlarmIcon.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.alarmClickListener != null) {
            this.alarmClickListener.onListAlarmClick((int) v.getTag());
        }
    }

    public void updateData(ArrayList<AIR_VO> list){ mList = list;}

    static class ViewHolder{
        TextView tvAirName;
        TextView tvAirMemo;
        TextView tvAlarmDay;

        ImageView imgAirIcon;
        ImageView imgAlarmIcon;

    }
}
