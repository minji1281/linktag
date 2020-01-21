package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;
import com.linktag.linkapp.API.ClovaFace;
import com.linktag.linkapp.API.WeatherAPI;
import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.linkapp.R;
import com.linktag.base.util.ClsImage;
import com.linktag.linkapp.model.LEDModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommuteFragment extends BaseFragment {
    //======================
    private final int TAKE_PHOTO = 0;
    private final int ALBUM = 0;
    private final int DELETE_PHOTO = 1;

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

    //========================
    // Layout
    //========================
    private View view;

    private TextView tvGoToWork;
    private TextView tvLeaveWork;
    private TextView tvWorkPlace;
    private TextView tvRecord;


    private File tempFile;
    private Uri uriPhoto;
    private Uri uriAlbum;
    private ImageView imgProfilePhoto;
    private Button btnChangePhoto;
    private Button btnSubmit;

    //===================================
    // Variable
    //===================================

    private String mBase64 = "";
    private String mUserImage = "";
    private ClovaFace clovaFace;


    public CommuteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_commute, container, false);

        initLayout();

        return view;
    }

    /**
     * 레이아웃 초기화
     */
    private void initLayout() {
        imgProfilePhoto = view.findViewById(R.id.imgProfilePhoto);
        if (Build.VERSION.SDK_INT >= 21) {
            imgProfilePhoto.setClipToOutline(true);
        }

        btnChangePhoto = view.findViewById(R.id.btnChangePhoto);
        btnChangePhoto.setOnClickListener(v -> setUserPhoto());

        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> submit());
    }

    private void setUserPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        final String str[] = {
                "사진 선택",
                "사진 삭제"
        };

        builder.setTitle("프로필 사진 변경").setNegativeButton(R.string.common_cancel, null)
                .setItems(str, (dialog, which) -> {
                    switch (which) {
                        // 사진 선택
                        case ALBUM:
                            clickSendPhotoAlbumPhoto();
                            break;
                        // 사직 삭제
                        case DELETE_PHOTO:
                            ClsImage.setUserPhoto(mContext, imgProfilePhoto, "", R.drawable.main_profile_no_image);
                            mBase64 = "";
                            mUserImage = "";
                            break;
                    }
                }).setCancelable(false).create();

        builder.show();
    }

    private void submit(){
        /*
        // Weather API

        Location loca = new Location("test");
        loca.setLatitude(35.234169);
        loca.setLongitude(128.686235);

        // 생성할때 넘겨줄 값
        // 1. 컨테이너, 2. 매장코드
        WeatherAPI weatherAPI = new WeatherAPI();
        weatherAPI.execute(loca);


 */

        // Clova Face API
        if(tempFile != null){
            // 생성자 2가지 'FACE', 'CELEB'
            // 생성자 입력값 없으면 FACE 자동

            // ClovaFace clovaFace = new ClovaFace("FACE");
            //clovaFace.execute(tempFile);

        } else {
            Toast.makeText(mActivity, "전송할 사진을 선택해 주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 사진첨부 앨범에서 사진 보내기
     */
    protected void clickSendPhotoAlbumPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_ALBUM_PHOTO);
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    //================================
    // Event
    //================================
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ALBUM_PHOTO)
        {
            if(resultCode == mActivity.RESULT_OK){
                Uri photoUri = data.getData();
                Cursor cursor = null;

                try {

                    /*
                     *  Uri 스키마를
                     *  content:/// 에서 file:/// 로  변경한다.
                     */
                    String[] proj = { MediaStore.Images.Media.DATA };

                    assert photoUri != null;
                    cursor = mActivity.getContentResolver().query(photoUri, proj, null, null, null);

                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    cursor.moveToFirst();

                    tempFile = new File(cursor.getString(column_index));
                    System.out.println("@@@@@@@");
                    System.out.println(tempFile.getName());

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                setImage();
            }
            else if(resultCode == mActivity.RESULT_CANCELED){
                //Toast.makeText(mActivity, "canceled", Toast.LENGTH_SHORT).show();
            }
        }

    }

//    private void requestCMTL_CONTROL(final String type, String CDO_02, String CDO_04) {
//        // 인터넷 연결 여부 확인
//        if (!ClsNetworkCheck.isConnectable(mContext)){
//            BaseAlert.show(getString(R.string.common_network_error));
//            return;
//        }
//        if(!checkLocationServicesStatus()) {
//            if(!checkLocationServicesStatus()) {
//                Toast.makeText(mContext, "위치 수집 권한이 없습니다.", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        openLoadingBar();
//
//        String GUBUN = "CONTROL";
//        String LED_ID = mUser.Value.OCP_ID;
//        String LED_01 = "CMTL";
//        String LED_04 = mUser.Value.OCM_01;
//        String LED_05 = CDO_04;
//        String LED_07 = ClsDateTime.getNow("HHmm");
//        String LED_23 = ClsDateTime.getNow("yyyyMMdd");
//        String LED_25 = CDO_02;
//        String LED_97 = etComment.getText().toString();
//
//        Call<LEDModel> call = Http.commute(HttpBaseService.TYPE.POST).CMTL_CONTROL(
//                BaseConst.URL_HOST,
//                GUBUN,
//                LED_ID,
//                LED_01,
//                LED_02,
//                LED_04,
//                LED_05,
//                LED_07,
//                LED_23,
//                LED_25,
//                latitude,
//                longitude,
//                LED_97,
//                mUser.Value.OCM_01
//        );
//
//        call.enqueue(new Callback<LEDModel>(){
//            @SuppressLint("HandlerLeak")
//            @Override
//            public void onResponse(Call<LEDModel> call, Response<LEDModel> response){
//                Message msg = new Message();
//                msg.obj = response;
//                msg.what = 100;
//
//                new Handler(){
//                    @Override
//                    public void handleMessage(Message msg){
//                        if (msg.what == 100){
//                            closeLoadingBar();
//
//                            Response<LEDModel> response = (Response<LEDModel>) msg.obj;
//
//                            callBack(response.body().Data.get(0));
//                        }
//                    }
//                }.sendMessage(msg);
//            }
//
//            @Override
//            public void onFailure(Call<LEDModel> call, Throwable t){
//                Log.d("Test", t.getMessage());
//                closeLoadingBar();
//            }
//        });
//    }

    public void setScan(String jsonStr){
//        try {
//
//            JSONArray jsonArray = new JSONArray(jsonStr);
//
//            if (jsonArray.length() > 0)
//            {
//                JSONObject obj = jsonArray.getJSONObject(0);
//
//                String LED_ID = obj.getString("LED_ID");
//                String LED_01 = obj.getString("LED_01");
//                String CDO_02 = obj.getString("CDO_02");
//                String CDO_04 = obj.getString("CDO_08");
//
//                // 로그인 회사 == 스캔 회사 일때
//                if(mUser.Value.OCP_ID.equals(LED_ID))
//                {
//                    if(LED_01.equals("CMT"))
//                    {
//                        // GPS 오차범위 비교
//                        // ATD_CONTROL 넣어야함
//                      //  requestCMTL_CONTROL(INPUT_TYPE_SCAN, CDO_02, CDO_04);
//                    }
//                    else{
//                        // LED_01값에 따라 다른 항목 스캔
//                    }
//                }
//                else{
//                    Toast.makeText(mContext, "일치하지 않은 사업장 입니다.", Toast.LENGTH_SHORT).show();
//                    //BaseAlert.show("일치하지 않은 사업장 입니다. (" + CTM_17 + ")");
//                }
//
//            }
//            else {
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    private void setImage() {

        ImageView imageView = view.findViewById(R.id.imgProfilePhoto);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        imageView.setImageBitmap(originalBm);

    }

}
