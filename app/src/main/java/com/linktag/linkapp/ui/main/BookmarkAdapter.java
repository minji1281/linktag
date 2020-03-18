package com.linktag.linkapp.ui.main;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.linktag.base.util.ClsBitmap;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

public class BookmarkAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<CtdVO> mList;
    private LayoutInflater mInflater;

    private boolean deleteChk = false;

    public BookmarkAdapter(Context context, ArrayList<CtdVO> list){
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
            convertView = mInflater.inflate(R.layout.griditem_service, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.ivService = convertView.findViewById(R.id.ivService);

            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(mContext.getResources().getColor(R.color.transparent_color));

            viewHolder.ivService.setBackground(drawable);
            if (Build.VERSION.SDK_INT >= 21) {
                viewHolder.ivService.setClipToOutline(true);
            }
            viewHolder.tvService = convertView.findViewById(R.id.tvService);
            viewHolder.layDelete = convertView.findViewById(R.id.layDelete);
            viewHolder.layType = convertView.findViewById(R.id.layType);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(deleteChk)
            viewHolder.layDelete.setVisibility(View.VISIBLE);
        else
            viewHolder.layDelete.setVisibility(View.GONE);

        int resource = convertView.getResources().getIdentifier("service_" + mList.get(position).SVC_01.toLowerCase() , "drawable", mContext.getPackageName());

        if(mList.get(position).CTM_19.equals("S")){
            viewHolder.layType.setVisibility(View.VISIBLE);
            viewHolder.tvService.setText(mList.get(position).CTM_17);
            ClsBitmap.setSharedPhoto(mContext, viewHolder.ivService, mList.get(position).CTD_01, mList.get(position).CTD_08, "", resource);
        }
        else {
            viewHolder.layType.setVisibility(View.GONE);
            viewHolder.tvService.setText(mList.get(position).CTD_02_NM);
            viewHolder.ivService.setImageResource(resource);

        }

        return convertView;
    }

    public void updateData(ArrayList<CtdVO> list){ mList = list;}

    public void setDelete(boolean chk){
        deleteChk = chk;
    }

    static class ViewHolder{
        ImageView ivService;
        TextView tvService;
        LinearLayout layDelete;
        LinearLayout layType;
    }
}
