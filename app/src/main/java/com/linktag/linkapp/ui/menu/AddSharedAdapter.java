package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.SvcVO;

import java.util.ArrayList;

public class AddSharedAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<SvcVO> mList;
    private LayoutInflater mInflater;

    public AddSharedAdapter(Context context, ArrayList<SvcVO> list){
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
        String imageUrl;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.griditem_service, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.ivService = convertView.findViewById(R.id.ivService);
            if (Build.VERSION.SDK_INT >= 21) {
                viewHolder.ivService.setClipToOutline(true);
            }
            viewHolder.tvService = convertView.findViewById(R.id.tvService);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        imageUrl = "http://app.linktag.io/files/admin/svc/" + mList.get(position).SVC_02 + "/" + mList.get(position).SVC_16;

        Glide.with(mContext).load(imageUrl)
                .placeholder(R.drawable.main_profile_no_image)
                .error(R.drawable.main_profile_no_image)
                .into(viewHolder.ivService);

        viewHolder.tvService.setText(mList.get(position).SVC_03);

        return convertView;
    }

    public void updateData(ArrayList<SvcVO> list){ mList = list;}

    static class ViewHolder{
        ImageView ivService;
        TextView tvService;
    }
}
