package com.linktag.linkapp.ui.settings_profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.linktag.base.util.ClsBitmap;
import com.linktag.base.util.ImageResizeUtils;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.OcmVO;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.settings.SettingsKey;
import com.linktag.base_resource.broadcast_action.ClsBroadCast;
import com.linktag.linkapp.R;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileMain extends BaseActivity {
    //======================
    // Final
    //======================
    private final int PICK_FROM_CAMERA = 0;
    private final int PICK_FROM_ALBUM = 1;
    private final int DELETE_PHOTO= 2;

    private final String FIREBASE_URL = "gs://linktag-a43f8.appspot.com";

    //======================
    // Layout
    //======================
    private BaseHeader header;

    private ImageView imgProfile;

    //private Button btnChangePhoto;
    private EditText etUserName;
    private EditText etPhoneNumber;

    private LinearLayout layoutPhoto;

    private LinearLayout layoutEmail;
    private LinearLayout layoutSignDate;

    private LinearLayout layoutChangePwd;

    private LinearLayout layoutChange;

    private EditText etOldPwd;
    private EditText etNewPwd1;
    private EditText etNewPwd2;
    private Button btnChangePwd;
    private Button btnSubmitPwd;

    //======================
    // Variable
    //======================
    private Boolean isChangeImg = false;
    private Boolean isCamera = false;
    private File tempFile;
    private Uri filePath;

    private String setFileName;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    //======================
    // Initialize
    //======================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());
        header.btnHeaderText.setVisibility(View.VISIBLE);
        header.btnHeaderText.setOnClickListener(v -> requestOCM_CONTROL("UPDATE"));

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21)
            imgProfile.setClipToOutline(true);

        imgProfile.setOnClickListener(v -> setUserPhoto());

        etUserName = findViewById(R.id.etUserName);
        etUserName.setText(mUser.Value.OCM_02);

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            etPhoneNumber.setText(PhoneNumberUtils.formatNumber(mUser.Value.OCM_51));
        else
            etPhoneNumber.setText(PhoneNumberUtils.formatNumber(mUser.Value.OCM_51, Locale.getDefault().getCountry()));

        layoutPhoto = findViewById(R.id.layoutPhoto);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutSignDate = findViewById(R.id.layoutSignDate);

        layoutChangePwd = findViewById(R.id.layoutChangePwd);

        layoutChange = findViewById(R.id.layoutChange);

        etOldPwd = findViewById(R.id.etOldPwd);
        etNewPwd1 = findViewById(R.id.etNewPwd1);
        etNewPwd2 = findViewById(R.id.etNewPwd2);
        etNewPwd2.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    btnSubmitPwd.performClick();
                    return true;
                }

                return false;
            }
        });
        
        btnChangePwd = findViewById(R.id.btnChangePwd);
        btnChangePwd.setOnClickListener(v -> setUserPwd());

        btnSubmitPwd = findViewById(R.id.btnSubmitPwd);
        btnSubmitPwd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity)
                        .setMessage(getString(R.string.alert_pwd_change1) + "\n" + getString(R.string.alert_pwd_change2))
                        .setPositiveButton(R.string.common_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestOCM_CONTROL("PASS");
                            }
                        })
                        .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        storage = FirebaseStorage.getInstance();
        
    }

    private void setUserPwd() {
        if(btnChangePwd.getText().equals(getString(R.string.common_change))) {
            layoutPhoto.setVisibility(View.GONE);
            layoutEmail.setVisibility(View.GONE);
            layoutSignDate.setVisibility(View.GONE);
            layoutChange.setVisibility(View.GONE);

            layoutChangePwd.setVisibility(View.VISIBLE);

            header.btnHeaderText.setVisibility(View.GONE);
            etOldPwd.setText("");
            etNewPwd1.setText("");
            etNewPwd2.setText("");
            btnChangePwd.setText(getString(R.string.common_cancel));
            etOldPwd.requestFocus();

        } else {
            layoutPhoto.setVisibility(View.VISIBLE);
            layoutEmail.setVisibility(View.VISIBLE);
            layoutSignDate.setVisibility(View.VISIBLE);
            layoutChange.setVisibility(View.VISIBLE);

            layoutChangePwd.setVisibility(View.GONE);

            header.btnHeaderText.setVisibility(View.VISIBLE);
            etOldPwd.setText("");
            etNewPwd1.setText("");
            etNewPwd2.setText("");
            btnChangePwd.setText(getString(R.string.common_change));
        }
    }

    private void setUserPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle(R.string.set_profile_image).setNegativeButton(R.string.common_cancel, null)
                .setItems(R.array.photo_select, (dialog, which) -> {
                    switch (which) {
                        // 사직 찍기
                        case PICK_FROM_CAMERA:
//                            if(isPermission)  takePhoto();
//                            else Toast.makeText(this, getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                            takePhoto();
                            break;
                        // 사진 선택
                        case PICK_FROM_ALBUM:
//                            if(isPermission) goToAlbum();
//                            else Toast.makeText(this, getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                            goToAlbum();
                            break;
                        // 사직 삭제
                        case DELETE_PHOTO:
                            if(!mUser.Value.OCM_52.equals("")){
                                ClsBitmap.setProfilePhoto(mContext, imgProfile, mUser.Value.OCM_01,"", mUser.Value.OCM_52, R.drawable.main_profile_no_image);
                                requestOCM_CONTROL("UPDATE_IMG");
                            }

                            break;
                    }
                }).create();

        builder.show();
    }

    @Override
    protected void initialize() {
        ClsBitmap.setProfilePhoto(mContext, imgProfile, mUser.Value.OCM_01, mUser.Value.OCM_52, "", R.drawable.main_profile_no_image);
    }

    private boolean validationCheck(String GUB){
        if(GUB.equals("PASS"))
        {
            if(!mUser.Value.OCM_03.equals(etOldPwd.getText().toString())){
                etOldPwd.requestFocus();
                Toast.makeText(mActivity, R.string.login_1, Toast.LENGTH_SHORT).show();
                return false;
            }
            if(etNewPwd1.getText().toString().length() == 0){
                etNewPwd1.requestFocus();
                Toast.makeText(mActivity, R.string.placeholder_profile_pwd1, Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!etNewPwd1.getText().toString().equals(etNewPwd2.getText().toString())){
                etNewPwd1.requestFocus();
                Toast.makeText(mActivity, R.string.login_1, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (GUB.equals("UPDATE")) {

        }

        return true;
    }


    private void requestOCM_CONTROL(String GUB){
        if(!validationCheck(GUB))
            return;

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, R.string.common_network_error, Toast.LENGTH_SHORT).show();
            return;
        }

        //openLoadingBar();

        String GUBUN = GUB;
        String OCM_01 = mUser.Value.OCM_01;
        String OCM_02 = etUserName.getText().toString();
        String OCM_03 = etNewPwd1.getText().toString();
        String OCM_24 = "";
        String OCM_51 = etPhoneNumber.getText().toString();
        String OCM_52;
        if(isChangeImg)
            OCM_52 = setFileName;
        else
            OCM_52 = mUser.Value.OCM_52;

        String OCM_98 = mUser.Value.OCM_01;

        Call<OCM_Model> call = Http.ocm(HttpBaseService.TYPE.POST).OCM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                OCM_01,
                OCM_02,
                OCM_03,
                OCM_24,
                OCM_51,
                OCM_52,
                OCM_98
        );

        call.enqueue(new Callback<OCM_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<OCM_Model> call, Response<OCM_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            //closeLoadingBar();

                            Response<OCM_Model> response = (Response<OCM_Model>) msg.obj;

                            callBack(GUB, response.body().Data.get(0));
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<OCM_Model> call, Throwable t){
                Log.d("OCM_CONTROL", t.getMessage());
                //closeLoadingBar();
            }
        });

    }

    private void callBack(String GUB, OcmVO data){
        if(data.Validation){
            switch(GUB){
                case "PASS":
                    logout();
                    //setUserPwd();
                    break;
                case "UPDATE":
                    setUserData(data);
                    break;
                case "UPDATE_IMG":
                    isChangeImg = false;
                    // 기존파일 삭제
                    storageRef = storage.getReferenceFromUrl(FIREBASE_URL).child("profile" + "/" + mUser.Value.OCM_01 + "/" + mUser.Value.OCM_52);
                    storageRef.delete();
                    mUser.Value.OCM_52 = "";
                    Toast.makeText(mActivity, R.string.common_updated, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    private void setUserData(OcmVO ocmVO) {
        if(isChangeImg){
            uploadFile();
            isChangeImg = false;
        }

        mUser.Value.OCM_02 = ocmVO.OCM_02;
        mUser.Value.OCM_51 = ocmVO.OCM_51;
        mUser.Value.OCM_52 = ocmVO.OCM_52;
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

    private void uploadFile(){
        if(filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.common_uploading);
            progressDialog.show();

            //storage 주소와 폴더 파일명을 지정해 준다.
            // 기존 파일 삭제 후 업로드
            storageRef = storage.getReferenceFromUrl(FIREBASE_URL).child("profile" + "/" + mUser.Value.OCM_01 + "/" + mUser.Value.OCM_52);
            storageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    storageRef = storage.getReferenceFromUrl(FIREBASE_URL).child("profile" + "/" + mUser.Value.OCM_01 + "/" + setFileName);
                    storageRef.putFile(filePath)
                            //성공시
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                                    Toast.makeText(getApplicationContext(), R.string.common_updated, Toast.LENGTH_SHORT).show();
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
            });

        } else {
            Toast.makeText(this, R.string.common_file_notfound, Toast.LENGTH_SHORT).show();
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

        imgProfile.setImageBitmap(originalBm);

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
            System.out.println("########### NOT RESULT_OK");
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

    /**
     * 로그아웃
     */
    private void logout() {
        openLoadingBar();
        new Handler().postDelayed(() -> {
            closeLoadingBar();
            mSettings.Value.AutoLogin = false;
            mSettings.putBooleanItem(SettingsKey.AutoLogin, false);
            Intent intent = new Intent(ClsBroadCast.BROAD_CAST_ACTION_LOGOUT);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }, 500);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }

        return result;
    }


}
