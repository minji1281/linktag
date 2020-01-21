package com.linktag.base.settings;

public class SettingsValue {
    /**
     * 로그인 아이디
     */
    public String LoginID;
    /**
     * 로그인 비번
     */
    public String LoginPW;
    /**
     * 아이디 저장 여부
     */
    public boolean AutoID;
    /**
     * 자동 로그인 여부
     */
    public boolean AutoLogin;

    //=======================
    // Location 관련
    //=======================
    // 이산소프트 35.234324 / 128.686369
    /**
     * 위도
     * 서버에서 받아서 처리하도록 변경 필요
     */
    public String WorkLatitude = "37.474322";
    /**
     * 경도
     * 서버에서 받아서 처리하도록 변경 필요
     */
    public String WorkLongitude = "127.054382";

    /**
     * 금일 출근 여부 - 출근 시 오늘 날짜 부여
     */
    public String WorkStart = "";
    /**
     * 금일 퇴근 여부 - 퇴퇴근 시오늘 날짜 부여
     */
    public String WorkEnd = "";
    /**
     *
     */
    public int GPSCount = 0;
    /**
     * GPS Time
     */
    public long GPSTime = 0;

}
