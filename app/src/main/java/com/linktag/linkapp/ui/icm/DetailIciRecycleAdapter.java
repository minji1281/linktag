package com.linktag.linkapp.ui.icm;

import android.app.AlertDialog;
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

import java.util.ArrayList;

public class DetailIciRecycleAdapter extends RecyclerView.Adapter<DetailIciRecycleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<String> mList;
    private LayoutInflater mInflater;
    private View view;
    private InterfaceUser mUser;

    private IciClickListener iciClickListener;
    private AlertDialog dialog;

    public interface IciClickListener{
        void onListIciClick(int position);
    }

    DetailIciRecycleAdapter(Context context, ArrayList<String> list, IciClickListener iciClickListener, AlertDialog dialog_tmp) {
        mContext = context;
        mList = list;
        mUser = InterfaceUser.getInstance();

        this.iciClickListener = iciClickListener;
        dialog = dialog_tmp;
    }

    @NonNull
    @Override
    public DetailIciRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.listitem_find_detail_ici
                , parent, false);
        DetailIciRecycleAdapter.ViewHolder viewHolder = new DetailIciRecycleAdapter.ViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String tmp = "ici_" + String.valueOf(position);
        viewHolder.imgIciIcon.setBackgroundResource(mContext.getResources().getIdentifier(tmp, "drawable", mContext.getPackageName()));
        viewHolder.tvIciName.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIciIcon;
        TextView tvIciName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgIciIcon = itemView.findViewById(R.id.imgIciIcon);
            tvIciName = itemView.findViewById(R.id.tvIciName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    iciClickListener.onListIciClick(position);
                    dialog.dismiss();
                }
            });
        }
    }

    public void updateData(ArrayList<String> list) {
        mList = list;
    }

}
