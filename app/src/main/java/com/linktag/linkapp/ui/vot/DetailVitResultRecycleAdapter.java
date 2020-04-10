package com.linktag.linkapp.ui.vot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.VIT_VO;

import java.util.ArrayList;

public class DetailVitResultRecycleAdapter extends RecyclerView.Adapter<DetailVitResultRecycleAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private ArrayList<VIT_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
    private int VIT_04_SUM;
    private String VOT_06;

    private VicClickListener vicClickListener;

    public interface VicClickListener{
        void onListVicClick(int position);
    }

    DetailVitResultRecycleAdapter(Context context, ArrayList<VIT_VO> list, int VIT_04_SUM_tmp, String VOT_06_tmp, VicClickListener vicClickListener) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
        VIT_04_SUM = VIT_04_SUM_tmp;
        VOT_06 = VOT_06_tmp;

        this.vicClickListener = vicClickListener;
    }

    @NonNull
    @Override
    public DetailVitResultRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_detail_vit_result
                , parent, false);
        DetailVitResultRecycleAdapter.ViewHolder viewHolder = new DetailVitResultRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.tvVitName.setText(mList.get(position).VIT_03);
        viewHolder.tvVitCount.setText(String.valueOf(mList.get(position).VIT_04));
        if(VOT_06.equals("N")){ //공개투표
            viewHolder.tvVitCount.setTag(position);
            viewHolder.tvVitCount.setOnClickListener(this);
            viewHolder.imgVitCount.setTag(position);
            viewHolder.imgVitCount.setOnClickListener(this);
        }

        viewHolder.pbVitRate.setMax(VIT_04_SUM);
        viewHolder.pbVitRate.setProgress(mList.get(position).VIT_04);
        if(VIT_04_SUM > 0 && mList.get(position).VIT_RANK == 1){
            viewHolder.pbVitRate.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_full_listitem));
        }
        else{
            viewHolder.pbVitRate.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progressbar_listitem));
        }

    }

    @Override
    public void onClick(View v) {
        if(this.vicClickListener != null) {
            this.vicClickListener.onListVicClick((int) v.getTag());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVitName;
        TextView tvVitCount;
        ImageView imgVitCount;
        ProgressBar pbVitRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVitName = itemView.findViewById(R.id.tvVitName);
            tvVitCount = itemView.findViewById(R.id.tvVitCount);
            imgVitCount = itemView.findViewById(R.id.imgVitCount);
            pbVitRate = itemView.findViewById(R.id.pbVitRate);
        }
    }

    public void updateData(ArrayList<VIT_VO> list) {
        mList = list;
    }

}
