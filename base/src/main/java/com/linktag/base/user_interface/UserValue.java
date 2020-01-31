package com.linktag.base.user_interface;

import java.util.Calendar;
import java.util.Date;

public class UserValue {
    public String CTM_01;
    public String OCP_ID;
    public String CTU_08;
    public String CTU_09;

    public String OCM_01;
    public String OCM_02;
    public String OCM_03;
    public String OCM_21;
    public String OCM_25;
    public String OCM_28;
    public String OCM_29;
    public String OCM_31;
    public String OCM_32;
    public String OCM_51;
    public String OCM_03_CHK;

    public String RUTC_01;   // 노선 코드
    public String RUTC_03;   // 노선명
    public String RUTC_04;   // 기점
    public String RUTC_05;   // 종점

    public String RUTC_ST;   // 매장 코드
    public String OCP_01;    //회사
    public String OCP_ADD;    //회사 주소


    public Calendar WeatherTime;   // 날씨 API호출용 시간 저장

    public boolean WeatherTimeCHK = false;

    public Calendar ScanTime;   // 날씨 API호출용 시간 저장
    public String DSH_GB;

}
