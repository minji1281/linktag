package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

public class ServiceAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<CtdVO> mList;
    private LayoutInflater mInflater;

    private boolean deleteChk = false;

    public ServiceAdapter(Context context, ArrayList<CtdVO> list){
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
            viewHolder.layDelete = convertView.findViewById(R.id.layDelete);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        imageUrl = "http://app.linktag.io/files/admin/svc/" + mList.get(position).CTD_02 + "/" + mList.get(position).SVC_16;

        Glide.with(mContext).load(imageUrl)
                .placeholder(R.drawable.main_profile_no_image)
                .error(R.drawable.main_profile_no_image)
                .into(viewHolder.ivService);

        viewHolder.tvService.setText(mList.get(position).CTD_02_NM);

        if(deleteChk)
            viewHolder.layDelete.setVisibility(View.VISIBLE);
        else
            viewHolder.layDelete.setVisibility(View.GONE);

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
    }
}
