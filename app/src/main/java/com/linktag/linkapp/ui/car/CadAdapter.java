package com.linktag.linkapp.ui.car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CAD_VO;

import java.text.NumberFormat;
import java.util.ArrayList;

public class CadAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CAD_VO> mList;
    private LayoutInflater mInflater;
    private InterfaceUser mUser;

    public CadAdapter(Context context, ArrayList<CAD_VO> list){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUser = InterfaceUser.getInstance();
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
            viewHolder.imgGubunIcon = (ImageView) convertView.findViewById(R.id.imgGubunIcon);
            viewHolder.tvDay = convertView.findViewById(R.id.tvDay);
            viewHolder.tvName = convertView.findViewById(R.id.tvName);
            viewHolder.tvMoney = convertView.findViewById(R.id.tvMoney);
            viewHolder.tvKm =  convertView.findViewById(R.id.tvKm);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Image
        if(mList.get(position).CAD_05.equals("1")){ //1:교체
            viewHolder.imgGubunIcon.setImageResource(R.drawable.ic_cad_change);
        }
        else{ //1:점검
            viewHolder.imgGubunIcon.setImageResource(R.drawable.ic_cad_check);
        }

        //Text
        viewHolder.tvDay.setText("정비일자 " + mList.get(position).CAD_03.substring(0, 4) + "." + mList.get(position).CAD_03.substring(4, 6) + "." + mList.get(position).CAD_03.substring(6, 8));
        viewHolder.tvName.setText(mList.get(position).CAD_04);
        viewHolder.tvMoney.setText(NumberFormat.getInstance().format(mList.get(position).CAD_07) + "원");
        viewHolder.tvKm.setText(NumberFormat.getInstance().format(mList.get(position).CAD_08) + "km");

        return convertView;
    }

    public void updateData(ArrayList<CAD_VO> list){ mList = list;}

    static class ViewHolder{
        ImageView imgGubunIcon;

        TextView tvDay;
        TextView tvName;
        TextView tvMoney;
        TextView tvKm;

    }
}
