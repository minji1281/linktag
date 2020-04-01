package com.linktag.linkapp.ui.dam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.linktag.linkapp.R;

import static com.linktag.linkapp.ui.dam.DamDetail.icon;
import static com.linktag.linkapp.ui.dam.DamDetail.img_icon;

public class DamUserIconRecycleAdapter extends RecyclerView.Adapter<DamUserIconRecycleAdapter.ViewHolder> {


    private Context mContext;
    private LayoutInflater mInflater;
    private View view;

    private int lastSelectedPosition = -1;
    private int mIndex;

    DamUserIconRecycleAdapter(Context context, int index) {
        mContext = context;
        mIndex = index;
    }


    @NonNull
    @Override
    public DamUserIconRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_dam_user_icon_list, parent, false);

        DamUserIconRecycleAdapter.ViewHolder viewHolder = new DamUserIconRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

//        String name = "dam_icon_" + (position + 1);
//
//        if (position == mIndex) {
//            viewHolder.img_check.setVisibility(View.VISIBLE);
//        }
//
//        int resource = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
//        viewHolder.img_icon.setImageResource(resource);

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
//                String name = "dam_icon_" + (position + 1);
//                lastSelectedPosition = position;
//                int resource = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
//                img_icon.setImageResource(resource);
//                icon = name;
//                notifyDataSetChanged();
            }
        });

    }


    @Override
    public int getItemCount() {
        return 8;
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
