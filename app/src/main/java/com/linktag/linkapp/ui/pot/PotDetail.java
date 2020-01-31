package com.linktag.linkapp.ui.pot;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.linkapp.R;

//import com.linktag.base.network.ClsNetworkCheck;
//import com.linktag.base.settings.SettingsKey;
//import com.linktag.base.util.ClsDateTime;
//import com.linktag.base.util.ClsImage;
//import com.linktag.base_resource.broadcast_action.ClsBroadCast;

public class PotDetail extends BaseActivity {
    //======================
    // Final
    //======================
//    private final int TAKE_PHOTO = 0;
//    private final int ALBUM = 1;
//    private final int DELETE_PHOTO = 2;
//
//    // 사진첨부
//    private final int REQUEST_CODE_ALBUM_PHOTO = 204;
//    // 사진첨부 사진 촬영
//    private final int REQUEST_CODE_PHOTO_TAKE_PHOTO = 205;
//    private final int REQUEST_CODE_CROP = 206;
//    private final int REQUEST_CODE_CROP_ALBUM = 207;
//    // 사진 타입
//    private final int MEDIA_TYPE_IMAGE = 1;
//    // 동영상 타입
//    private final int MEDIA_TYPE_VIDEO = 2;


    //======================
    // Layout
    //======================
    private BaseHeader header;
//    private ImageView imgProfilePhoto;
//    private Button btnChangePhoto;
//    private EditText etUserName;
//    private EditText etPhoneNumber;
//    private EditText etDep;
//    private EditText etRank;
    private EditText etName;
    private TextView tvPreWaterDay;
    private TextView etMemo;

    private NumberPicker npCycle;
    private NumberPicker npCycle2;

    private Switch swAlarm;

    private TimePicker tpAlarmTime;
//
//    private LinearLayout layoutPhoto;
//    private LinearLayout layoutChangePwd;
//    private RelativeLayout layoutChangePhone;
//    private RelativeLayout layoutName;
//    private RelativeLayout layoutChangeEmail;
//    private RelativeLayout layoutChangeDep;
//    private RelativeLayout layoutChangeRank;
//
//    private EditText etOldPwd;
//    private EditText etNewPwd1;
//    private EditText etNewPwd2;
//    private Button btnChangePwd;
//    private Button btnSubmitPwd;

    //======================
    // Variable
    //======================
//    private File fileTakePhoto;
//    private Uri uriPhoto;
//    private Uri uriAlbum;
//    private String mBase64 = "";
//    private String mUserImage = "";

    //======================
    // Initialize
    //======================
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pot_detail);

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());
        header.btnHeaderRight1.setVisibility((View.VISIBLE));
        header.btnHeaderRight1.setMaxWidth(50);
        header.btnHeaderRight1.setMaxHeight(50);
        header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
        header.btnHeaderRight1.setOnClickListener(v -> setOnClickDelete());

        etName = findViewById(R.id.etName);
        tvPreWaterDay = findViewById(R.id.tvPreWaterDay);
        etMemo = findViewById(R.id.etMemo);

        npCycle = findViewById(R.id.npCycle);
        npCycle.setMinValue(0);
        npCycle.setMaxValue(60);
        npCycle2 = findViewById(R.id.npCycle2);
        npCycle2.setMinValue(0);
        npCycle2.setMaxValue(1);
        npCycle2.setDisplayedValues(new String[] {"일", "개월"});

        swAlarm = findViewById(R.id.swAlarm);

        tpAlarmTime = findViewById(R.id.tpAlarmTime);

//        header.btnHeaderText.setVisibility(View.VISIBLE);
//        header.btnHeaderText.setOnClickListener(v -> requestOCM_CONTROL("UPDATE"));
//
//        imgProfilePhoto = findViewById(R.id.imgProfilePhoto);
//        if (Build.VERSION.SDK_INT >= 21) {
//            imgProfilePhoto.setClipToOutline(true);
//        }
//
//        btnChangePhoto = findViewById(R.id.btnChangePhoto);
//        btnChangePhoto.setOnClickListener(v -> setUserPhoto());
//
//        etUserName = findViewById(R.id.etUserName);
//        etUserName.setText(mUser.Value.OCM_02);
//
//        etPhoneNumber = findViewById(R.id.etPhoneNumber);
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
//            etPhoneNumber.setText(PhoneNumberUtils.formatNumber(mUser.Value.OCM_51));
//        else
//            etPhoneNumber.setText(PhoneNumberUtils.formatNumber(mUser.Value.OCM_51, Locale.getDefault().getCountry()));
//
//        etDep = findViewById(R.id.etDep);
//        etDep.setText(mUser.Value.OCM_31);
//
//        etRank = findViewById(R.id.etRank);
//        etRank.setText(mUser.Value.OCM_32);
//
//        layoutPhoto = findViewById(R.id.layoutPhoto);
//
//        layoutChangePwd = findViewById(R.id.layoutChangePwd);
//        layoutChangePwd.setVisibility(View.GONE);
//
//        layoutChangePhone = findViewById(R.id.layoutChangePhone);
//        layoutName = findViewById(R.id.layoutName);
//        layoutChangeEmail = findViewById(R.id.layoutChangeEmail);
//        layoutChangeDep = findViewById(R.id.layoutChangeDep);
//        layoutChangeRank = findViewById(R.id.layoutChangeRank);
//
//        etOldPwd = findViewById(R.id.etOldPwd);
//        etNewPwd1 = findViewById(R.id.etNewPwd1);
//        etNewPwd2 = findViewById(R.id.etNewPwd2);
//        etNewPwd2.setOnKeyListener(new View.OnKeyListener(){
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
//                    btnSubmitPwd.performClick();
//                    return true;
//                }
//
//                return false;
//            }
//        });
//
//        btnChangePwd = findViewById(R.id.btnChangePwd);
//        btnChangePwd.setOnClickListener(v -> setUserPwd());
//
//        btnSubmitPwd = findViewById(R.id.btnSubmitPwd);
//        btnSubmitPwd.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(mActivity)
//                        .setMessage("비밀번호를 변경하시겠습니까?\n변경후에는 자동으로 로그아웃 됩니다.")
//                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requestOCM_CONTROL("PASS");
//                            }
//                        })
//                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                return;
//                            }
//                        })
//                        .setCancelable(false)
//                        .show();
//
//            }
//        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initialize() {
//        String mUserImage = "";
//        String setPwd = getIntent().getExtras().getString("setPwd", "");
//
//        // 프로필 이미지 설정
//        ClsImage.setUserPhoto(mContext, imgProfilePhoto, mUserImage, R.drawable.main_profile_no_image);

        getDetail();
//
//        if(setPwd.equals("1")){
//            setUserPwd();
//            etOldPwd.requestFocus();
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDetail() {
        /* 최초 로딩시 intent 가져옴 */
        etName.setText(getIntent().getExtras().getString("POT_02"));
        tvPreWaterDay.setText(getIntent().getExtras().getString("POT_03_T"));
        etMemo.setText(getIntent().getExtras().getString("POT_06"));

        npCycle.setValue(getIntent().getExtras().getInt("POT_04"));
        if(getIntent().getExtras().getString("POT_05").equals("D")){
            npCycle2.setValue(0); //일
        }
        else{ //M
            npCycle2.setValue(1); //개월
        }

        Boolean alarm = false;
        if(getIntent().getExtras().getString("ARM_03").equals("Y")){
            alarm = true;
        }
        swAlarm.setChecked(alarm);

        tpAlarmTime.setHour(Integer.parseInt(getIntent().getExtras().getString("POT_96").substring(8, 10)));
        tpAlarmTime.setMinute(Integer.parseInt(getIntent().getExtras().getString("POT_96").substring(10)));
    }

    private void setOnClickDelete(){
        Toast.makeText(mContext, "삭제 준비중입니다!!!", Toast.LENGTH_SHORT).show();
    }

//    private void setUserPwd() {
//        if(btnChangePwd.getText().equals("수정")) {
//            layoutPhoto.setVisibility(View.GONE);
//            layoutChangePhone.setVisibility(View.GONE);
//            layoutName.setVisibility(View.GONE);
//            layoutChangeEmail.setVisibility(View.GONE);
//            layoutChangePwd.setVisibility(View.VISIBLE);
//            layoutChangeDep.setVisibility(View.GONE);
//            layoutChangeRank.setVisibility(View.GONE);
//            header.btnHeaderText.setVisibility(View.GONE);
//            etOldPwd.setText("");
//            etNewPwd1.setText("");
//            etNewPwd2.setText("");
//            btnChangePwd.setText("취소");
//            etOldPwd.requestFocus();
//
//        } else {
//            layoutPhoto.setVisibility(View.VISIBLE);
//            layoutChangePhone.setVisibility(View.VISIBLE);
//            layoutName.setVisibility(View.VISIBLE);
//            layoutChangeEmail.setVisibility(View.VISIBLE);
//            layoutChangePwd.setVisibility(View.GONE);
//            layoutChangeDep.setVisibility(View.VISIBLE);
//            layoutChangeRank.setVisibility(View.VISIBLE);
//            header.btnHeaderText.setVisibility(View.VISIBLE);
//            etOldPwd.setText("");
//            etNewPwd1.setText("");
//            etNewPwd2.setText("");
//            btnChangePwd.setText("수정");
//        }
//    }

//    private void setUserPhoto() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//        final String str[] = {
//                "사진 찍기",
//                "사진 선택",
//                "사진 삭제"
//        };
//
//        builder.setTitle("프로필 사진 변경").setNegativeButton(R.string.common_cancel, null)
//                .setItems(str, (dialog, which) -> {
//                    switch (which) {
//                        // 사직 찍기
//                        case TAKE_PHOTO:
//                            clickSendPhotoTakePhoto();
//                            break;
//                        // 사진 선택
//                        case ALBUM:
//                            clickSendPhotoAlbumPhoto();
//                            break;
//                        // 사직 삭제
//                        case DELETE_PHOTO:
//                            ClsImage.setUserPhoto(mContext, imgProfilePhoto, "", R.drawable.main_profile_no_image);
//                            mBase64 = "";
//                            mUserImage = "";
//                            break;
//                    }
//                }).setCancelable(false).create();
//
//        builder.show();
//    }

//    private boolean validationCheck(String GUB){
//        if(GUB.equals("PASS"))
//        {
//            if(!mUser.Value.OCM_03.equals(etOldPwd.getText().toString())){
//                etOldPwd.requestFocus();
//                Toast.makeText(mActivity, "현재 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if(etNewPwd1.getText().toString().length() == 0){
//                etNewPwd1.requestFocus();
//                Toast.makeText(mActivity, "수정 할 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//            if(!etNewPwd1.getText().toString().equals(etNewPwd2.getText().toString())){
//                etNewPwd1.requestFocus();
//                Toast.makeText(mActivity, "입력된 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        } else if (GUB.equals("UPDATE")) {
//
//        }
//
//        return true;
//    }


//    private void requestOCM_CONTROL(String GUB){
//        if(!validationCheck(GUB))
//            return;
//
//        //인터넷 연결 여부 확인
//        if(!ClsNetworkCheck.isConnectable(mContext)){
//            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        openLoadingBar();
//
//        String GUBUN = GUB;
//        String OCM_01 = mUser.Value.OCM_01;
//        String OCM_02 = etUserName.getText().toString();
//        String OCM_03 = etNewPwd1.getText().toString();
//        String OCM_31 = etDep.getText().toString();
//        String OCM_32 = etRank.getText().toString();
//        String OCM_51 = etPhoneNumber.getText().toString();
//        String OCM_98 = mUser.Value.OCM_01;
//
//        Call<OCM_Model> call = Http.ocm(HttpBaseService.TYPE.POST).OCM_CONTROL(
//                BaseConst.URL_HOST,
//                GUBUN,
//                OCM_01,
//                OCM_02,
//                OCM_03,
//                OCM_31,
//                OCM_32,
//                OCM_51,
//                OCM_98
//        );
//
//        call.enqueue(new Callback<OCM_Model>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<OCM_Model> call, Response<OCM_Model> response){
//                Message msg = new Message();
//                msg.obj = response;
//                msg.what = 100;
//
//                new Handler(){
//                    @Override
//                    public void handleMessage(Message msg){
//                        if(msg.what == 100){
//                            closeLoadingBar();
//
//                            Response<OCM_Model> response = (Response<OCM_Model>) msg.obj;
//
//                            callBack(GUB, response.body().Data.get(0));
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<OCM_Model> call, Throwable t){
//                Log.d("OCM_CONTROL", t.getMessage());
//                closeLoadingBar();
//            }
//        });
//
//    }

//    private void callBack(String GUB, OcmVO data){
//        if(data.Validation){
//            switch(GUB){
//                case "PASS":
//                    logout();
//                    //setUserPwd();
//                    break;
//                case "UPDATE":
//                    setUserData(data);
//                    break;
//            }
//        }
//
//    }

//    private void setUserData(OcmVO ocmVO) {
//        mUser.Value.OCM_02 = ocmVO.OCM_02;
//        mUser.Value.OCM_31 = ocmVO.OCM_31;
//        mUser.Value.OCM_32 = ocmVO.OCM_32;
//        mUser.Value.OCM_51 = ocmVO.OCM_51;
//    }

    /**
     * 사진첨부 찍어서 보내기
     */
//    protected void clickSendPhotoTakePhoto() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        fileTakePhoto = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//        uriPhoto = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", fileTakePhoto);
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);
//        startActivityForResult(intent, REQUEST_CODE_PHOTO_TAKE_PHOTO);
//    }

    /**
     * 사진첨부 앨범에서 사진 보내기
     */
//    protected void clickSendPhotoAlbumPhoto() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        intent.setType("image/*");
//        startActivityForResult(intent, REQUEST_CODE_ALBUM_PHOTO);
//    }


//    @SuppressLint("SimpleDateFormat")
//    private File getOutputMediaFile(int type) {
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "auas");
//
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("Test", "failed to create directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = ClsDateTime.getNow("yyyyMMddHHmmss", Locale.US);
//        File mediaFile;
//
//        if (type == MEDIA_TYPE_IMAGE) {
//            mediaFile = new File(
//                    mediaStorageDir.getPath() + File.separator + "_IMG_" + timeStamp + ".jpg");
//        } else if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(
//                    mediaStorageDir.getPath() + File.separator + "_VID_" + timeStamp + ".mp4");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }

    /**
     * 크롭하기
     */
//    private void cropImage() {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uriPhoto, "image/*");
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.putExtra("outputX", 300);
//        intent.putExtra("outputY", 300);
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//
//        this.grantUriPermission("com.android.camera", uriPhoto,
//                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//        mActivity.startActivityForResult(intent, REQUEST_CODE_CROP);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // 사진
//        if (requestCode == REQUEST_CODE_CROP && resultCode == RESULT_OK) {
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPhoto);
//                imgProfilePhoto.setImageBitmap(bitmap);
//                mBase64 = ClsImage.getBase64ImageString(bitmap);
//                mUserImage = uriPhoto.getPath();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (requestCode == REQUEST_CODE_CROP_ALBUM && resultCode == RESULT_OK) {
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriAlbum);
//                imgProfilePhoto.setImageURI(uriAlbum);
//                mBase64 = ClsImage.getBase64ImageString(bitmap);
//                mUserImage = uriAlbum.getPath();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            if (resultCode == RESULT_OK) {
//                switch (requestCode) {
//                    case REQUEST_CODE_ALBUM_PHOTO:
//                        if (data.getData() != null) {
//                            try {
//                                uriPhoto = data.getData();
//                                cropImage();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        break;
//                    case REQUEST_CODE_PHOTO_TAKE_PHOTO:
//                        if (!fileTakePhoto.exists()) {
//                            return;
//                        }
//
//                        cropImage();
//                        break;
//                    case REQUEST_CODE_CROP:
//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPhoto);
//                            imgProfilePhoto.setImageBitmap(bitmap);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                }
//            }
//        }
//
//    }

    /**
     * 로그아웃
     */
//    private void logout() {
//        openLoadingBar();
//        new Handler().postDelayed(() -> {
//            closeLoadingBar();
//            mSettings.Value.AutoLogin = false;
//            mSettings.putBooleanItem(SettingsKey.AutoLogin, false);
//            Intent intent = new Intent(ClsBroadCast.BROAD_CAST_ACTION_LOGOUT);
//            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//        }, 500);
//    }


}
