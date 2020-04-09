package com.linktag.linkapp.ui.vot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.VIT_VO;

import java.util.ArrayList;

public class VitRecycleAdapter extends RecyclerView.Adapter<VitRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VIT_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    VitRecycleAdapter(Context context, ArrayList<VIT_VO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
    }

    @NonNull
    @Override
    public VitRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_vit
                , parent, false);
        VitRecycleAdapter.ViewHolder viewHolder = new VitRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if(mList.get(position).VIT_RANK == 1){
            viewHolder.tvVitRank.setText(R.string.vot_list_first);
        }
        else{ //2
            viewHolder.tvVitRank.setText(R.string.vot_list_second);
        }
        viewHolder.tvVitName.setText(mList.get(position).VIT_03);
        viewHolder.tvVitPer.setText("0%");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVitRank;
        TextView tvVitName;
        TextView tvVitPer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVitRank = itemView.findViewById(R.id.tvVitRank);
            tvVitName = itemView.findViewById(R.id.tvVitName);
            tvVitPer = itemView.findViewById(R.id.tvVitPer);
        }
    }

    public void updateData(ArrayList<VIT_VO> list) {
        mList = list;
    }


}
