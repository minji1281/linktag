package com.linktag.linkapp.ui.dam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.ClsBitmap;
import com.linktag.linkapp.R;
import com.linktag.linkapp.value_object.DamVO;
import com.linktag.linkapp.value_object.DcmVO;
import com.linktag.linkapp.value_object.VamVO;
import com.soundcloud.android.crop.Crop;

import java.util.ArrayList;

import static com.linktag.linkapp.ui.dam.DamDetail.icon;
import static com.linktag.linkapp.ui.dam.DamDetail.img_icon;

public class DamUserIconRecycleAdapter extends RecyclerView.Adapter<DamUserIconRecycleAdapter.ViewHolder> {


    private Activity mActivity;
    private Context mContext;
    private LayoutInflater mInflater;
    private View view;

    private int lastSelectedPosition = -1;
    private int mIndex;

    private ArrayList<DcmVO> mList;


    private final int PICK_FROM_CAMERA = 0;
    private final int PICK_FROM_ALBUM = 1;
    private final int DELETE_PHOTO = 2;

    public static int viewHolder_index;

    private InterfaceUser mUser = InterfaceUser.getInstance();

    DamUserIconRecycleAdapter(Activity activity, Context context, ArrayList<DcmVO> list) {
        mList = list;
        mContext = context;
        mActivity = activity;
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

        ClsBitmap.setSharedPhoto(mContext, viewHolder.img_icon,  mUser.Value.OCM_01, mUser.Value.OCM_52, "", R.drawable.btn_add);


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

                viewHolder_index = position;
                setUserPhoto();

                notifyDataSetChanged();

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

    private void setUserPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(R.string.set_profile_image).setNegativeButton(R.string.common_cancel, null)
                .setItems(R.array.photo_select, (dialog, which) -> {
                    switch (which) {
                        // 사직 찍기
                        case PICK_FROM_CAMERA:
                            //takePhoto();
                            break;
                        // 사진 선택
                        case PICK_FROM_ALBUM:
                            goToAlbum();
                            break;
                        // 사직 삭제
//                        case DELETE_PHOTO:
//                            if(!mUser.Value.OCM_52.equals("")){
//                                ClsBitmap.setProfilePhoto(mContext, imgProfile, mUser.Value.OCM_01,"", mUser.Value.OCM_52, R.drawable.main_profile_no_image);
//                                requestOCM_CONTROL("UPDATE_IMG");
//                            }
//                            break;
                    }
                }).create();

        builder.show();
    }


    /**
     * 앨범에서 이미지 가져오기
     */
    private void goToAlbum() {

        DamIconDetail.isCamera = false;

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        mActivity.startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void updateData(ArrayList<DcmVO> list) {
        mList = list;


    }



}
