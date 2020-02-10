package com.linktag.linkapp.ui.pot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.PotVO;

import java.util.ArrayList;

public class PotAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private ArrayList<PotVO> mList;
    private LayoutInflater mInflater;

    private AlarmClickListener alarmClickListener;

    public interface AlarmClickListener{
        void onListAlarmClick(int position);
    }

    public PotAdapter(Context context, ArrayList<PotVO> list, AlarmClickListener alarmClickListener){ //, AlarmClickListener alarmClickListener
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
            convertView = mInflater.inflate(R.layout.listitem_find_pot, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvPotName = convertView.findViewById(R.id.tvPotName);
            viewHolder.tvPreWaterDay = convertView.findViewById(R.id.tvPreWaterDay);
            viewHolder.tvDday = convertView.findViewById(R.id.tvDday);
            viewHolder.tvAlarmDay = convertView.findViewById(R.id.tvAlarmDay);

            viewHolder.PotIcon = (ImageView) convertView.findViewById(R.id.PotIcon);
            viewHolder.AlarmIcon = (ImageView) convertView.findViewById(R.id.AlarmIcon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Text
        viewHolder.tvPotName.setText(mList.get(position).POT_02);
        viewHolder.tvPreWaterDay.setText(mList.get(position).POT_03_T);
        String DDAY = "";
        if(Integer.parseInt(mList.get(position).DDAY) > 0) {
            DDAY = "D-" + mList.get(position).DDAY;
        }
        else if(Integer.parseInt(mList.get(position).DDAY) == 0){
            DDAY = "D-Day";
        }
        else{
            DDAY = "D+" + (Integer.parseInt(mList.get(position).DDAY) * -1);
        }
        viewHolder.tvDday.setText(DDAY);
        viewHolder.tvAlarmDay.setText(mList.get(position).POT_96.substring(2, 4) + "." + mList.get(position).POT_96.substring(4, 6) + "." + mList.get(position).POT_96.substring(6, 8) + " " + mList.get(position).POT_96.substring(8, 10) + ":" + mList.get(position).POT_96.substring(10, 12));

        //Image
        viewHolder.PotIcon.setImageResource(R.drawable.ic_launcher);
        if(mList.get(position).ARM_03.equals("Y")){
            viewHolder.AlarmIcon.setImageResource(R.drawable.main_noti_selected);
        }
        else{ //N
            viewHolder.AlarmIcon.setImageResource(R.drawable.btn_noti_off_gray);
        }
        viewHolder.AlarmIcon.setTag(position);
        viewHolder.AlarmIcon.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(this.alarmClickListener != null) {
            this.alarmClickListener.onListAlarmClick((int) v.getTag());
        }
    }

    public void updateData(ArrayList<PotVO> list){ mList = list;}

    static class ViewHolder{
        TextView tvPotName;
        TextView tvPreWaterDay;
        TextView tvDday;
        TextView tvAlarmDay;

        ImageView PotIcon;
        ImageView AlarmIcon;

    }
}
