package com.linktag.linkapp.ui.iam;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsAES;
import com.linktag.base.util.ScanCode;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.CDS_Model;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.IAMModel;
import com.linktag.linkapp.model.JDMModel;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.dam.DamDetail;
import com.linktag.linkapp.ui.jdm.JdmDetail;
import com.linktag.linkapp.ui.menu.ChangeActivityCls;
import com.linktag.linkapp.ui.menu.ChooseOne;
import com.linktag.linkapp.ui.menu.ChooseScan;
import com.linktag.linkapp.ui.scanner.ScanResult;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.IamVO;
import com.linktag.linkapp.value_object.JdmVO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IamDetail extends BaseActivity {

    private BaseHeader header;

    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private Intent alarmIntent;
    private Calendar calendar = Calendar.getInstance();
    private TimePicker picker;

    private EditText ed_name;

    private Button bt_alarm;
    private Button bt_goScan;
    private Button bt_stop;
    private Button bt_save;

    private CtdVO intentVO;
    private IamVO iamVO;
    private String scanCode;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        setContentView(R.layout.activity_iam_detail);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        initLayout();
        initialize();


        intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
        if (getIntent().hasExtra("scanCode")) {
            scanCode = getIntent().getStringExtra("scanCode");
            header.btnHeaderRight1.setVisibility((View.GONE));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initLayout() {

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        linearLayout = findViewById(R.id.linearLayout);

        iamVO = (IamVO) getIntent().getSerializableExtra("IamVO");


        picker = findViewById(R.id.timePicker);
        picker.setIs24HourView(true);

        ed_name = findViewById(R.id.ed_name);

        bt_alarm = findViewById(R.id.bt_alarm);
        bt_goScan = findViewById(R.id.bt_goScan);
        bt_stop = findViewById(R.id.bt_stop);

        bt_save = findViewById(R.id.bt_save);

        ed_name.setText(iamVO.getIAM_03());

    }

    @Override
    protected void initialize() {


        // 앞서 설정한 값으로 보여주기
        // 없으면 디폴트 값은 현재시간
        SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        long millis = sharedPreferences.getLong("nextNotifyTime", calendar.getTimeInMillis());

        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);

        Date nextDate = nextNotifyTime.getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(nextDate);
        Toast.makeText(getApplicationContext(), "[처음 실행시] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();


        // 이전 설정값으로 TimePicker 초기화
        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());

        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));
        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));


        if (Build.VERSION.SDK_INT >= 23) {
            picker.setHour(pre_hour);
            picker.setMinute(pre_minute);
        } else {
            picker.setCurrentHour(pre_hour);
            picker.setCurrentMinute(pre_minute);
        }


        bt_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                int hour, hour_24, minute;
                String am_pm;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour_24 = picker.getHour();
                    minute = picker.getMinute();
                } else {
                    hour_24 = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }
                if (hour_24 > 12) {
                    am_pm = "PM";
                    hour = hour_24 - 12;
                } else {
                    hour = hour_24;
                    am_pm = "AM";
                }

                // 현재 지정된 시간으로 알람 시간 설정
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour_24);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                // 이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }

                Date currentDateTime = calendar.getTime();
                String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

                //  Preference에 설정한 값 저장
                SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                editor.putLong("nextNotifyTime", (long) calendar.getTimeInMillis());
                editor.apply();


                diaryNotification(calendar);
            }

        });


        bt_goScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goScan();
            }
        });


        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RingtonePlayingService.class);
                intent.setAction("startForeground");
                getApplicationContext().stopService(intent);
            }
        });


        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ed_name.getText().toString().equals("")) {
                    Toast.makeText(mContext, getString(R.string.validation_check1), Toast.LENGTH_LONG).show();
                    return;
                }

                if (scanCode != null) {
                    requestCDS_CONTROL(
                            "INSERT",
                            intentVO.CTD_07,
                            scanCode,
                            "",
                            intentVO.CTD_01,
                            intentVO.CTD_02,
                            intentVO.CTD_09,
                            mUser.Value.OCM_01);

                } else {
                    requestIAM_CONTROL("UPDATE");
                }
            }
        });


    }

    private void requestCDS_CONTROL(String GUBUN, String CTD_07, String scanCode, String CDS_03, String CTD_01, String CTD_02, String CTD_09, String OCM_01) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            BaseAlert.show(mContext.getString(R.string.common_network_error));
            return;
        }

        Call<CDS_Model> call = Http.cds(HttpBaseService.TYPE.POST).CDS_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                CTD_07,
                scanCode,
                CDS_03,
                CTD_01,
                CTD_02,
                CTD_09,
                OCM_01
        );

        call.enqueue(new Callback<CDS_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CDS_Model> call, Response<CDS_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {
                            Response<CDS_Model> response = (Response<CDS_Model>) msg.obj;

                            if (GUBUN.equals("INSERT")) {
                                iamVO.IAM_01 = response.body().Data.get(0).CDS_03;
                                requestIAM_CONTROL("INSERT");
                            }
                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<CDS_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                Toast.makeText(mContext, R.string.common_exception, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void requestIAM_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(IamDetail.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<IAMModel> call = Http.iam(HttpBaseService.TYPE.POST).IAM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                iamVO.IAM_ID,
                iamVO.IAM_01,
                iamVO.IAM_02,
                ed_name.getText().toString(),
                iamVO.IAM_96,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01
        );


        call.enqueue(new Callback<IAMModel>() {
            @Override
            public void onResponse(Call<IAMModel> call, Response<IAMModel> response) {


                if (GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")) {

                    Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "]" + "해당 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
//                    if (iamVO.ARM_03.equals("Y") && dateBool) {
//                        Toast.makeText(mContext,"[" + ed_name.getText().toString() + "]" + getString(R.string.iam_text1) +"\n"+
//                                getString(R.string.iam_text2) +"\n"+ iamVO.IAM_96.substring(0,4)+"-" + iamVO.IAM_96.substring(4,6)+"-"+ iamVO.IAM_96.substring(6,8)+" " +
//                                iamVO.IAM_96.substring(8,10)+":" + iamVO.IAM_96.substring(10,12)+ " " + getString(R.string.iam_text3), Toast.LENGTH_LONG ).show();
//                    }else{
//                        Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "]" + getString(R.string.iam_text1), Toast.LENGTH_SHORT).show();
//                    }
                    onBackPressed();
                }
                if (GUBUN.equals("DELETE")) {
                    onBackPressed();
                }

            }

            @Override
            public void onFailure(Call<IAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());

                requestCDS_CONTROL(
                        "DELETE",
                        intentVO.CTD_07,
                        scanCode,
                        iamVO.IAM_01,
                        "",
                        "",
                        "",
                        "");

                closeLoadingBar();

            }
        });

    }


    /**
     * 바코드를 스캔한다.
     */
    public void goScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanCode.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void scanResult(String str) {
        boolean validation = false;

        String scanCode = "";
        String o = "N";
        try {
            String[] split = str.split("\\?");
            if (split[0].equals("http://linktag.io/scan") || split[0].equals("http://www.linktag.io/scan")) {
                Uri uri = Uri.parse(str);

                String t = uri.getQueryParameter("t");
                String u = uri.getQueryParameter("u");
                String s = uri.getQueryParameter("s");
                o = uri.getQueryParameter("o");

                ClsAES aes = new ClsAES();
                String dec = aes.Decrypt(aes.Base64Decoding(s));

                if (t.length() == 2) {//QR일때
                    if (u.length() == 10 && dec.length() == 15) {
                        validation = true;
                        scanCode = t + u + dec;
                    }
                } else if (t.length() == 3) { //NFC일때             암호화 모듈사용시 오류 발생.
                    if (u.length() == 10 && s.length() == 14) {
                        validation = true;
                        scanCode = t + u + s;
                    }
                } else if (t.length() == 6) { //BEACON일때              암호화 모듈사용시 오류 발생.
                    if (u.length() == 10 && s.length() == 36) {
                        validation = true;
                        scanCode = t + u + s;
                    }
                }
            }
        } catch (Exception e) {
            validation = false;
        }

        if (validation) {

            requestCTD_SELECT(scanCode);

        } else {
            Toast.makeText(mContext, R.string.alert_scan_error1, Toast.LENGTH_LONG).show();
        }

    }

    private void requestCTD_SELECT(String scanCode) {
        Call<CTD_Model> call = Http.ctd(HttpBaseService.TYPE.POST).CTD_SELECT(
                BaseConst.URL_HOST,
                "DETAIL_CDS",
                "",
                "",
                mUser.Value.OCM_01,
                scanCode
        );

        call.enqueue(new Callback<CTD_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<CTD_Model> call, Response<CTD_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 100) {

                            Response<CTD_Model> response = (Response<CTD_Model>) msg.obj;

                            callBack(response.body(), scanCode);

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

    private void callBack(CTD_Model model, String scanCode) {
        if (model.Data.get(0).Validation) {
            if (model.Data.get(0).CDS_03.equals(iamVO.IAM_01)) {
                Intent intent = new Intent(mContext, RingtonePlayingService.class);
                intent.setAction("startForeground");
                getApplicationContext().stopService(intent);
            } else {
                Toast.makeText(mContext, "이거 아닌데?", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(mContext, "이거 아닌데?", Toast.LENGTH_SHORT).show();
        }
    }


    void diaryNotification(Calendar calendar) {
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        Boolean dailyNotify = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DAILY_NOTIFICATION, true);
        Boolean dailyNotify = true; // 무조건 알람을 사용

        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);

        alarmIntent = new Intent(this, AlarmReceiver.class);

        alarmIntent.putExtra("goService", ".ui.iam.RingtonePlayingService");
        alarmIntent.putExtra("goActivity", ".ui.intro.Intro");
        alarmIntent.putExtra("toActivity", ".ui.iam.IamMain");
        alarmIntent.putExtra("CTD_01", intentVO.CTD_01);
        alarmIntent.putExtra("CTD_02", intentVO.CTD_02);
        alarmIntent.putExtra("scanCode", iamVO.IAM_01);
        alarmIntent.putExtra("mRoutCode", iamVO.IAM_02);
        alarmIntent.putExtra("alarmTitle", iamVO.IAM_03 + "알람이 시작되었습니다.");
        alarmIntent.putExtra("alarmText", "스캔을하여 종료하세요.");

        pendingIntent = PendingIntent.getBroadcast(this, iamVO.IAM_02, alarmIntent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {

            //createNotify();

            if (alarmManager != null) {

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }
//        else { //Disable Daily Notifications
//            if (PendingIntent.getBroadcast(this, 0, alarmIntent, 0) != null && alarmManager != null) {
//                alarmManager.cancel(pendingIntent);
//                //Toast.makeText(this,"Notifications were disabled",Toast.LENGTH_SHORT).show();
//            }
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        }
    }


    public void InfinityAlarm() {

        long[] pattern = {1000, 50, 1000, 50};
        vibrator.vibrate(pattern, 0);

    }

    public void createNotify() {

        String alarmTitle = "상태바 드래그시 보이는 타이틀";
        String alarmText = "상태바 드래그시 보이는 서브타이틀";


        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "linktag alarm");


        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.ic_launcher); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남

            int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

            NotificationChannel channel = new NotificationChannel("linktag alarm", alarmTitle, importance);
            channel.setDescription(alarmText);

            if (notificationManager != null) {
                // 노티피케이션 채널을 시스템에 등록
                notificationManager.createNotificationChannel(channel);
            }
        } else
            builder.setSmallIcon(R.mipmap.sym_icon_foreground); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        builder.setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker(alarmText) //알림발생시 잠깐 나오는 텍스트
                .setContentTitle(alarmTitle)
                .setContentText(alarmText)
                .setContentInfo("INFO")
                .setContentIntent(pendingIntent);


        if (notificationManager != null) {
            // 노티피케이션 동작시킴
            notificationManager.notify(1, builder.build()); //id값은 알림채널과 연결용

        }

    }

}
