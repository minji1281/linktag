package com.linktag.linkapp.ui.dam;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.linktag.base.base_activity.BaseActivity;
import com.linktag.base.base_header.BaseHeader;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.linkapp.R;
import com.linktag.linkapp.model.DAMModel;
import com.linktag.linkapp.model.LOG_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.alarm.AlarmDialog;
import com.linktag.linkapp.ui.jdm.RecycleDayDialog;
import com.linktag.linkapp.ui.master_log.MasterLog;
import com.linktag.linkapp.ui.menu.CTDS_CONTROL;
import com.linktag.linkapp.value_object.CtdVO;
import com.linktag.linkapp.value_object.DamVO;
import com.linktag.linkapp.value_object.LogVO;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DamDetail extends BaseActivity {

    private BaseHeader header;

    private EditText ed_name;

    private ImageView imageView;
    private ImageView imageView2;

    private LinearLayout linearLayout_day;
    private TextView tv_message;

    private LinearLayout item1;
    private LinearLayout item2;
    private LinearLayout item3;

    private ImageView img_check1;
    private ImageView img_check2;
    private ImageView img_check3;

    public static ImageView img_icon;

    private DatePicker datePicker;
    private TextView tv_datePicker;

    private DatePickerDialog.OnDateSetListener callbackMethod;

    private String callBackTime = "";
    private LinearLayout linearLayout;
    private InputMethodManager imm;

    private Button bt_save;

    private Button btn_calendar;

    private CtdVO intentVO;
    private DamVO damVO;
    private Calendar calendar = Calendar.getInstance();
    private Calendar tCalendar = Calendar.getInstance();
    private Calendar dCalendar = Calendar.getInstance();
    private Calendar sCalendar = Calendar.getInstance();

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HHmm");

    private TextView tv_D_day;

    public static String icon;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_dam);

        initLayout();

        initialize();

        if (getIntent().hasExtra("GUBUN")) {
            intentVO = (CtdVO) getIntent().getSerializableExtra("intentVO");
            header.btnHeaderRight1.setVisibility((View.GONE));
            linearLayout_day.setVisibility(View.GONE);
            tv_message.setVisibility(View.VISIBLE);
        }

    }

    public void requestDAM_CONTROL(String GUBUN) {
        // 인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(DamDetail.this)) {
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }


        Call<DAMModel> call = Http.dam(HttpBaseService.TYPE.POST).DAM_CONTROL(
                BaseConst.URL_HOST,
                GUBUN,
                damVO.DAM_ID,
                damVO.DAM_01,
                ed_name.getText().toString(),
                damVO.DAM_03,
                damVO.DAM_04,
                damVO.DAM_96,
                mUser.Value.OCM_01,
                mUser.Value.OCM_01,
                damVO.ARM_03
        );


        call.enqueue(new Callback<DAMModel>() {
            @Override
            public void onResponse(Call<DAMModel> call, Response<DAMModel> response) {

                Calendar calendar = Calendar.getInstance();
                boolean dateBool = Long.parseLong(formatDate.format(calendar.getTime()) + formatTime.format(calendar.getTime())) < Long.parseLong(damVO.DAM_96);

                if (GUBUN.equals("INSERT")) {
                    CTDS_CONTROL ctds_control = new CTDS_CONTROL(mContext, intentVO.CTM_01, intentVO.CTD_02, damVO.DAM_01);
                    ctds_control.requestCTDS_CONTROL();
                    onBackPressed();
                }
                if (GUBUN.equals("INSERT") || GUBUN.equals("UPDATE")) {

                    if (damVO.ARM_03.equals("Y") && dateBool) {
                        Toast.makeText(mContext, "[" + ed_name.getText().toString() + "]" + "디데이 정보가 저장되었습니다." + "\n" +
                                "다음 알림예정은" + "\n" + damVO.DAM_96.substring(0, 4) + "-" + damVO.DAM_96.substring(4, 6) + "-" + damVO.DAM_96.substring(6, 8) + " " +
                                damVO.DAM_96.substring(8, 10) + ":" + damVO.DAM_96.substring(10, 12) + " " + "입니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "[" + ed_name.getText().toString() + "]" + "디데이 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    onBackPressed();
                }
                if (GUBUN.equals("DELETE")) {
                    onBackPressed();
                }

            }

            @Override
            public void onFailure(Call<DAMModel> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });

    }


    @Override
    protected void initLayout() {

        header = findViewById(R.id.header);
        header.btnHeaderLeft.setOnClickListener(v -> finish());

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        linearLayout = findViewById(R.id.linearLayout);
        datePicker = findViewById(R.id.datePicker);

        linearLayout_day = findViewById(R.id.linearLayout_day);
        tv_message = findViewById(R.id.tv_message);

        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        item3 = findViewById(R.id.item3);

        img_check1 = findViewById(R.id.img_check1);
        img_check2 = findViewById(R.id.img_check2);
        img_check3 = findViewById(R.id.img_check3);

        img_icon = findViewById(R.id.img_icon);

        tv_D_day = findViewById(R.id.tv_D_day);

        ed_name = findViewById(R.id.ed_name);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        tv_datePicker = findViewById(R.id.tv_datePicker);
        bt_save = findViewById(R.id.bt_save);
        btn_calendar = findViewById(R.id.btn_calendar);

        damVO = (DamVO) getIntent().getSerializableExtra("DamVO");

        int resource = mContext.getResources().getIdentifier(damVO.DAM_03, "drawable", mContext.getPackageName());
        img_icon.setImageResource(resource);


        ed_name.setText(damVO.getDAM_02());


        if (damVO.getDAM_96().equals("")) {
            tv_datePicker.setText(format.format(calendar.getTime())+getDateofWeek(calendar));
            damVO.setDAM_96(formatDate.format(calendar.getTime()) + formatTime.format(calendar.getTime()));

        } else {
            calendar.set(Calendar.YEAR, Integer.parseInt(damVO.DAM_96.substring(0, 4)));
            calendar.set(Calendar.MONTH, Integer.parseInt(damVO.DAM_96.substring(4, 6)) - 1);
            calendar.set(Calendar.DATE, Integer.parseInt(damVO.DAM_96.substring(6, 8)));

            tv_datePicker.setText(stringTodateFormat(damVO.DAM_96)+getDateofWeek(calendar));
        }

        callBackTime = damVO.DAM_96.substring(8, 12);


        if (damVO.ARM_03.equals("Y")) {
            imageView.setImageResource(R.drawable.alarm_state_on);
        } else {
            imageView.setImageResource(R.drawable.alarm_state_off);
        }

        dCalendar.set(Calendar.YEAR, Integer.parseInt(damVO.DAM_96.substring(0, 4)));
        dCalendar.set(Calendar.MONTH, Integer.parseInt(damVO.DAM_96.substring(4, 6)) - 1);
        dCalendar.set(Calendar.DATE, Integer.parseInt(damVO.DAM_96.substring(6, 8)));

        dCalendar.clear(Calendar.HOUR);
        dCalendar.clear(Calendar.MINUTE);
        dCalendar.clear(Calendar.SECOND);
        dCalendar.clear(Calendar.MILLISECOND); // 시간, 분, 초, 밀리초 초기화


        long dDayDiff;
        int dcount;
        switch (damVO.getDAM_04()) {
            case "1":
                dDayDiff = tCalendar.getTimeInMillis() - dCalendar.getTimeInMillis();
                dcount = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));
                img_check1.setVisibility(View.VISIBLE);
                startCountAnimation(dcount, "1");
                break;
            case "2":
                img_check2.setVisibility(View.VISIBLE);
                dDayDiff = dCalendar.getTimeInMillis() - tCalendar.getTimeInMillis();
                dcount = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));
                startCountAnimation(dcount, "2");
                break;
            case "3":
                img_check3.setVisibility(View.VISIBLE);
                dDayDiff = dCalendar.getTimeInMillis() - tCalendar.getTimeInMillis();
                dcount = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));
                while (dcount < 0){
                    dCalendar.add(Calendar.YEAR, 1);
                    dDayDiff = dCalendar.getTimeInMillis() - tCalendar.getTimeInMillis();
                    dcount = (int) (Math.floor(TimeUnit.HOURS.convert(dDayDiff, TimeUnit.MILLISECONDS) / 24f));
                }
                startCountAnimation(dcount, "3");
                break;
        }

    }

    @Override
    protected void initialize() {

        img_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DamIconDetail.class);
                intent.putExtra("index",Integer.parseInt(damVO.DAM_03.replace("dam_icon_","")));
                mContext.startActivity(intent);

            }
        });

        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_check1.setVisibility(View.VISIBLE);
                img_check2.setVisibility(View.GONE);
                img_check3.setVisibility(View.GONE);
                damVO.setDAM_04("1");
            }
        });

        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_check1.setVisibility(View.GONE);
                img_check2.setVisibility(View.VISIBLE);
                img_check3.setVisibility(View.GONE);
                damVO.setDAM_04("2");
            }
        });

        item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_check1.setVisibility(View.GONE);
                img_check2.setVisibility(View.GONE);
                img_check3.setVisibility(View.VISIBLE);
                damVO.setDAM_04("3");
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (damVO.ARM_03.equals("Y")) {
                    imageView.setImageResource(R.drawable.alarm_state_off);
                    damVO.setARM_03("N");
                } else if (damVO.ARM_03.equals("N")) {
                    imageView.setImageResource(R.drawable.alarm_state_on);
                    damVO.setARM_03("Y");
                }
            }
        });

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        sCalendar.set(year, monthOfYear, dayOfMonth);
                        String monthString = "";
                        String dayOfMonthString = "";
                        if (monthOfYear < 10) {
                            monthString = "0" + String.valueOf(monthOfYear + 1);
                        } else {
                            monthString = String.valueOf(monthOfYear + 1);
                        }
                        if (dayOfMonth < 10) {
                            dayOfMonthString = "0" + String.valueOf(dayOfMonth);
                        } else {
                            dayOfMonthString = String.valueOf(dayOfMonth);
                        }
                        damVO.setDAM_96(year+monthString+dayOfMonthString+callBackTime);
                        tv_datePicker.setText(year + "." + monthString + "." + dayOfMonthString + getDateofWeek(sCalendar));

                    }
                });

        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthString = "";
                String dayOfMonthString = "";
                if (month < 10) {
                    monthString = "0" + String.valueOf(month + 1);
                } else {
                    monthString = String.valueOf(month + 1);
                }
                if (dayOfMonth < 10) {
                    dayOfMonthString = "0" + String.valueOf(dayOfMonth);
                } else {
                    dayOfMonthString = String.valueOf(dayOfMonth);
                }
                sCalendar.set(year, month, dayOfMonth);
                damVO.setDAM_96(year+monthString+dayOfMonthString+callBackTime);
                tv_datePicker.setText(year + "." + monthString + "." + dayOfMonthString + getDateofWeek(sCalendar));

                datePicker.init(sCalendar.get(Calendar.YEAR), sCalendar.get(Calendar.MONTH), sCalendar.get(Calendar.DATE),
                        new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                sCalendar.set(year, monthOfYear, dayOfMonth);

                                String monthString = "";
                                String dayOfMonthString = "";
                                if (monthOfYear < 10) {
                                    monthString = "0" + String.valueOf(monthOfYear + 1);
                                } else {
                                    monthString = String.valueOf(monthOfYear + 1);
                                }
                                if (dayOfMonth < 10) {
                                    dayOfMonthString = "0" + String.valueOf(dayOfMonth);
                                } else {
                                    dayOfMonthString = String.valueOf(dayOfMonth);
                                }
                                damVO.setDAM_96(year+monthString+dayOfMonthString+callBackTime);
                                tv_datePicker.setText(year + "." + monthString + "." + dayOfMonthString + getDateofWeek(sCalendar));

                            }
                        });

            }
        };


        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale locale = getResources().getConfiguration().locale;
                Locale.setDefault(locale);
                DatePickerDialog dialog = new DatePickerDialog(DamDetail.this, callbackMethod, sCalendar.get(Calendar.YEAR), sCalendar.get(Calendar.MONTH), sCalendar.get(Calendar.DATE));
                dialog.show();
            }
        });


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ed_name.getText().equals("")) {
                    Toast.makeText(mContext, getString(R.string.validation_check1), Toast.LENGTH_LONG).show();
                    return;
                }

                damVO.setDAM_03(icon);
                if (getIntent().hasExtra("GUBUN")) {
                    requestDAM_CONTROL("INSERT");
//                    requestLOG_CONTROL("1", getString(R.string.jdm_text6));
                } else {
                    requestDAM_CONTROL("UPDATE");
                }
            }
        });


        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
                AlarmDialog alarmDialog = new AlarmDialog(mContext, callBackTime);

                alarmDialog.setDialogListener(new AlarmDialog.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(String time) {
                        damVO.setDAM_96(damVO.getDAM_96().substring(0, 8) + time);
                        callBackTime = time;
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                alarmDialog.show();
            }
        });

        if (damVO.DAM_97.equals(mUser.Value.OCM_01)) { //작성자만 삭제버튼 보임
            header.btnHeaderRight1.setVisibility((View.VISIBLE));
            header.btnHeaderRight1.setMaxWidth(50);
            header.btnHeaderRight1.setMaxHeight(50);
            header.btnHeaderRight1.setImageResource(R.drawable.btn_cancel); //delete는 왜 크기가 안맞는거야!!! 일단 대체아이콘으로..,,
            header.btnHeaderRight1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    deleteDialog();

                }
            });
        }

    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(view);

        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        EditText etDeleteName = (EditText) view.findViewById(R.id.etDeleteName);

        AlertDialog dialog = builder.create();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (etDeleteName.getText().toString().equals(damVO.DAM_02)) {
                    dialog.dismiss();
                    requestDAM_CONTROL("DELETE");
                } else {
                    Toast.makeText(mActivity, getResources().getString(com.linktag.base.R.string.common_confirm_delete), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void requestLOG_CONTROL(String LOG_03, String LOG_04) {
        //인터넷 연결 여부 확인
        if (!ClsNetworkCheck.isConnectable(mContext)) {
            Toast.makeText(mActivity, getResources().getString(com.linktag.base.R.string.common_network_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Call<LOG_Model> call = Http.log(HttpBaseService.TYPE.POST).LOG_CONTROL(
                BaseConst.URL_HOST,
                "INSERT",
                damVO.DAM_ID,
                damVO.DAM_01,
                "",
                LOG_03,
                LOG_04,
                "",
                mUser.Value.OCM_01,
                "SP_DAML_CONTROL"
        );

        call.enqueue(new Callback<LOG_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<LOG_Model> call, Response<LOG_Model> response) {

            }

            @Override
            public void onFailure(Call<LOG_Model> call, Throwable t) {
                Log.d("LOG_CONTROL", t.getMessage());
//                closeLoadingBar();
            }
        });
    }


    private void startCountAnimation(int count, String GUBUN) {

        DecimalFormat format = new DecimalFormat("###,###");
        ValueAnimator animator;
        if (GUBUN.equals("1")) {
            animator = ValueAnimator.ofInt(0, count + 1); //0 is min number, 600 is max number
        } else {
            animator = ValueAnimator.ofInt(0, count); //0 is min number, 600 is max number
        }
        animator.setDuration(500); //Duration is in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                if (GUBUN.equals("1")) {
                    tv_D_day.setText(format.format(animation.getAnimatedValue()) + "일");
                } else {
                    if (count == 0) {
                        tv_D_day.setText("D-DAY");
                    } else {
                        tv_D_day.setText("D-" + format.format(animation.getAnimatedValue()));
                    }
                }

            }
        });

        animator.start();
    }

    public String stringTodateFormat(String str) {
        String retStr = "";
        //yyyy.MM.dd
        retStr = str.substring(0, 4) + "." + str.substring(4, 6) + "." + str.substring(6, 8);
        return retStr;
    }

    public String getDateofWeek(Calendar calendar){
        String day = "";
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayNum) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;

        }

        return " ("+day+")";

    }


}
