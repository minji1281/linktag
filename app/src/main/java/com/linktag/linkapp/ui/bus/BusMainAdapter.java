package com.linktag.linkapp.ui.bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.BhmVO;

import java.util.ArrayList;

public class BusMainAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<BhmVO> mList;
    private LayoutInflater mInflater;

    public BusMainAdapter(Context context, ArrayList<BhmVO> list){
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
            convertView = mInflater.inflate(R.layout.listitem_find_human, parent, false);

            vh = new ViewHolder();
            vh.tvStoreName = convertView.findViewById(R.id.tvStoreName);
            vh.tvManager = convertView.findViewById(R.id.tvManager);
            vh.tvAddress = convertView.findViewById(R.id.tvAddress);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.tvStoreName.setText(mList.get(position).BHM_06.substring(4,6)+"/"+mList.get(position).BHM_06.substring(6)+" "+mList.get(position).BHM_07.substring(0,2)+":"+mList.get(position).BHM_07.substring(2,4)
                               +" "+mList.get(position).BHM_09+" "+mList.get(position).BHM_08+"대 "+mList.get(position).RTSC_04);
        vh.tvManager.setText(mList.get(position).BHM_06);
        vh.tvAddress.setText(mList.get(position).BHM_08);

        return convertView;
    }

    public void updateData(ArrayList<BhmVO> list){ mList = list;}

    static class ViewHolder{
        TextView tvStoreName;
        TextView tvManager;
        TextView tvAddress;

    }
}
