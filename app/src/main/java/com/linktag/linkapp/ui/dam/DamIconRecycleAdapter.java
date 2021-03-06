package com.linktag.linkapp.ui.dam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.linktag.linkapp.R;

import static com.linktag.linkapp.ui.dam.DamDetail.img_icon;

public class DamIconRecycleAdapter extends RecyclerView.Adapter<DamIconRecycleAdapter.ViewHolder> {


    private Context mContext;
    private LayoutInflater mInflater;
    private View view;

    private int lastSelectedPosition = -1;
    private int mIndex;
    private String DAM_03;

    DamIconRecycleAdapter(Context context, String filename) {
        mContext = context;
        DAM_03 = filename;
    }


    @NonNull
    @Override
    public DamIconRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_dam_icon_list, parent, false);

        DamIconRecycleAdapter.ViewHolder viewHolder = new DamIconRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if (!DAM_03.equals("")) {
            mIndex = Integer.parseInt(DAM_03.replace("dam_icon_", "")) - 1;

            if (position == mIndex) {
                viewHolder.img_check.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.img_check.setVisibility(View.GONE);
        }
        int resource = mContext.getResources().getIdentifier("dam_icon_" + (position + 1), "drawable", mContext.getPackageName());
        viewHolder.img_icon.setImageResource(resource);


        if (lastSelectedPosition != -1) {
            if (position == lastSelectedPosition) {
                viewHolder.img_check.setVisibility(View.VISIBLE);
            } else {
                viewHolder.img_check.setVisibility(View.GONE);
            }
        }

        viewHolder.img_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename = "dam_icon_" + (position + 1);
                lastSelectedPosition = position;
                int resource = mContext.getResources().getIdentifier(filename, "drawable", mContext.getPackageName());
                img_icon.setImageResource(resource);
                DamDetail.filename = filename;
                notifyDataSetChanged();
                DamIconDetail.mAdapter2.updateDAM_03(filename);

            }
        });

    }


    @Override
    public int getItemCount() {
        return 16;
    }

    public void updateDAM_03(String filename, int initPos) {
        DAM_03 = filename;
        lastSelectedPosition = initPos;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        ImageView img_icon;
        ImageView img_check;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_icon = itemView.findViewById(R.id.img_icon);
            img_check = itemView.findViewById(R.id.img_check);

        }
    }

}
