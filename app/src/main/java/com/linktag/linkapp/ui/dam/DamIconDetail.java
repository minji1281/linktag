package com.linktag.linkapp.ui.dam;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ImageResizeUtils;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.DCMModel;
import com.linktag.linkapp.model.TRDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.trp.TrpRecycleAdapter;
import com.linktag.linkapp.value_object.DcmVO;
import com.linktag.linkapp.value_object.TrpVO;
import com.linktag.linkapp.value_object.VamVO;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DamIconDetail extends BaseActivity {

    private BaseHeader header;

    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private DamIconRecycleAdapter mAdapter;

    private LinearLayoutManager linearLayoutManager2;
    private RecyclerView recyclerView2;
    private DamUserIconRecycleAdapter mAdapter2;

    private ArrayList<DcmVO> mList;

    private final int PICK_FROM_CAMERA = 0;
    private final int PICK_FROM_ALBUM = 1;
    private final int DELETE_PHOTO= 2;


    private File tempFile;
    private Uri filePath;
    public static Boolean isCamera = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_dam_icon);

        initLayout();
        initialize();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initLayout() {

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);

        linearLayoutManager = new GridLayoutManager(mContext, 4);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new DamIconRecycleAdapter(mContext, getIntent().getIntExtra("index",0));
        recyclerView.setAdapter(mAdapter);


        recyclerView2 = findViewById(R.id.recyclerView2);

        linearLayoutManager2 = new GridLayoutManager(mContext, 4);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(linearLayoutManager2);
        mAdapter2 = new DamUserIconRecycleAdapter(mActivity, mContext, mList);
        recyclerView2.setAdapter(mAdapter2);

    }

    @Override
    protected void initialize() {
        requestDCM_SELECT();
    }


    public void requestDCM_SELECT() {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }


        Call<DCMModel> call = Http.dcm(HttpBaseService.TYPE.POST).DCM_SELECT(
                BaseConst.URL_HOST,
                "LIST",
                getIntent().getExtras().getString("DAM_ID"),
                getIntent().getExtras().getString("DAM_01")
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
                            if (mList == null)
                                mList = new ArrayList<>();
                            mAdapter2.updateData(mList);
                            mAdapter2.notifyDataSetChanged();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            switch (requestCode) {
                case PICK_FROM_CAMERA: {
                    Uri photoUri = Uri.fromFile(tempFile);

                    cropImage(photoUri);
                    break;
                }
                case PICK_FROM_ALBUM: {
                    Uri photoUri = data.getData();

                    cropImage(photoUri);
                    break;
                }
                case Crop.REQUEST_CROP: {
                    setImage();
                }
            }

        }
    }


    /**
     *  Crop 기능
     */
    private void cropImage(Uri photoUri) {
        /**
         *  갤러리에서 선택한 경우에는 tempFile 이 없으므로 새로 생성해줍니다.
         */
        if(tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, R.string.alert_image_error1, Toast.LENGTH_SHORT).show();
                finish();
                e.printStackTrace();
            }
        }

        //크롭 후 저장할 Uri
        filePath = Uri.fromFile(tempFile);

        Crop.of(photoUri, filePath).asSquare().start(this);
    }

    /**
     *  폴더 및 파일 만들기
     */
    private File createImageFile() throws IOException {
        // 이미지 파일 이름
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss_");
        Date now = new Date();
        String imageFileName = formatter.format(now);


        // 이미지가 저장될 파일 이름
        File storageDir = new File(getExternalFilesDir(null).getAbsolutePath() + "/linktag/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".png", storageDir);

        return image;
    }

    /**
     *  tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
     */
    private void setImage() {
        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        //mAdapter2.updateData(originalBm);
//        imgProfile.setImageBitmap(originalBm);
//
//        /**
//         *  tempFile 사용 후 null 처리를 해줘야 합니다.
//         *  (resultCode != RESULT_OK) 일 때 (tempFile != null)이면 해당 파일을 삭제하기 때문에
//         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
//         */
//
//        isChangeImg = true;
//        setFileName = tempFile.getName();
//        tempFile = null;
    }



}
