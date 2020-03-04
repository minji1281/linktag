package com.linktag.linkapp.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.linktag.base.base_fragment.BaseFragment;
import com.linktag.base.network.ClsNetworkCheck;
import com.linktag.base.util.BaseAlert;
import com.linktag.base.util.ClsDateTime;
import com.linktag.linkapp.R;
import com.linktag.linkapp.decorators.EventDecorator;
import com.linktag.linkapp.decorators.OneDayDecorator;
import com.linktag.linkapp.decorators.SaturdayDecorator;
import com.linktag.linkapp.decorators.SundayDecorator;
import com.linktag.linkapp.model.ARM_Model;
import com.linktag.linkapp.network.BaseConst;
import com.linktag.linkapp.network.Http;
import com.linktag.linkapp.network.HttpBaseService;
import com.linktag.linkapp.ui.calendar.CalendarAdapter;
import com.linktag.linkapp.ui.spinner.SpinnerList;
import com.linktag.linkapp.value_object.ARM_VO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.format.DateFormatTitleFormatter;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends BaseFragment {
    //===================================
    // Layout//===================================
    private View view;
    private ListView listView;
    private SwipeRefreshLayout swipeRefresh;

    private String NowDate;
    private String NowYYmm;


    //===================================
    // Variable
    //===================================
    private CalendarAdapter mAdapter;
    private ArrayList<ARM_VO> mList;



    String time,kcal,menu;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    Cursor cursor;
    MaterialCalendarView materialCalendarView;


    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NowDate = ClsDateTime.getNow("yyyyMM");
        NowYYmm = ClsDateTime.getNow("yyyyMM");
    }

    String NowToday = ClsDateTime.getNow("yyyyMM");
    private static final SimpleDateFormat YYYYM_FORMAT = new SimpleDateFormat("yyyy년 M월");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        materialCalendarView = view.findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2015, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2040, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        materialCalendarView.setPagingEnabled(false);
        materialCalendarView.setShowOtherDates(MaterialCalendarView.SHOW_OUT_OF_RANGE);
        materialCalendarView.setTitleFormatter(new DateFormatTitleFormatter(YYYYM_FORMAT));
        materialCalendarView.setHeaderTextAppearance(R.style.CalendarWidgetHeader);



        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");

                String shot_Day = Year  + String.format("%02d", Month)  ;
                NowDate = shot_Day;
                NowYYmm = shot_Day;
                Log.i("shot_Day test", shot_Day + "");
                materialCalendarView.clearSelection();

                requestARM_SELECT(shot_Day); // 여기 추가
            }
        });



        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                oneDayDecorator);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");

                String shot_Day = Year  + String.format("%02d", Month) +  String.format("%02d", Day) ;
                NowDate = shot_Day;
                materialCalendarView.clearSelection();

                requestARM_SELECT(NowDate); // 여기 추가
            }
        });

        materialCalendarView.setOnTitleClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //  Toast.makeText(mActivity, NowDate , Toast.LENGTH_SHORT).show();
                materialCalendarView.clearSelection();
                requestARM_SELECT(NowYYmm); // 여기 추가
            }
        });


        initLayout();

        initialize();


        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        requestARM_SELECT(NowYYmm);
    }

    private void initLayout() {
        listView = view.findViewById(R.id.listCalen);
        //   listView.setOnItemClickListener((parent, view, position, id) -> goWorkRecord(position));

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> requestARM_SELECT(NowYYmm));
    }

    protected void initialize(){
        mList = new ArrayList<>();

        mAdapter = new CalendarAdapter(mContext, mList);
        listView.setAdapter(mAdapter);
    }

//    private void goWorkRecord(int position) {
//        Intent intent = new Intent(mContext, BoardMain.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent.putExtra("DSH_GB", mList.get(position).DSH_GB);
////        intent.putExtra(BoardMain.WORK_STATE, mList.get(position));
//        mContext.startActivity(intent);
//    }


    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;


        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            System.out.println("##############222222222 "+ Arrays.toString(Time_Result)); // 이렇게 하면 [1.0, 1.1, 1.2] 이 출력됩니다.

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();
            dates.clear();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환


                for(int i = 0 ; i < Time_Result.length ; i ++){
                    String[] time = Time_Result[i].split(",");
                    int year = Integer.parseInt(time[0]);
                    int month = Integer.parseInt(time[1]);
                    int dayy = Integer.parseInt(time[2]);
                    //System.out.println("##############33333333333 "+ day); // 이렇게 하면 [1.0, 1.1, 1.2] 이 출력됩니다.

//                    System.out.println("year :"+year+" m :"+month+" d :"+dayy);
                    CalendarDay day = CalendarDay.from(year, month-1, dayy);  // 기본 오늘 날짜를 하나 가져가서

                    dates.add(day);
                    calendar.set(year,month-1,dayy);

//                    System.out.println("##############444444444 "+ calendar); // 이렇게 하면 [1.0, 1.1, 1.2] 이 출력됩니다.
                }
//            System.out.println("##############5555555555 "+ dates); // 이렇게 하면 [1.0, 1.1, 1.2] 이 출력됩니다.
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays));
        }
    }


    public void requestARM_SELECT(String getDate) {
        // 인터넷 연결 여부 확인
        if(!ClsNetworkCheck.isConnectable(mContext)){
            BaseAlert.show(getString(R.string.common_network_error));
            return;
        }

        openLoadingBar();

        String strToday = ClsDateTime.getNow("yyyyMMdd");

        if(getDate.equals("")){ getDate =  strToday;  }


        Call<ARM_Model> call = Http.arm(HttpBaseService.TYPE.POST).ARM_SELECT(
                BaseConst.URL_HOST,
                "CAL_LIST",
                mUser.Value.CTM_01,
                "",
                mUser.Value.OCM_01,
                "Y",
                "",
                "",
                "",
                getDate, //선택날짜
                "",
                "",
                "",
                ""
        );

        call.enqueue(new Callback<ARM_Model>() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(Call<ARM_Model> call, Response<ARM_Model> response) {
                Message msg = new Message();
                msg.obj = response;
                msg.what = 100;

                new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if(msg.what == 100){
                            closeLoadingBar();

                            Response<ARM_Model> response = (Response<ARM_Model>) msg.obj;

                            mList = response.body().Data;
                            if(mList == null)
                                mList = new ArrayList<>();

                            String[] DateArray = new String[response.body().Total];
                            if(response.body().Total > 0){
                                for(int i = 0; i < response.body().Total; i++){
                                    //    carList.add(new SpinnerDate(response.body().Data.get(i).ARM_92.substring(0,4)+","+response.body().Data.get(i).ARM_92.substring(4,2)+","+response.body().Data.get(i).ARM_92.substring(6,2)));

                                    // ar[i] = response.body().Data.get(i).ARM_92.substring(0,4)+","+response.body().Data.get(i).ARM_92.substring(4,2)+","+response.body().Data.get(i).ARM_92.substring(6,2);
                                    //    DateArray[i] = response.body().Data.get(i).ARM_92;
                                    //   DateArray[i] = response.body().Data.get(i).ARM_92.substring(4,6);
                                    DateArray[i] = response.body().Data.get(i).ARM_92.substring(0,4)+","+response.body().Data.get(i).ARM_92.substring(4,6)+","+response.body().Data.get(i).ARM_92.substring(6,8);
                                }
                            }
//
                            //     new ApiSimulator(ar).executeOnExecutor(Executors.newSingleThreadExecutor());

//                            System.out.println("##############1111111111 "+ Arrays.toString(DateArray)); // 이렇게 하면 [1.0, 1.1, 1.2] 이 출력됩니다.
                            new ApiSimulator(DateArray).executeOnExecutor(Executors.newSingleThreadExecutor());

                            mAdapter.updateData(mList);
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);

                        }
                    }
                }.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ARM_Model> call, Throwable t) {
                Log.d("Test", t.getMessage());
                closeLoadingBar();

            }
        });


    }


}
