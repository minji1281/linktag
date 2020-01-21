package com.linktag.linkapp.ui.work_place_search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linktag.linkapp.R;

import java.util.ArrayList;

public class FindWorkPlaceAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<WorkPlaceVO> mList;
    private LayoutInflater mInflater;

    public FindWorkPlaceAdapter(Context context, ArrayList<WorkPlaceVO> list) {
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
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_find_work_place, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvWorkPlaceName = convertView.findViewById(R.id.tvWorkPlaceName);
            viewHolder.tvManager = convertView.findViewById(R.id.tvManager);
            viewHolder.tvAddress = convertView.findViewById(R.id.tvAddress);
            viewHolder.tvPhone = convertView.findViewById(R.id.tvPhone);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvWorkPlaceName.setText(mList.get(position).WorkPlaceName);
        viewHolder.tvManager.setText(mList.get(position).Manager);
        viewHolder.tvAddress.setText(mList.get(position).Address);
        viewHolder.tvPhone.setText(mList.get(position).Phone);

        return convertView;
    }

    public void updateData(ArrayList<WorkPlaceVO> list) {
        mList = list;
    }

    static class ViewHolder {
        TextView tvWorkPlaceName;
        TextView tvManager;
        TextView tvAddress;
        TextView tvPhone;
    }
}
