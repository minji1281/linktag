package com.linktag.linkapp.ui.bus;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.linkapp.API.GPSInfo;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.RutcVO;

import java.util.ArrayList;

public class BusSelectAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<RutcVO> mList;
    private LayoutInflater mInflater;

    public BusSelectAdapter(Context context, ArrayList<RutcVO> list){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        ViewHolder vh;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_find_bus, parent, false);

            vh = new ViewHolder();
            vh.tvStoreName = convertView.findViewById(R.id.tvStoreName);
            vh.tvManager = convertView.findViewById(R.id.tvManager);
            vh.tvAddress = convertView.findViewById(R.id.tvAddress);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.tvStoreName.setText(mList.get(position).RUTC_03);
        vh.tvManager.setText(mList.get(position).RUTC_01);
        vh.tvAddress.setText("기점: "+mList.get(position).RUTC_04 +" ~ 종점: "+ mList.get(position).RUTC_05);

        return convertView;
    }

    public void updateData(ArrayList<RutcVO> list){ mList = list;}

    static class ViewHolder{
        TextView tvStoreName;
        TextView tvManager;
        TextView tvAddress;

    }
}
