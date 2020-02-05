package com.linktag.linkapp.ui.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CtdVO;

import java.util.ArrayList;

public class ServiceAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private ArrayList<CtdVO> mList;
    private LayoutInflater mInflater;

    private ServiceBtnClickListener serviceBtnClickListener;

    public interface ServiceBtnClickListener{
        void onGridBtnClick(int position);
    }

    public ServiceAdapter(Context context, ArrayList<CtdVO> list, ServiceBtnClickListener serviceBtnClickListener){
        this.mContext = context;
        this.mList = list;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.serviceBtnClickListener = serviceBtnClickListener;
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
            viewHolder.btnService = convertView.findViewById(R.id.btnService);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.btnService.setText(mList.get(position).CTD_02_NM);
        viewHolder.btnService.setTag(position);
        viewHolder.btnService.setOnClickListener(this);

        return convertView;
    }

    public void updateData(ArrayList<CtdVO> list){ mList = list;}

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnService){
            this.serviceBtnClickListener.onGridBtnClick((int) v.getTag());
        }
    }

    static class ViewHolder{
        Button btnService;
    }
}
