package com.linktag.linkapp.ui.pot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.util.ClsDateTime;
import com.linktag.base.util.ClsImage;
import com.linktag.linkapp.R;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.value_object.PotVO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotDetail extends BaseActivity {
    //======================
    // Final
    //======================
    private final int TAKE_PHOTO = 0;
    private final int ALBUM = 1;
    private final int DELETE_PHOTO = 2;

    // 사진첨부
    private final int REQUEST_CODE_ALBUM_PHOTO = 204;
    // 사진첨부 사진 촬영
    private final int REQUEST_CODE_PHOTO_TAKE_PHOTO = 205;
    private final int REQUEST_CODE_CROP = 206;
    private final int REQUEST_CODE_CROP_ALBUM = 207;
    // 사진 타입
    private final int MEDIA_TYPE_IMAGE = 1;
    // 동영상 타입
    private final int MEDIA_TYPE_VIDEO = 2;

    //======================
    // Layout
    //======================
    private BaseHeader header;

//    private ImageView imgPotPhoto;

    private TextView tvName;
    private TextView tvPreWaterDay;
    private EditText etMemo;

    private NumberPicker npCycle;
    private NumberPicker npCycle2;

    private Switch swAlarm;

    private TimePicker tpAlarmTime;

    private Button btnWaterUpdate;
    private Button btnSave;

    //======================
    // Variable
    //======================
//    private File fileTakePhoto;
//    private Uri uriPhoto;
//    private Uri uriAlbum;
//    private String mBase64 = "";
//    private String mUserImage = "";
//    private String fpath = "";

    //======================
    // Initialize
    //======================

    public String ARM_03 = "N";
    public int ARM_04 = 0;

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

        if(getIntent().getExtras().getString("POT_97").equals(mUser.Value.OCM_01)){ //작성자만 삭제버튼 보임
            header.btnHeaderRight1.setVisibility((View.VISIBLE));
            header.btnHeaderRight1.setMaxWidth(50);
            header.btnHeaderRight1.setMaxHeight(50);
            header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
            header.btnHeaderRight1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(mActivity)
                            .setMessage("해당 화분을 삭제하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPOT_CONTROL("DELETE");
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .show();

                }
            });
        }

//        imgPotPhoto = (ImageView) findViewById(R.id.imgPotPhoto);
//        imgPotPhoto.setOnClickListener(v -> setPotPhoto());

        tvName = (TextView) findViewById(R.id.tvName);
        tvPreWaterDay = (TextView) findViewById(R.id.tvPreWaterDay);
        etMemo = (EditText) findViewById(R.id.etMemo);

        npCycle = (NumberPicker) findViewById(R.id.npCycle);
        npCycle.setMinValue(0);
        npCycle.setMaxValue(60);
        npCycle2 = (NumberPicker) findViewById(R.id.npCycle2);
        npCycle2.setMinValue(0);
        npCycle2.setMaxValue(1);
        npCycle2.setDisplayedValues(new String[] {"일", "개월"});

        swAlarm = (Switch) findViewById(R.id.swAlarm);

        tpAlarmTime = (TimePicker) findViewById(R.id.tpAlarmTime);

        btnWaterUpdate = (Button) findViewById(R.id.btnWaterUpdate);
        btnWaterUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new AlertDialog.Builder(mActivity)
                        .setMessage("물주기를 업데이트 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener(){
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public  void onClick(DialogInterface dialog, int which){
                                requestPOT_CONTROL("WATER");
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener(){
                            @Override
                            public  void onClick(DialogInterface dialog, int which){
                                return;
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
//                try {
                    requestPOT_CONTROL("UPDATE");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initialize() {
        getDetail();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getDetail() {
        /* 최초 로딩시 intent 가져옴 */
//        ClsImage.setUserPhoto(mContext, imgPotPhoto, mUserImage, R.drawable.ic_menu_gallery); //오아시스껀데 안되는거같은뎁..,,
//        setImage(getIntent().getExtras().getString("POT_81"));

        tvName.setText(getIntent().getExtras().getString("POT_02"));
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

        ARM_04 = getIntent().getExtras().getInt("ARM_04");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPOT_CONTROL(String GUB) {

        //인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            Toast.makeText(mActivity, "인터넷 연결을 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        openLoadingBar();

        String GUBUN = GUB;
        String POT_ID = "1"; //컨테이너 수정해야돼!!!
        String POT_01 = getIntent().getExtras().getString("POT_01"); //코드번호
        String POT_02 = getIntent().getExtras().getString("POT_02"); //명칭
        int POT_04 = npCycle.getValue(); //주기
        String POT_05 = "M";
        if(npCycle2.getValue() == 0){ //주기구분
            POT_05 = "D"; //일
        }
        String POT_06 = etMemo.getText().toString(); //메모
        String POT_81 = getIntent().getExtras().getString("POT_81"); //이미지
        String POT_96 = (tpAlarmTime.getHour()<10 ? "0" + String.valueOf(tpAlarmTime.getHour()) : String.valueOf(tpAlarmTime.getHour())) + (tpAlarmTime.getMinute()<10 ? "0" + String.valueOf(tpAlarmTime.getMinute()) : String.valueOf(tpAlarmTime.getMinute())); //알림시간
        String POT_98 = mUser.Value.OCM_01; //사용자코드
        if(swAlarm.isChecked()){ //알림여부
            ARM_03 = "Y";
        }
        else{
            ARM_03 = "N";
        }

        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                POT_ID,
                POT_01,
                POT_02,
                POT_04,

                POT_05,
                POT_06,
                POT_81,
                POT_96,
                POT_98,

                ARM_03
        );

        call.enqueue(new Callback<POT_Model>(){
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<POT_Model> call, Response<POT_Model> response){
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;

                            if(GUB.equals("DELETE")){
                                callBack(GUB, new PotVO());
                            }
                            else{
                                //알람 주석처리
//                                AlarmMain alarmMain = new AlarmMain();
//                                int ID = response.body().Data.get(0).ARM_04;
//                                alarmMain.deleteAlarm(getApplicationContext(), ARM_04); //기존 푸시알람 해제
//                                ARM_04 = ID;
//
//                                if(ARM_03.equals("Y")){
//                                    String alarmTitle = "물주기 - " + response.body().Data.get(0).POT_02;
//                                    String alarmText = "식물에게 물을 주세요~";
//                                    String className = ".ui.pot.PotScan";
//
//                                    Intent intent = new Intent();
//                                    intent.putExtra("POT_81", response.body().Data.get(0).POT_81);
//                                    intent.putExtra("POT_02", response.body().Data.get(0).POT_02);
//                                    intent.putExtra("POT_03_T", response.body().Data.get(0).POT_03_T);
//                                    intent.putExtra("POT_04", response.body().Data.get(0).POT_04);
//                                    intent.putExtra("POT_05", response.body().Data.get(0).POT_05);
//                                    intent.putExtra("ARM_03", response.body().Data.get(0).ARM_03);
//                                    intent.putExtra("POT_96", response.body().Data.get(0).POT_96);
//                                    intent.putExtra("POT_06", response.body().Data.get(0).POT_06);
//                                    intent.putExtra("POT_01", response.body().Data.get(0).POT_01);
//                                    intent.putExtra("POT_97", response.body().Data.get(0).POT_97);
//                                    intent.putExtra("className", className);
//
//                                    intent.putExtra("ID", ID);
//                                    intent.putExtra("alarmTitle", alarmTitle);
//                                    intent.putExtra("alarmText", alarmText);
//
//                                    alarmMain.setAlarm(getApplicationContext(), intent); //새로운 푸시알람 설정
//                                    }
                                callBack(GUB, response.body().Data.get(0));
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<POT_Model> call, Throwable t){
                Log.d("POT_CONTROL", t.getMessage());
                closeLoadingBar();
            }
        });

    }

    //이미지 테스트용...
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void requestPOT_CONTROL2(String GUB) throws IOException {
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
//        String POT_ID = "1"; //컨테이너 수정해야돼!!!
//        String POT_01 = getIntent().getExtras().getString("POT_01"); //코드번호
//        String POT_02 = getIntent().getExtras().getString("POT_02"); //명칭
//        int POT_04 = npCycle.getValue(); //주기
//        String POT_05 = "M";
//        if(npCycle2.getValue() == 0){ //주기구분
//            POT_05 = "D"; //일
//        }
//        String POT_06 = etMemo.getText().toString(); //메모
//        String POT_81 = getIntent().getExtras().getString("POT_81"); //이미지
//        String POT_96 = (tpAlarmTime.getHour()<10 ? "0" + String.valueOf(tpAlarmTime.getHour()) : String.valueOf(tpAlarmTime.getHour())) + (tpAlarmTime.getMinute()<10 ? "0" + String.valueOf(tpAlarmTime.getMinute()) : String.valueOf(tpAlarmTime.getMinute())); //알림시간
//        String POT_98 = mUser.Value.OCM_01; //사용자코드
//        if(swAlarm.isChecked()){ //알림여부
//            ARM_03 = "Y";
//        }
//        else{
//            ARM_03 = "N";
//        }
//
//        File file = new File(fpath);
////        File file = fileTakePhoto;
//
////        RequestBody POT_81_F = RequestBody.create(MediaType.parse("image/jpeg"), file);
//        MultipartBody POT_81_F = MultipartBody.Part.createFormData("POT_81_F", file.getPath(), RequestBody.create(MediaType.parse("image/jpeg"), file));
//
//        Call<POT_Model> call = Http.pot(HttpBaseService.TYPE.POST).POT_CONTROL(
//                BaseConst.URL_HOST,
//                GUBUN,
//                POT_ID,
//                POT_01,
//                POT_02,
//                POT_04,
//
//                POT_05,
//                POT_06,
//                POT_81,
//                POT_96,
//                POT_98,
//
//                ARM_03
////                POT_81_F
//        );
//
//        call.enqueue(new Callback<POT_Model>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<POT_Model> call, Response<POT_Model> response){
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
//                            Response<POT_Model> response = (Response<POT_Model>) msg.obj;
//
//                            if(GUB.equals("DELETE")){
//                                callBack(GUB, new PotVO());
//                            }
//                            else{
//                                callBack(GUB, response.body().Data.get(0));
//                            }
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<POT_Model> call, Throwable t){
//                Log.d("POT_CONTROL", t.getMessage());
//                closeLoadingBar();
//            }
//        });
//
//    }

    private void callBack(String GUB, PotVO data){
        if(data.Validation){
            switch(GUB){
                case "UPDATE":
                    finish();
                    break;
                case "WATER":
                    setUserData(data);
                    break;
                case "DELETE":
                    finish();
                    break;
            }
        }

    }

    private void setUserData(PotVO potVO) {
        tvPreWaterDay.setText(potVO.POT_03_T);
    }

//    private void setPotPhoto() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//        final String str[] = {
//                "사진 찍기",
//                "사진 선택",
//                "사진 삭제"
//        };
//
//        builder.setTitle("화분 사진 변경").setNegativeButton(R.string.common_cancel, null)
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
////                            ClsImage.setUserPhoto(mContext, imgPotPhoto, "", R.drawable.main_profile_no_image);
//                            setImage(getIntent().getExtras().getString("POT_81"));
//                            mBase64 = "";
//                            mUserImage = "";
//                            break;
//                    }
//                }).setCancelable(false).create();
//
//        builder.show();
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
//
//        fileTakePhoto = getOutputMediaFile(MEDIA_TYPE_IMAGE);
//    }

//    @SuppressLint("SimpleDateFormat")
//    private File getOutputMediaFile(int type) {
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LinkApp");
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
//                imgPotPhoto.setImageBitmap(bitmap);
//                mBase64 = ClsImage.getBase64ImageString(bitmap);
//                mUserImage = uriPhoto.getPath();
////                SaveBitmapToFileCache(bitmap, )
//                fpath = saveBitmapToJpeg(mContext, bitmap, "potImage");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (requestCode == REQUEST_CODE_CROP_ALBUM && resultCode == RESULT_OK) {
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriAlbum);
//                imgPotPhoto.setImageURI(uriAlbum);
//                mBase64 = ClsImage.getBase64ImageString(bitmap);
//                mUserImage = uriAlbum.getPath();
////                ByteArrayOutputStream bos = new ByteArrayOutputStream();
////                file = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
////                fpath = mUserImage + ;
//                fpath = saveBitmapToJpeg(mContext, bitmap, "potImage");
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
////                                fpath
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
//                            imgPotPhoto.setImageBitmap(bitmap);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                }
//            }
//        }
//
//    }

//    private void setImage(String string_url){
//        Thread mThread = new Thread(){
//            @Override
//            public void run(){
//                try{
//                    URL url = new URL("http://app.linktag.io" + string_url);
//
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setDoInput(true);
//                    conn.connect();
//
//                    InputStream is = conn.getInputStream();
//                    bitmap = BitmapFactory.decodeStream(is);
//                } catch (MalformedURLException e){
//                    e.printStackTrace();
//                } catch(IOException e){
//                    e.printStackTrace();
//                }
//            }
//        };
//        if(string_url.equals("")){ //저장된 이미지가 없다.
//            imgPotPhoto.setImageResource(R.drawable.ic_menu_gallery);
//        }
//        else{ //저장된 이미지가 있다!
//            mThread.start();
//            try{
//                mThread.join();
//                imgPotPhoto.setImageBitmap(bitmap);
//            } catch (InterruptedException e){
//                e.printStackTrace();
//            }
//        }
//    }

//    public static String saveBitmapToJpeg(Context context, Bitmap bitmap, String name){
//
//        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로
//
//        String fileName = name + ".jpg";  // 파일이름은 마음대로!
//
//        File tempFile = new File(storage,fileName);
//
//        try{
//            tempFile.createNewFile();  // 파일을 생성해주고
//
//            FileOutputStream out = new FileOutputStream(tempFile);
//
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌
//
//            out.close(); // 마무리로 닫아줍니다.
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴해주면 끝!
//    }


}
