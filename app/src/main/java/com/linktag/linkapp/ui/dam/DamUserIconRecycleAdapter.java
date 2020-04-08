package com.linktag.linkapp.ui.dam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.user_interface.InterfaceUser;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsBitmap;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.DAMModel;
import com.linktag.linkapp.model.DCMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.DcmVO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.PrimitiveIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.linktag.linkapp.ui.dam.DamDetail.img_icon;

public class DamUserIconRecycleAdapter extends RecyclerView.Adapter<DamUserIconRecycleAdapter.ViewHolder> {


    private Activity mActivity;
    private Context mContext;
    private LayoutInflater mInflater;
    private View view;

    private int lastSelectedPosition = -1;
    private int mIndex;

    private ArrayList<DcmVO> mList;


    private final int ICON_SELECT = 0;
    private final int PICK_FROM_ALBUM = 1;
    private final int DELETE_PHOTO = 2;


    private final int INIT_PICK_FROM_ALBUM = 0;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    private final String FIREBASE_URL = "gs://linktag-a43f8.appspot.com";

    private String DAM_03;

    private int checkedPos = -1;

    private InterfaceUser mUser = InterfaceUser.getInstance();

    DamUserIconRecycleAdapter(Activity activity, Context context, ArrayList<DcmVO> list, String filename) {
        mList = list;
        mContext = context;
        mActivity = activity;
        DAM_03 = filename;
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


        if (mList.size() > 0 && mList.size() >= position + 1) {
            ClsBitmap.setSharedDamIcon(mContext, viewHolder.img_icon, mList.get(position).DCM_ID, mList.get(position).DCM_01, mList.get(position).DCM_03, "", R.drawable.btn_add);
            if (DAM_03.equals(mList.get(position).DCM_03)) {
                viewHolder.img_check.setVisibility(View.VISIBLE);
                checkedPos = position;
            } else {
                viewHolder.img_check.setVisibility(View.GONE);
            }

            viewHolder.img_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setUserPhoto(mList.get(position).DCM_ID, mList.get(position).DCM_01, mList.get(position).DCM_02, mList.get(position).DCM_03, position);

                }
            });


        } else {
            ClsBitmap.setSharedDamIcon(mContext, viewHolder.img_icon, "", "", "", "", R.drawable.btn_add);
            viewHolder.img_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setUserPhoto();

                }
            });
        }
    }

    public void updateDAM_03(String filename) {
        DAM_03 = filename;
        notifyDataSetChanged();
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

            img_icon.setBackground(new ShapeDrawable(new OvalShape()));
            if (Build.VERSION.SDK_INT >= 21)
                img_icon.setClipToOutline(true);
        }
    }

    private void setUserPhoto() {
        goToAlbum();
    }


    private void setUserPhoto(String DCM_ID, String DCM_01, String DCM_02, String DCM_03, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("아이콘 이미지 설정").setNegativeButton(R.string.common_cancel, null)
                .setItems(R.array.dam_icon_select, (dialog, which) -> {
                    switch (which) {
                        //선택한 아이콘 사용
                        case ICON_SELECT:
                            if (!DCM_01.equals("")) {
                                iconSelect(DCM_ID, DCM_01, DCM_03, position);
                            }
                            break;
                        // 사진 선택
                        case PICK_FROM_ALBUM:
                            goToAlbum(DCM_02, DCM_03);
                            break;
                        // 사직 삭제
                        case DELETE_PHOTO:
                            iconDelete(DCM_ID, DCM_01, DCM_02, DCM_03, position);
                            break;
                    }
                }).create();

        builder.show();
    }

    private void iconDelete(String DCM_ID, String DCM_01, String DCM_02, String DCM_03, int position) {
        ClsBitmap.setSharedDamIcon(mContext, img_icon, "", "", "", "", R.drawable.btn_add);
        if (checkedPos == position) {

            requestDAM_CONTROL(DCM_ID, DCM_01);
        } else {
            notifyDataSetChanged();
        }

        storageRef = storage.getReferenceFromUrl(FIREBASE_URL).child("shared_dam_icon" + "/" + DCM_ID + "/" + "/" + DCM_01 + "/" + DCM_03);
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                requestDCM_CONTROL(DCM_ID, DCM_01, DCM_02);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext.getApplicationContext(), R.string.alert_image_error1, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void iconSelect(String DCM_ID, String DCM_01, String DCM_03, int position) {
        ClsBitmap.setSharedDamIcon(mContext, img_icon, DCM_ID, DCM_01, DCM_03, "", R.drawable.btn_add);
        this.DAM_03 = DCM_03;
        DamDetail.filename = DCM_03;

        mActivity.finish();
//        notifyDataSetChanged();
//        DamIconDetail.mAdapter.updateDAM_03("", -1);

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

    private void goToAlbum(String DCM_02, String DCM_03) {

        DamIconDetail.isCamera = false;

        Intent intent = new Intent(Intent.ACTION_PICK);

        mActivity.getIntent().putExtra("DCM_02", DCM_02);
        mActivity.getIntent().putExtra("filename", DCM_03);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        mActivity.startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public void updateData(ArrayList<DcmVO> list) {
        mList = list;
    }

    public void requestDCM_CONTROL(String DAM_ID, String DAM_01, String DAM_02) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<DCMModel> call = Http.dcm(HttpBaseService.TYPE.POST).DCM_CONTROL(
                BaseConst.URL_HOST,
                "DELETE",
                DAM_ID,
                DAM_01,
                DAM_02,
                "",
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<DCMModel>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<DCMModel> call, Response<DCMModel> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<DCMModel> response = (Response<DCMModel>) msg.obj;
                            mList = response.body().Data;
                            if (mList == null) mList = new ArrayList<>();

                            updateData(mList);
                            notifyDataSetChanged();

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<DCMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }


    public void requestDAM_CONTROL(String DAM_ID, String DAM_01) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<DAMModel> call = Http.dam(HttpBaseService.TYPE.POST).DAM_CONTROL(
                BaseConst.URL_HOST,
                "UPDATE_ICON",
                DAM_ID,
                DAM_01,
                "",
                "dam_icon_1",
                "",
                "",
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                ""
        );


        call.enqueue(new Callback<DAMModel>() {
            @Override
            public void onResponse(Call<DAMModel> call, Response<DAMModel> response) {

                int resource = mContext.getResources().getIdentifier("dam_icon_1", "drawable", mContext.getPackageName());
                img_icon.setImageResource(resource);
                DAM_03 = "dam_icon_1";
                DamDetail.filename = "dam_icon_1";
                DamIconDetail.mAdapter.updateDAM_03("dam_icon_1", -1);
                DamIconDetail.mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<DAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

            }
        });

    }


}
