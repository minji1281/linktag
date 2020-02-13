package com.linktag.linkapp.network;

import com.linktag.linkapp.model.BRCModel;
import com.linktag.linkapp.model.CMTModel;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.JDMModel;
import com.linktag.linkapp.model.NOCModel;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.model.SVC_Model;
import com.linktag.linkapp.model.LOGIN_Model;
import com.linktag.linkapp.model.DSHModel;
import com.linktag.linkapp.model.BRDModel;
import com.linktag.linkapp.model.NOTModel;
import com.linktag.linkapp.model.TKNModel;

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
        @POST(BaseConst.URL_CMT_SELECT)
        Call<CMTModel> CMT_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CMT_ID") String CMT_ID,
                @Field(value = "CMT_01") String CMT_01,
                @Field(value = "CMT_02") String CMT_02,
                @Field(value = "CMT_03") String CMT_03,
                @Field(value = "CMT_98") String CMT_98
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
        @POST(BaseConst.URL_BRC_SELECT)
        Call<BRCModel> BRC_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "BRC_ID") String BRC_ID,
                @Field(value = "BRC_01") String BRC_01,
                @Field(value = "BRC_02") String BRC_02,
                @Field(value = "BRC_03") String BRC_03,
                @Field(value = "BRC_98") String BRC_98
        );



        @FormUrlEncoded
        @POST(BaseConst.URL_BRC_CONTROL)
        Call<BRCModel> BRC_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "BRC_ID") String BRC_ID,
                @Field(value = "BRC_01") String BRC_01,
                @Field(value = "BRC_02") String BRC_02,
                @Field(value = "BRC_03") String BRC_03,
                @Field(value = "BRC_98") String BRC_98
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


        @FormUrlEncoded
        @POST(BaseConst.URL_NOC_SELECT)
        Call<NOCModel> NOC_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "NOC_ID") String NOC_ID,
                @Field(value = "NOC_01") String NOC_01,
                @Field(value = "NOC_02") String NOC_02,
                @Field(value = "NOC_03") String NOC_03,
                @Field(value = "NOC_98") String NOC_98
        );



        @FormUrlEncoded
        @POST(BaseConst.URL_NOC_CONTROL)
        Call<NOCModel> NOC_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "NOC_ID") String NOC_ID,
                @Field(value = "NOC_01") String NOC_01,
                @Field(value = "NOC_02") String NOC_02,
                @Field(value = "NOC_03") String NOC_03,
                @Field(value = "NOC_98") String NOC_98
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

        @FormUrlEncoded
        @POST(BaseConst.CTD_CONTROL)
        Call<CTD_Model> CTD_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CTD_01") String CTD_01,
                @Field(value = "CTD_02") String CTD_02,
                @Field(value = "CTD_03") String CTD_03,
                @Field(value = "CTD_04") String CTD_04,
                @Field(value = "CTD_05") String CTD_05,
                @Field(value = "CTD_06") double CTD_06,
                @Field(value = "CTD_07") String CTD_07,
                @Field(value = "CTD_08") String CTD_08,
                @Field(value = "CTD_09") String CTD_09,
                @Field(value = "CTD_10") String CTD_10,
                @Field(value = "CTD_97") String CTD_97,
                @Field(value = "CTD_98") String CTD_98
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

        //이미지 테스트용...
//        @Multipart
//        @FormUrlEncoded
//        @POST(BaseConst.POT_CONTROL2)
//        Call<POT_Model> POT_CONTROL2(
//                @Path(value = "host", encoded = true) String host,
//                @Field(value = "GUBUN") String GUBUN,
//                @Field(value = "POT_ID") String POT_ID,
//                @Field(value = "POT_01") String POT_01,
//                @Field(value = "POT_02") String POT_02,
//                @Field(value = "POT_04") int POT_04,
//
//                @Field(value = "POT_05") String POT_05,
//                @Field(value = "POT_06") String POT_06,
//                @Field(value = "POT_81") String POT_81,
//                @Field(value = "POT_96") String POT_96,
//                @Field(value = "POT_98") String POT_98,
//
//                @Field(value = "ARM_03") String ARM_03,
//                @Part("POT_81_F\"; filename=\"pp.png\" ") RequestBody POT_81_F
//        );

    }


    //--------------------------------------------------
    // 대쉬보드
    //--------------------------------------------------
    public static IPSH push(TYPE type){ return (IPSH) retrofit(IPSH.class, type); }

    public interface IPSH {

        @FormUrlEncoded
        @POST(BaseConst.URL_TKN_SELECT)
        Call<TKNModel> TKN_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "TKN_ID") String TKN_ID,
                @Field(value = "TKN_01") String TKN_01,
                @Field(value = "TKN_02") String TKN_02,
                @Field(value = "TKN_03") String TKN_03,
                @Field(value = "TKN_04") String TKN_04
        );

        
        @FormUrlEncoded
        @POST(BaseConst.URL_TKN_CONTROL)
        Call<TKNModel> TKN_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "TKN_ID") String TKN_ID,
                @Field(value = "TKN_01") String TKN_01,
                @Field(value = "TKN_02") String TKN_02,
                @Field(value = "TKN_03") String TKN_03,
                @Field(value = "TKN_04") String TKN_04,
                @Field(value = "TKN_98") String TKN_98
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_TKN_CALL)
        Call<TKNModel> NotifyAsync(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "to") String to,
                @Field(value = "title") String title,
                @Field(value = "body") String body
        );



    }

}