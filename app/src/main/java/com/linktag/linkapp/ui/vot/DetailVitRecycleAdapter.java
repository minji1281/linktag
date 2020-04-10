package com.linktag.linkapp.ui.vot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.VIT_VO;

import java.util.ArrayList;

public class DetailVitRecycleAdapter extends RecyclerView.Adapter<DetailVitRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VIT_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;
    private String GUBUN;
    private String VOT_05;

    DetailVitRecycleAdapter(Context context, ArrayList<VIT_VO> list, String GUB, String VOT_05_tmp) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
        GUBUN = GUB;
        VOT_05 = VOT_05_tmp;
    }

    @NonNull
    @Override
    public DetailVitRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_detail_vit
                , parent, false);
        DetailVitRecycleAdapter.ViewHolder viewHolder = new DetailVitRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.tvVitName.setText(mList.get(position).VIT_03);

        if(GUBUN.equals("INSERT")){
            viewHolder.imgFunc.setBackgroundResource(R.drawable.ic_minus_round);
            viewHolder.imgFunc.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    VotDetail.mList_VIT.remove(position);

                    VotDetail.mAdapter_VIT.updateData(VotDetail.mList_VIT);
                    VotDetail.mAdapter_VIT.notifyDataSetChanged();
                }
            });
        }
        else{ //UPDATE
            if(VOT_05.equals("Y")){ //복수투표
                if(mList.get(position).boolCheck){
                    viewHolder.imgFunc.setBackgroundResource(R.drawable.ic_check_on);
                    viewHolder.imgFunc.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            viewHolder.imgFunc.setBackgroundResource(R.drawable.ic_check_off);
                            VotDetail.mList_VIT.get(position).boolCheck = false;
                            VotDetail.voteItemList.remove(mList.get(position).VIT_02);

                            VotDetail.mAdapter_VIT.updateData(VotDetail.mList_VIT);
                            VotDetail.mAdapter_VIT.notifyDataSetChanged();
                        }
                    });
                }
                else{
                    viewHolder.imgFunc.setBackgroundResource(R.drawable.ic_check_off);
                    viewHolder.imgFunc.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            viewHolder.imgFunc.setBackgroundResource(R.drawable.ic_check_on);
                            VotDetail.mList_VIT.get(position).boolCheck = true;
                            VotDetail.voteItemList.add(mList.get(position).VIT_02);

                            VotDetail.mAdapter_VIT.updateData(VotDetail.mList_VIT);
                            VotDetail.mAdapter_VIT.notifyDataSetChanged();
                        }
                    });
                }
            }
            else{ //단일투표
                if(mList.get(position).boolCheck){
                    viewHolder.imgFunc.setBackgroundResource(R.drawable.ic_check_on);
                    viewHolder.imgFunc.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            viewHolder.imgFunc.setBackgroundResource(R.drawable.ic_check_off);
                            VotDetail.voteInt = -1;
                            VotDetail.mList_VIT.get(position).boolCheck = false;
                            VotDetail.voteItemList.clear();

                            VotDetail.mAdapter_VIT.updateData(VotDetail.mList_VIT);
                            VotDetail.mAdapter_VIT.notifyDataSetChanged();
                        }
                    });
                }
                else{
                    viewHolder.imgFunc.setBackgroundResource(R.drawable.ic_check_off);
                    viewHolder.imgFunc.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            viewHolder.imgFunc.setBackgroundResource(R.drawable.ic_check_on);
                            if(VotDetail.voteItemList.size() > 0){
                                VotDetail.mList_VIT.get(VotDetail.voteInt).boolCheck = false;
                                VotDetail.voteItemList.clear();
                            }
                            VotDetail.mList_VIT.get(position).boolCheck = true;
                            VotDetail.voteItemList.add(mList.get(position).VIT_02);
                            VotDetail.voteInt = position;

                            VotDetail.mAdapter_VIT.updateData(VotDetail.mList_VIT);
                            VotDetail.mAdapter_VIT.notifyDataSetChanged();
                        }
                    });
                }
            }

        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVitName;
        ImageView imgFunc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVitName = itemView.findViewById(R.id.tvVitName);
            imgFunc = itemView.findViewById(R.id.imgFunc);
        }
    }

    public void updateData(ArrayList<VIT_VO> list) {
        mList = list;
    }

}
