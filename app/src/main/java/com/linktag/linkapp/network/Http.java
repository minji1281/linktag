package com.linktag.linkapp.network;

import com.linktag.linkapp.model.CFB_Model;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.JDMModel;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.model.SVC_Model;
import com.linktag.linkapp.model.WTH_Model;
import com.linktag.linkapp.model.RUTC_Model;
import com.linktag.linkapp.model.RTSC_Model;
import com.linktag.linkapp.model.LOGIN_Model;
import com.linktag.linkapp.model.BHM_Model;
import com.linktag.linkapp.model.DSHModel;
import com.linktag.linkapp.model.BRDModel;
import com.linktag.linkapp.model.NOTModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;


public class Http extends HttpBaseService {

    //--------------------------------------------------
    // 회원
    //--------------------------------------------------
    public static IEMP member(TYPE type) {
        return (IEMP) retrofit(IEMP.class, type);
    }

    public interface IEMP {

        /**
         * 로그인
         *
         * @param host
         * @param GUBUN
         * @param EMP_ID
         * @param EMP_15 폰번호
         * @param EMP_20 비밀번호
         * @return
         */
        @FormUrlEncoded
        @POST(BaseConst.URL_EMPVIEW)
        Call<LOGIN_Model> login(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "EMP_ID") String EMP_ID,
                @Field(value = "EMP_15") String EMP_15,
                @Field(value = "EMP_20") String EMP_20
        );


        @FormUrlEncoded
        @POST(BaseConst.EMP_CONTROL)
        Call<OCM_Model> signUp(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "OCM_ID") String OCM_ID,
                @Field(value = "OCM_01") String OCM_01,
                @Field(value = "OCM_02") String OCM_02,
                @Field(value = "OCM_03") String OCM_03,
                @Field(value = "OCM_21") String OCM_21,
                @Field(value = "OCM_51") String OCM_51,
                @Field(value = "OCM_98") String OCM_98
        );
    }



    //--------------------------------------------------
    // 로그인
    //--------------------------------------------------

    public static ILOGIN login(TYPE type) {
        return (ILOGIN) retrofit(ILOGIN.class, type);
    }

    public interface ILOGIN {
        /**
         * 로그인
         *
         * @param host
         * @param GUBUN
         * @param OCM_03 비밀번호
         * @param OCM_21 메일주소
         * @return
         */
        @FormUrlEncoded
        @POST(BaseConst.URL_LOGIN)
        Call<LOGIN_Model> login(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "OCM_03") String OCM_03,
                @Field(value = "OCM_21") String OCM_21,
                @Field(value = "CTN_01") String CTN_01,
                @Field(value = "TMP1") String TMP1
        );
    }

    //--------------------------------------------------
    // 회원
    //--------------------------------------------------
    public static IOCM ocm(TYPE type) {
        return (IOCM) retrofit(IOCM.class, type);
    }

    public interface IOCM {


        @FormUrlEncoded
        @POST(BaseConst.OCM_CONTROL)
        Call<OCM_Model> OCM_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "OCM_01") String OCM_01,
                @Field(value = "OCM_02") String OCM_02,
                @Field(value = "OCM_03") String OCM_03,
                @Field(value = "OCM_31") String OCM_31,
                @Field(value = "OCM_32") String OCM_32,
                @Field(value = "OCM_51") String OCM_51,
                @Field(value = "OCM_98") String OCM_98
        );

    }

    //--------------------------------------------------
    // 수집
    //--------------------------------------------------
    public static ICFB cfb(TYPE type) {
        return (ICFB) retrofit(ICFB.class, type);
    }

    public interface ICFB {

        @FormUrlEncoded
        @POST(BaseConst.CFB_CONTROL)
        Call<CFB_Model> CFB_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CFB_ID") String CFB_ID,
                @Field(value = "CFB_01") String CFB_01,
                @Field(value = "CFB_02") String CFB_02,
                @Field(value = "CFB_03") String CFB_03,
                @Field(value = "CFB_04") int CFB_04,
                @Field(value = "CFB_05") String CFB_05,
                @Field(value = "CFB_06") String CFB_06,
                @Field(value = "CFB_07") float CFB_07,
                @Field(value = "CFB_08") float CFB_08,
                @Field(value = "CFB_98") String CFB_98

        );

    }

    //--------------------------------------------------
    // 대쉬보드
    //--------------------------------------------------
    public static IDSH commute(TYPE type){ return (IDSH) retrofit(IDSH.class, type); }

    public interface IDSH {

        @FormUrlEncoded
        @POST(BaseConst.URL_DSH_SELECT)
        Call<DSHModel> DSH_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "DSH_ID") String DSH_ID,
                @Field(value = "DSH_01") String DSH_01,
                @Field(value = "DSH_02") String DSH_02,
                @Field(value = "DSH_03") String DSH_03,
                @Field(value = "DSH_04") String DSH_04
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_BRD_SELECT)
        Call<BRDModel> BRD_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "BRD_ID") String BRD_ID,
                @Field(value = "BRD_01") String BRD_01,
                @Field(value = "BRD_02") String BRD_02,
                @Field(value = "BRD_03") String BRD_03,
                @Field(value = "BRD_04") String BRD_04,
                @Field(value = "BRD_06") String BRD_06
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_BRD_CONTROL)
        Call<BRDModel> BRD_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "BRD_ID") String BRD_ID,
                @Field(value = "BRD_01") String BRD_01,
                @Field(value = "BRD_02") String BRD_02,
                @Field(value = "BRD_03") String BRD_03,
                @Field(value = "BRD_04") String BRD_04,
                @Field(value = "BRD_05") String BRD_05,
                @Field(value = "BRD_06") String BRD_06,
                @Field(value = "BRD_98") String BRD_98
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_NOT_SELECT)
        Call<NOTModel> NOT_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "NOT_ID") String NOT_ID,
                @Field(value = "NOT_01") String NOT_01,
                @Field(value = "NOT_02") String NOT_02,
                @Field(value = "NOT_03") String NOT_03,
                @Field(value = "NOT_04") String NOT_04,
                @Field(value = "NOT_06") String NOT_06
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_NOT_CONTROL)
        Call<NOTModel> NOT_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "NOT_ID") String NOT_ID,
                @Field(value = "NOT_01") String NOT_01,
                @Field(value = "NOT_02") String NOT_02,
                @Field(value = "NOT_03") String NOT_03,
                @Field(value = "NOT_04") String NOT_04,
                @Field(value = "NOT_05") String NOT_05,
                @Field(value = "NOT_06") String NOT_06,
                @Field(value = "NOT_98") String NOT_98
        );
    }

    //--------------------------------------------------
    // 메뉴
    //--------------------------------------------------
    public static ISVC svc(TYPE type) {
        return (ISVC) retrofit(ISVC.class, type);
    }

    public interface ISVC {

        @FormUrlEncoded
        @POST(BaseConst.SVC_SELECT)
        Call<SVC_Model> SVC_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "SVC_01") String SVC_01,
                @Field(value = "SVC_02") String SVC_02,
                @Field(value = "SVC_03") String SVC_03,
                @Field(value = "SVC_06") String SVC_06,
                @Field(value = "SVC_07") String SVC_07,
                @Field(value = "SVC_08") String SVC_08,
                @Field(value = "SVC_90") String SVC_90
        );

    }

    public static ICTD ctd(TYPE type) {
        return (ICTD) retrofit(ICTD.class, type);
    }

    public interface ICTD {

        @FormUrlEncoded
        @POST(BaseConst.CTD_SELECT)
        Call<CTD_Model> CTD_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CTD_01") String CTD_01,
                @Field(value = "CTD_02") String CTD_02,
                @Field(value = "OCM_01") String OCM_01

        );

    }

    //--------------------------------------------------
    // 장독
    //--------------------------------------------------
    public static IJDM jdm(TYPE type) {
        return (IJDM) retrofit(IJDM.class, type);
    }

    public interface IJDM {

        @FormUrlEncoded
        @POST(BaseConst.URL_JDM_SELECT)
        Call<JDMModel> JDM_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "JDM_ID") String JDM_ID,
                @Field(value = "JDM_01") String JDM_01,
                @Field(value = "OCM_01") String OCM_01
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_JDM_CONTROL)
        Call<JDMModel> JDM_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "JDM_ID") String JDM_ID,
                @Field(value = "JDM_01") String JDM_01,
                @Field(value = "JDM_02") String JDM_02,
                @Field(value = "JDM_03") String JDM_03,
                @Field(value = "JDM_04") String JDM_04,
                @Field(value = "JDM_96") String JDM_96,
                @Field(value = "JDM_97") String JDM_97,
                @Field(value = "JDM_98") String JDM_98,
                @Field(value = "ARM_03") String ARM_03

        );

    }



    //--------------------------------------------------
    // 버스
    //--------------------------------------------------
    public static IRUTC rutc(TYPE type) {
        return (IRUTC) retrofit(IRUTC.class, type);
    }

    public interface IRUTC {

        @FormUrlEncoded
        @POST(BaseConst.RUTC_SELECT)
        Call<RUTC_Model> RUTC_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "RUTC_ID") String RUTC_ID,     // 회사코드
                @Field(value = "RUTC_03") String RUTC_03,     // 매장코드
                @Field(value = "RUTC_07") String RUTC_07,     // 매장명
                @Field(value = "RUTC_08") String RUTC_08      //
        );

    }

    public static IRTSC rtsc(TYPE type) {
        return (IRTSC) retrofit(IRTSC.class, type);
    }

    public interface IRTSC {

        @FormUrlEncoded
        @POST(BaseConst.RTSC_SELECT)
        Call<RTSC_Model> RTSC_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "RTSC_ID") String RTSC_ID,     // 회사코드
                @Field(value = "RTSC_02") String RTSC_02,     // 매장코드
                @Field(value = "DAY_ST") String DAY_ST,     // 매장명
                @Field(value = "DAY_ED") String DAY_ED      //
        );

    }

    public static IBHM bhm(TYPE type) {return (IBHM) retrofit(IBHM.class, type);
    }

    public interface IBHM {

        @FormUrlEncoded
        @POST(BaseConst.BHM_SELECT)
        Call<BHM_Model> BHM_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "BHM_ID") String BHM_ID,
                @Field(value = "BHM_01") String BHM_01,
                @Field(value = "BHM_02") String BHM_02,
                @Field(value = "BHM_06_ST") String BHM_06_ST,
                @Field(value = "BHM_06_ED") String BHM_06_ED,
                @Field(value = "BHM_09") String BHM_09
        );

    }



    //--------------------------------------------------
    // 날씨
    //--------------------------------------------------
    public static IWTH wth(TYPE type) {
        return (IWTH) retrofit(IWTH.class, type);
    }

    public interface IWTH {

        @FormUrlEncoded
        @POST(BaseConst.WTH_CONTROL)
        Call<WTH_Model> WTH_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "WTH_ID") String WTH_ID,
                @Field(value = "WTH_01") String WTH_01,
                @Field(value = "WTH_02") String WTH_02,
                @Field(value = "WTH_03") String WTH_03,
                @Field(value = "WTH_04") String WTH_04,
                @Field(value = "WTH_05") float WTH_05,
                @Field(value = "WTH_06") float WTH_06,
                @Field(value = "WTH_07") float WTH_07,
                @Field(value = "WTH_08") float WTH_08,
                @Field(value = "WTH_09") float WTH_09

        );

    }


    //--------------------------------------------------
    // 물주기
    //--------------------------------------------------
    public static IPOT pot(TYPE type) {
        return (IPOT) retrofit(IPOT.class, type);
    }

    public interface IPOT {

        @FormUrlEncoded
        @POST(BaseConst.POT_SELECT)
        Call<POT_Model> POT_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "POT_ID") String POT_ID,     // 컨테이너
                @Field(value = "POT_01") String POT_01,     // 코드번호
                @Field(value = "OCM_01") String OCM_01      // 사용자 아이디
        );

        @FormUrlEncoded
        @POST(BaseConst.POT_CONTROL)
        Call<POT_Model> POT_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "POT_ID") String POT_ID,
                @Field(value = "POT_01") String POT_01,
                @Field(value = "POT_02") String POT_02,
                @Field(value = "POT_04") int POT_04,

                @Field(value = "POT_05") String POT_05,
                @Field(value = "POT_06") String POT_06,
                @Field(value = "POT_81") String POT_81,
                @Field(value = "POT_96") String POT_96,
                @Field(value = "POT_98") String POT_98,

                @Field(value = "ARM_03") String ARM_03
        );

    }

}