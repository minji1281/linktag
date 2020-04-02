package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.util.ClsBitmap;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

public class ChooseScanAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<CtdVO> mList;
    private LayoutInflater mInflater;

    public ChooseScanAdapter(Context context, ArrayList<CtdVO> list){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return mList.size(); }

    @Override
    public Object getItem(int position) { return mList.get(position); }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.griditem_shared, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.ivShared = convertView.findViewById(R.id.ivShared);

            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(mContext.getResources().getColor(R.color.transparent_color));

            viewHolder.ivShared.setBackground(drawable);
            if (Build.VERSION.SDK_INT >= 21) {
                viewHolder.ivShared.setClipToOutline(true);
            }

            viewHolder.tvSharedNM = convertView.findViewById(R.id.tvSharedNM);
            viewHolder.tvServiceNM = convertView.findViewById(R.id.tvServiceNM);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int resource = convertView.getResources().getIdentifier("service_" + mList.get(position).SVC_01.toLowerCase() , "drawable", mContext.getPackageName());

        if(mList.get(position).CTD_09.equals("S")){
            viewHolder.tvServiceNM.setVisibility(View.VISIBLE);
            viewHolder.tvSharedNM.setText(mList.get(position).CTD_10);
            viewHolder.tvServiceNM.setText(mList.get(position).CTD_02_NM);
            ClsBitmap.setSharedPhoto(mContext, viewHolder.ivShared, mList.get(position).CTD_01, mList.get(position).CTD_08, "", resource);
        } else {
            viewHolder.tvServiceNM.setVisibility(View.GONE);
            viewHolder.tvSharedNM.setText(mList.get(position).CTD_02_NM);
            viewHolder.ivShared.setImageResource(resource);
        }

        return convertView;
    }

    public void updateData(ArrayList<CtdVO> list){ mList = list;}

    static class ViewHolder{
        ImageView ivShared;
        TextView tvSharedNM;
        TextView tvServiceNM;
    }
}
