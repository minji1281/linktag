package com.linktag.linkapp.ui.srsv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.SIF_VO;

import java.util.ArrayList;

public class SifRecycleAdapter_horizontal extends RecyclerView.Adapter<SifRecycleAdapter_horizontal.ViewHolder> {

    private Context mContext;
    private ArrayList<SIF_VO> mList;
    private LayoutInflater mInflater;
    private View view;

    public SifRecycleAdapter_horizontal(Context context, ArrayList<SIF_VO> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public SifRecycleAdapter_horizontal.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_sif_list_horizontal, parent, false);
        SifRecycleAdapter_horizontal.ViewHolder viewHolder = new SifRecycleAdapter_horizontal.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        //ivShop 이미지 보류

        viewHolder.tvShop.setText(mList.get(position).SIF_03);

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivShop;
        TextView tvShop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivShop = itemView.findViewById(R.id.ivShop);
            tvShop = itemView.findViewById(R.id.tvShop);

        }
    }

    public void updateData(ArrayList<SIF_VO> list) {
        mList = list;
    }

}
