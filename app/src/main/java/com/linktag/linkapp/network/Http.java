package com.linktag.linkapp.network;

import com.linktag.linkapp.model.CFB_Model;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.model.WTH_Model;
import com.linktag.linkapp.model.RUTC_Model;
import com.linktag.linkapp.model.RTSC_Model;
import com.linktag.linkapp.model.LOGIN_Model;
import com.linktag.linkapp.model.BHM_Model;
import com.linktag.linkapp.model.LEDModel;

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


    public static ICMT commute(TYPE type){ return (ICMT) retrofit(ICMT.class, type); }

    public interface ICMT {

        @FormUrlEncoded
        @POST(BaseConst.URL_CMT_SELECT)
        Call<LEDModel> CMT_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "LED_ID") String LED_ID,
                @Field(value = "LED_01") String LED_01,
                @Field(value = "LED_02") String LED_02,
                @Field(value = "LED_04") String LED_04,
                @Field(value = "LED_05") String LED_05,
                @Field(value = "LED_23ST") String LED_23ST,
                @Field(value = "LED_23ED") String LED_23ED
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_CMTL_CONTROL)
        Call<LEDModel> CMTL_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "LED_ID") String LED_ID,
                @Field(value = "LED_01") String LED_01,
                @Field(value = "LED_02") String LED_02,

                @Field(value = "LED_04") String LED_04,
                @Field(value = "LED_05") String LED_05,

                @Field(value = "LED_07") String LED_07,

                @Field(value = "LED_23") String LED_23,
                @Field(value = "LED_25") String LED_25,

                @Field(value = "LED_90") Double LED_90,
                @Field(value = "LED_91") Double LED_91,

                @Field(value = "LED_97") String LED_97,
                @Field(value = "LED_98") String LED_98

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

}