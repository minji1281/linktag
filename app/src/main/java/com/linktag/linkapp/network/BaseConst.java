package com.linktag.linkapp.network;


public class BaseConst {
    //--------------------------------------------------
    // 공통 - 도메인
    //--------------------------------------------------
    public static String URL_HOST = "http://app.linktag.io";
    //public static String URL_HOST = "https://linktagapp.azurewebsites.net";

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
    public static final String OCM_SELECT = "{host}/Mobile/OCM_SELECT";

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

    // 코드 스캔
    public static final String CDS_CONTROL = "{host}/Mobile/CDS_CONTROL";

    // 메뉴
    public static final String CTD_SELECT = "{host}/Mobile/CTD_SELECT";
    public static final String CTD_CONTROL = "{host}/Mobile/CTD_CONTROL";
    public static final String SVC_SELECT = "{host}/Mobile/SVC_SELECT";

    public static final String CTU_SELECT = "{host}/Mobile/CTU_SELECT";
    public static final String CTU_CONTROL = "{host}/Mobile/CTU_CONTROL";

    public static final String INV_SELECT = "{host}/Mobile/INV_SELECT";
    public static final String INV_CONTROL = "{host}/Mobile/INV_CONTROL";

    // 즐겨찾기
    public static final String BMK_CONTROL = "{host}/Mobile/BMK_CONTROL";

    //알람
    public static final String URL_ARM_CONTROL = "{host}/Mobile/ARM_CONTROL";
    public static final String ARM_SELECT = "{host}/Mobile/ARM_SELECT";

    //장독
    public static final String URL_JDM_SELECT = "{host}/Mobile/JDM_SELECT";
    public static final String URL_JDM_CONTROL = "{host}/Mobile/JDM_CONTROL";

    //복약
    public static final String URL_TRP_SELECT = "{host}/Mobile/TRP_SELECT";
    public static final String URL_TRP_CONTROL = "{host}/Mobile/TRP_CONTROL";
    public static final String URL_TRD_SELECT = "{host}/Mobile/TRD_SELECT";
    public static final String URL_TRD_CONTROL = "{host}/Mobile/TRD_CONTROL";

    //PC
    public static final String URL_PCM_SELECT = "{host}/Mobile/PCM_SELECT";
    public static final String URL_PCM_CONTROL = "{host}/Mobile/PCM_CONTROL";
    public static final String URL_PCD_SELECT = "{host}/Mobile/PCD_SELECT";
    public static final String URL_PCD_CONTROL = "{host}/Mobile/PCD_CONTROL";

    //냉장고
    public static final String URL_RFM_SELECT = "{host}/Mobile/RFM_SELECT";
    public static final String URL_RFM_CONTROL = "{host}/Mobile/RFM_CONTROL";
    public static final String URL_RFD_SELECT = "{host}/Mobile/RFD_SELECT";
    public static final String URL_RFD_CONTROL = "{host}/Mobile/RFD_CONTROL";

    //물주기
    public static final String POT_SELECT = "{host}/Mobile/POT_SELECT";
    public static final String POT_CONTROL = "{host}/Mobile/POT_CONTROL";
//    public static final String POT_CONTROL2 = "{host}/Mobile/POT_CONTROL";

    //필터관리
    public static final String FRM_SELECT = "{host}/Mobile/FRM_SELECT";
    public static final String FRM_CONTROL = "{host}/Mobile/FRM_CONTROL";

    //화장품 유효기간 관리
    public static final String COS_SELECT = "{host}/Mobile/COS_SELECT";
    public static final String COS_CONTROL = "{host}/Mobile/COS_CONTROL";
    public static final String COD_SELECT = "{host}/Mobile/COD_SELECT";
    public static final String COD_CONTROL = "{host}/Mobile/COD_CONTROL";

    //차량 소모품 점검/교체 내역
    public static final String CAR_SELECT = "{host}/Mobile/CAR_SELECT";
    public static final String CAR_CONTROL = "{host}/Mobile/CAR_CONTROL";
    public static final String CAD_SELECT = "{host}/Mobile/CAD_SELECT";
    public static final String CAD_CONTROL = "{host}/Mobile/CAD_CONTROL";


    //이력(log)
    public static final String LOG_CONTROL = "{host}/Mobile/LOG_CONTROL";

    //접종
    public static final String URL_VAC_SELECT = "{host}/Mobile/VAC_SELECT";
    public static final String URL_VAC_CONTROL = "{host}/Mobile/VAC_CONTROL";

    public static final String URL_VAM_SELECT = "{host}/Mobile/VAM_SELECT";
    public static final String URL_VAM_CONTROL = "{host}/Mobile/VAM_CONTROL";

    public static final String URL_VAD_SELECT = "{host}/Mobile/VAD_SELECT";
    public static final String URL_VAD_CONTROL = "{host}/Mobile/VAD_CONTROL";

    //디데이
    public static final String URL_DAM_SELECT = "{host}/Mobile/DAM_SELECT";
    public static final String URL_DAM_CONTROL = "{host}/Mobile/DAM_CONTROL";
    public static final String URL_DCM_SELECT = "{host}/Mobile/DCM_SELECT";
    public static final String URL_DCM_CONTROL = "{host}/Mobile/DCM_CONTROL";

    //무한알람
    public static final String URL_IAM_SELECT = "{host}/Mobile/IAM_SELECT";
    public static final String URL_IAM_CONTROL = "{host}/Mobile/IAM_CONTROL";

    //연습실 예약 관리
    public static final String RMM_SELECT = "{host}/Mobile/RMM_SELECT";
    public static final String RMM_CONTROL = "{host}/Mobile/RMM_CONTROL";
    public static final String RMD_SELECT = "{host}/Mobile/RMD_SELECT";
    public static final String RMD_CONTROL = "{host}/Mobile/RMD_CONTROL";
    public static final String RMR_SELECT = "{host}/Mobile/RMR_SELECT";
    public static final String RMR_CONTROL = "{host}/Mobile/RMR_CONTROL";

    //투표 관리
    public static final String VOT_SELECT = "{host}/Mobile/VOT_SELECT";
    public static final String VOT_CONTROL = "{host}/Mobile/VOT_CONTROL";
    public static final String VIT_SELECT = "{host}/Mobile/VIT_SELECT";
    public static final String VIT_CONTROL = "{host}/Mobile/VIT_CONTROL";
    public static final String VIC_SELECT = "{host}/Mobile/VIC_SELECT";
    public static final String VIC_CONTROL = "{host}/Mobile/VIC_CONTROL";

}
