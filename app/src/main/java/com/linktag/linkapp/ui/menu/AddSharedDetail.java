package com.linktag.linkapp.ui.menu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsBitmap;
import com.linktag.base.util.ImageResizeUtils;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.CtdVO;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSharedDetail extends BaseActivity {
    //======================
    // Final
    //======================
    private final String GUBUN_TYPE_INSERT = "INSERT_SHARED";
    private final String GUBUN_TYPE_UPDATE = "UPDATE_SHARED";

    private final int PICK_FROM_CAMERA = 0;
    private final int PICK_FROM_ALBUM = 1;
    private final int DELETE_PHOTO = 2;

    private final String FIREBASE_URL = "gs://linktag-a43f8.appspot.com";

    //===================================
    // Layout
    //===================================
    private BaseHeader header;

    private EditText etSharedName;
    private ImageView imgShared;
    private Button btnSubmit;

    //===================================
    // Variable
    //===================================
    private String GUBUN;
    private String type;
    private String typeStr;
    private CtdVO intentVO;

    private Boolean isChangeImg = false;
    private Boolean isCamera = false;
    private File tempFile;
    private Uri filePath;

    private String setFileName;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shared_detail);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        etSharedName = findViewById(R.id.etSharedName);
        imgShared = findViewById(R.id.imgShared);
        if (Build.VERSION.SDK_INT >= 21)
            imgShared.setClipToOutline(true);
        imgShared.setOnClickListener(v -> setSharedPhoto());

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage("[" + intentVO.CTD_02_NM + "] " + getString(R.string.alert_service_add));
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.common_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        requestCTD_CONTROL();
                    }
                });
                builder.setNegativeButton(R.string.common_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.create().show();
            }
        });

        storage = FirebaseStorage.getInstance();

    }

    @Override
    protected void initialize() {
        type = getIntent().getStringExtra("type");
        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");

        if(type.equals("INSERT")){
            GUBUN = GUBUN_TYPE_INSERT;
            typeStr = getResources().getString(R.string.common_added);
            header.tvHeaderTitle.setText(getString(R.string.menu_bar2) + " " + getString(R.string.common_add));
        } else {
            GUBUN = GUBUN_TYPE_UPDATE;
            typeStr = getResources().getString(R.string.common_updated);
            if(!intentVO.CTD_08.equals(""))
                ClsBitmap.setSharedPhoto(mContext, imgShared, intentVO.CTD_01, intentVO.CTD_08, "", R.drawable.shared_no_image);
            etSharedName.setText(intentVO.CTM_17);
            header.tvHeaderTitle.setText(getString(R.string.menu_bar2) + " " + getString(R.string.common_update));
        }

        etSharedName.requestFocus();
    }

    private void setSharedPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(R.string.set_profile_image).setNegativeButton(R.string.common_cancel, null)
                .setItems(R.array.photo_select, (dialog, which) -> {
                    switch (which) {
                        // 사직 찍기
                        case PICK_FROM_CAMERA:
                            takePhoto();
                            break;
                        // 사진 선택
                        case PICK_FROM_ALBUM:
                            goToAlbum();
                            break;
                        // 사직 삭제
                        case DELETE_PHOTO:
                            if(intentVO.CTD_01 != null && intentVO.CTD_01 != "" && !intentVO.CTD_08.equals("")){
                                ClsBitmap.setSharedPhoto(mContext, imgShared, intentVO.CTD_01,"", intentVO.CTD_08, R.drawable.shared_no_image);
                                requestCTD_CONTROL();
                            }

                            break;
                    }
                }).create();

        builder.show();
    }

    /**
     *  앨범에서 이미지 가져오기
     */
    private void goToAlbum() {
        isCamera = false;

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    /**
     *  카메라에서 이미지 가져오기
     */
    private void takePhoto() {
        isCamera = true;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, R.string.alert_image_error1, Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (tempFile != null) {

            /**
             *  안드로이드 OS 누가 버전 이후부터는 file:// URI 의 노출을 금지로 FileUriExposedException 발생
             *  Uri 를 FileProvider 도 감싸 주어야 합니다.
             *
             */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", tempFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else {

                Uri photoUri = Uri.fromFile(tempFile);
                //Log.d(TAG, "takePhoto photoUri : " + photoUri);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            }
        }
    }

    private void storageTask(){
        if(filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.common_uploading);
            progressDialog.show();

            if (intentVO.CTD_01 != null && !intentVO.CTD_01.equals("") && !intentVO.CTD_08.equals("")){
                storageRef = storage.getReferenceFromUrl(FIREBASE_URL).child("shared" + "/" + intentVO.CTD_01 + "/" + intentVO.CTD_08);
                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadFile(progressDialog);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadFile(progressDialog);
                    }
                });
            } else {
                uploadFile(progressDialog);
            }
        } else {
            Toast.makeText(this, R.string.common_file_notfound, Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile(ProgressDialog progressDialog){
        storageRef = storage.getReferenceFromUrl(FIREBASE_URL).child("shared" + "/" + intentVO.CTD_01 + "/" + setFileName);
        storageRef.putFile(filePath)
        //성공시
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기

                Toast.makeText(getApplicationContext(), typeStr, Toast.LENGTH_SHORT).show();
                mActivity.finish();
            }
        })
        //실패시
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.alert_image_error1, Toast.LENGTH_SHORT).show();
            }
        })
        //진행중
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //dialog에 진행률을 퍼센트로 출력해 준다
                progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
            }
        });
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
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    /**
     *  tempFile 을 bitmap 으로 변환 후 ImageView 에 설정한다.
     */
    private void setImage() {
        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, isCamera);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        imgShared.setImageBitmap(originalBm);

        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 (tempFile != null)이면 해당 파일을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */

        isChangeImg = true;
        setFileName = tempFile.getName();
        tempFile = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Toast.makeText(this, R.string.common_canceled, Toast.LENGTH_SHORT).show();

            if (tempFile != null) {
                if (tempFile.delete()) {
                    Toast.makeText(this, R.string.common_deleted, Toast.LENGTH_SHORT).show();
                    tempFile = null;
                }
            }
            return;
        }

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

    public void requestCTD_CONTROL() {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        String CTD_01 = intentVO.CTD_01;
        String SVC_02 = intentVO.CTD_02;
        String CTD_10 = etSharedName.getText().toString();
        String CTD_08;
        if(isChangeImg)
            CTD_08 = setFileName;
        else
            CTD_08 = intentVO.CTD_08;

        //openLoadingBar();

        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CTD_01,
                SVC_02,
                "1",
                "1",
                "3",
                0,
                mUser.Value.OCM_01,
                CTD_08,
                "",
                CTD_10,
                "",
                mUser.Value.OCM_01
        );

        call.enqueue(new Callback<CTD_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CTD_Model> call, Response<CTD_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            //closeLoadingBar();

                            Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;
                            callBack(response.body().Data.get(0));

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CTD_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                //closeLoadingBar();

            }
        });

    }

    private void callBack(CtdVO data){
        if(isChangeImg){
            if(GUBUN.equals(GUBUN_TYPE_INSERT))
                intentVO.CTD_01 = data.CTD_01;
            storageTask();
        } else {
            Toast.makeText(getApplicationContext(), typeStr, Toast.LENGTH_SHORT).show();
            mActivity.finish();
        }

    }
}
