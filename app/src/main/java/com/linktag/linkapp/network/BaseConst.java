package com.linktag.linkapp.network;


public class BaseConst {

    //--------------------------------------------------
    // 공통 - 도메인
    //--------------------------------------------------
    public static String URL_HOST = "http://app.linktag.io";

    //토큰발행
    public static final String URL_TKN_SELECT = "{host}/Mobile/TKN_SELECT";
    public static final String URL_TKN_CONTROL = "{host}/Mobile/TKN_CONTROL";
    public static final String URL_TKN_CALL = "{host}/Mobile/NotifyAsync";

    /**
     * 가입
     */
    public static final String EMP_CONTROL = "{host}/Mobile/EMP_CONTROL";
    //--------------------------------------------------

    public static final String URL_LOGIN = "{host}/Mobile/M_LOGIN";

    public static final String OCM_CONTROL = "{host}/Mobile/OCM_CONTROL";

    //대쉬보드
    public static final String URL_DSH_SELECT = "{host}/Mobile/DSH_SELECT";
    public static final String URL_CMT_SELECT = "{host}/Mobile/CMT_SELECT";
    public static final String URL_BRD_SELECT = "{host}/Mobile/BRD_SELECT";
    public static final String URL_BRD_CONTROL = "{host}/Mobile/BRD_CONTROL";
    public static final String URL_NOT_SELECT = "{host}/Mobile/NOT_SELECT";
    public static final String URL_NOT_CONTROL = "{host}/Mobile/NOT_CONTROL";

    public static final String URL_BRC_SELECT = "{host}/Mobile/BRC_SELECT";
    public static final String URL_BRC_CONTROL = "{host}/Mobile/BRC_CONTROL";

    public static final String URL_NOC_SELECT = "{host}/Mobile/NOC_SELECT";
    public static final String URL_NOC_CONTROL = "{host}/Mobile/NOC_CONTROL";

//    public static final String URL_CMT_CONTROL = "{host}/Mobile/CMT_CONTROL";
//    public static final String URL_CMTL_CONTROL = "{host}/Mobile/CMTL_CONTROL";

    // 메뉴
    public static final String CTD_SELECT = "{host}/Mobile/CTD_SELECT";
    public static final String CTD_CONTROL = "{host}/Mobile/CTD_CONTROL";
    public static final String SVC_SELECT = "{host}/Mobile/SVC_SELECT";

    //장독
    public static final String URL_JDM_SELECT = "{host}/Mobile/JDM_SELECT";
    public static final String URL_JDM_CONTROL = "{host}/Mobile/JDM_CONTROL";

    //물주기
    public static final String POT_SELECT = "{host}/Mobile/POT_SELECT";
    public static final String POT_CONTROL = "{host}/Mobile/POT_CONTROL";

}
