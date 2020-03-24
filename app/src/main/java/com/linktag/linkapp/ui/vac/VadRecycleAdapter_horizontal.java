package com.linktag.linkapp.ui.vac;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.VadVO;

import java.util.ArrayList;

public class VadRecycleAdapter_horizontal extends RecyclerView.Adapter<VadRecycleAdapter_horizontal.ViewHolder> {

    private Context mContext;
    private ArrayList<VadVO> mList;
    private LayoutInflater mInflater;
    private View view;
    private VadRecycleAdapter_horizontal mAdapter;

    VadRecycleAdapter_horizontal(Context context, ArrayList<VadVO> list) {
        mContext = context;
        mList = list;
    }

    public void setmAdapter(VadRecycleAdapter_horizontal mAdapter) {
        this.mAdapter = mAdapter;
    }

    @NonNull
    @Override
    public VadRecycleAdapter_horizontal.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_vad_list_horizontal
                , parent, false);
        VadRecycleAdapter_horizontal.ViewHolder viewHolder = new VadRecycleAdapter_horizontal.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        viewHolder.tv_name.setText(mList.get(position).VAD_05);
        viewHolder.tv_date.setText(mList.get(position).VAD_04);

    }



    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);

        }
    }

    public void updateData(ArrayList<VadVO> list) {
        mList = list;
    }


}
