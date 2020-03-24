package com.linktag.linkapp.ui.master_log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.LogVO;

import java.util.ArrayList;

public class LogAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<LogVO> mList;
    private LayoutInflater mInflater;
    private InterfaceUser mUser;
    private String func_text;

    public LogAdapter(Context context, ArrayList<LogVO> list, String text) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUser = InterfaceUser.getInstance();
        this.func_text = text;
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

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_find_log, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            viewHolder.tvDay = (TextView) convertView.findViewById(R.id.tvDay);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Text
        if (mList.get(position).LOG_03.equals("1")) { //신규등록
            viewHolder.tvContent.setText(R.string.log_new_text);
        } else if (mList.get(position).LOG_03.equals("2")) {
            if (func_text != null) {
                //기능
                viewHolder.tvContent.setText(func_text);
            }else{
                viewHolder.tvContent.setText(mList.get(position).LOG_04);
            }

        } else if (mList.get(position).LOG_03.equals("3")) { //알림발송
            viewHolder.tvContent.setText(R.string.log_alarm_text);
        }
        viewHolder.tvDay.setText(mList.get(position).LOG_05.substring(0, 4) + "." + mList.get(position).LOG_05.substring(4, 6) + "." + mList.get(position).LOG_05.substring(6, 8) + " " + mList.get(position).LOG_05.substring(8, 10) + ":" + mList.get(position).LOG_05.substring(10, 12));
        viewHolder.tvUserName.setText(mList.get(position).LOG_98);

        return convertView;
    }

    public void updateData(ArrayList<LogVO> list) {
        mList = list;
    }

    static class ViewHolder {
        TextView tvContent;
        TextView tvDay;
        TextView tvUserName;

    }
}
