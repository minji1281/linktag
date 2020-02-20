package com.linktag.linkapp.ui.car;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CAD_VO;

import java.text.NumberFormat;
import java.util.ArrayList;

public class CadAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CAD_VO> mList;
    private LayoutInflater mInflater;

    public CadAdapter(Context context, ArrayList<CAD_VO> list){
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

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.listitem_find_cad, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tvCadName = convertView.findViewById(R.id.tvCadName);
            viewHolder.tvCadKm = convertView.findViewById(R.id.tvCadKm);
            viewHolder.tvCadMoney = convertView.findViewById(R.id.tvCadMoney);
            viewHolder.tvCadDay = convertView.findViewById(R.id.tvCadDay);

            viewHolder.imgCadIcon = (ImageView) convertView.findViewById(R.id.imgCadIcon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Image
        viewHolder.imgCadIcon.setImageResource(R.drawable.ic_cad);

        //Text
        String CAD_04_T = "교체";
        if(mList.get(position).CAD_04.equals("2")){
            CAD_04_T = "점검";
        }
        viewHolder.tvCadName.setText("[" + CAD_04_T + "]" + mList.get(position).CAD_05);
        viewHolder.tvCadKm.setText(NumberFormat.getInstance().format(mList.get(position).CAD_08) + "km");
        viewHolder.tvCadMoney.setText(NumberFormat.getInstance().format(mList.get(position).CAD_07) + "원");
        viewHolder.tvCadDay.setText(mList.get(position).CAD_03.substring(2, 4) + "." + mList.get(position).CAD_03.substring(4, 6) + "." + mList.get(position).CAD_03.substring(6, 8));

        return convertView;
    }

    public void updateData(ArrayList<CAD_VO> list){ mList = list;}

    static class ViewHolder{
        TextView tvCadName;
        TextView tvCadKm;
        TextView tvCadMoney;
        TextView tvCadDay;

        ImageView imgCadIcon;

    }
}
