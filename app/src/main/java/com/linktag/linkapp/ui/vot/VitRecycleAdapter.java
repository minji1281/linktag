package com.linktag.linkapp.ui.vot;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.VIT_VO;
import com.linktag.linkapp.value_object.VOT_VO;

import java.util.ArrayList;

public class VitRecycleAdapter extends RecyclerView.Adapter<VitRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VIT_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
    private CtdVO intentVO;
    private VOT_VO VOT;

    VitRecycleAdapter(Context context, ArrayList<VIT_VO> list, VOT_VO VOT_tmp, CtdVO intentVO_tmp) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
        VOT = VOT_tmp;
        intentVO = intentVO_tmp;
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
        double per = ((double) mList.get(position).VIT_04 / (double) mList.get(position).VIT_04_SUM) * 100.0;
        viewHolder.tvVitPer.setText(Math.round(per) + "%");
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VotDetail.class);
                    intent.putExtra("VOT", VOT);
                    intent.putExtra("intentVO", intentVO);

                    mContext.startActivity(intent);
                }
            });
        }
    }

    public void updateData(ArrayList<VIT_VO> list) {
        mList = list;
    }


}
