package com.linktag.linkapp.ui.vot;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.VIC_VO;

import java.util.ArrayList;

public class DetailVicRecycleAdapter extends RecyclerView.Adapter<DetailVicRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VIC_VO> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    DetailVicRecycleAdapter(Context context, ArrayList<VIC_VO> list) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();
    }

    @NonNull
    @Override
    public DetailVicRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_vic
                , parent, false);
        DetailVicRecycleAdapter.ViewHolder viewHolder = new DetailVicRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.tvVicName.setText(mList.get(position).VIC_03_NM);
        if(mList.get(position).VIC_03.equals(mUser.Value.OCM_01)){
            viewHolder.btnMe.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.btnMe.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVicName;
        Button btnMe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVicName = itemView.findViewById(R.id.tvVicName);
            btnMe = itemView.findViewById(R.id.btnMe);
        }
    }

    public void updateData(ArrayList<VIC_VO> list) {
        mList = list;
    }

}
