package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

public class SharedAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private ArrayList<CtdVO> mList;
    private LayoutInflater mInflater;

    private SharedBtnClickListener sharedBtnClickListener;

    public interface SharedBtnClickListener{
        void onGridBtnClick(int position);
    }

    public SharedAdapter(Context context, ArrayList<CtdVO> list, SharedBtnClickListener sharedBtnClickListener){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.sharedBtnClickListener = sharedBtnClickListener;
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
            viewHolder.btnShared = convertView.findViewById(R.id.btnShared);
            viewHolder.tvShared = convertView.findViewById(R.id.tvShared);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.btnShared.setText(mList.get(position).CTD_02_NM);
        viewHolder.btnShared.setTag(position);
        viewHolder.btnShared.setOnClickListener(this);
        viewHolder.tvShared.setText(mList.get(position).CTM_17);
        viewHolder.tvShared.setTag(position);
        viewHolder.tvShared.setOnClickListener(this);

        return convertView;
    }

    public void updateData(ArrayList<CtdVO> list){ mList = list;}

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnShared || v.getId() == R.id.tvShared){
            this.sharedBtnClickListener.onGridBtnClick((int) v.getTag());
        }
    }

    static class ViewHolder{
        Button btnShared;
        TextView tvShared;
    }
}
