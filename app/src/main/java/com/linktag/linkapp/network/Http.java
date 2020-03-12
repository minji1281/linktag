package com.linktag.linkapp.network;

import com.linktag.linkapp.model.ARMModel;
import com.linktag.linkapp.model.BRCModel;
import com.linktag.linkapp.model.CADModel;
import com.linktag.linkapp.model.CARModel;
import com.linktag.linkapp.model.CMTModel;
import com.linktag.linkapp.model.CODModel;
import com.linktag.linkapp.model.COSModel;
import com.linktag.linkapp.model.CTDS_Model;
import com.linktag.linkapp.model.CTD_Model;
import com.linktag.linkapp.model.CTU_Model;
import com.linktag.linkapp.model.FRMModel;
import com.linktag.linkapp.model.JDMModel;
import com.linktag.linkapp.model.NOCModel;
import com.linktag.linkapp.model.OCM_Model;
import com.linktag.linkapp.model.PCDModel;
import com.linktag.linkapp.model.PCMModel;
import com.linktag.linkapp.model.POT_Model;
import com.linktag.linkapp.model.RFDModel;
import com.linktag.linkapp.model.RFMModel;
import com.linktag.linkapp.model.SVC_Model;
import com.linktag.linkapp.model.LOGIN_Model;
import com.linktag.linkapp.model.DSHModel;
import com.linktag.linkapp.model.BRDModel;
import com.linktag.linkapp.model.NOTModel;
import com.linktag.linkapp.model.TKNModel;
import com.linktag.linkapp.model.TRDModel;
import com.linktag.linkapp.model.TRPModel;
import com.linktag.linkapp.model.ARM_Model;

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
                @Field(value = "OCM_24") String OCM_24,
                @Field(value = "OCM_51") String OCM_51,
                @Field(value = "OCM_52") String OCM_52,
                @Field(value = "OCM_98") String OCM_98
        );

        @FormUrlEncoded
        @POST(BaseConst.OCM_SELECT)
        Call<OCM_Model> OCM_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "OCM_01") String OCM_01,
                @Field(value = "OCM_02") String OCM_02,
                @Field(value = "OCM_21") String OCM_21,
                @Field(value = "CTM_01") String CTM_01
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
    // 코드 스캔
    //--------------------------------------------------
    public static ICTDS ctds(TYPE type) {
        return (ICTDS) retrofit(ICTDS.class, type);
    }

    public interface ICTDS {
        @FormUrlEncoded
        @POST(BaseConst.CTDS_SELECT)
        Call<CTDS_Model> CTDS_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CTDS_01") String CTDS_01,
                @Field(value = "CTDS_02") String CTDS_02,
                @Field(value = "CTDS_03") String CTDS_03
        );

        @FormUrlEncoded
        @POST(BaseConst.CTDS_CONTROL)
        Call<CTDS_Model> CTDS_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CTDS_01") String CTDS_01,
                @Field(value = "CTDS_02") String CTDS_02,
                @Field(value = "CTDS_03") String CTDS_03,
                @Field(value = "CTDS_04") String CTDS_04,
                @Field(value = "CTDS_98") String CTDS_98
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
                @Field(value = "OCM_01") String OCM_01,
                @Field(value = "CTDS_03") String CTDS_03

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

    public static ICTU ctu(TYPE type) { return (ICTU) retrofit(ICTU.class, type); }

    public interface ICTU {

        @FormUrlEncoded
        @POST(BaseConst.CTU_SELECT)
        Call<CTU_Model> CTU_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CTU_01") String CTU_01,
                @Field(value = "CTU_02") String CTU_02,
                @Field(value = "CTU_03") String CTU_03

        );

        @FormUrlEncoded
        @POST(BaseConst.CTU_CONTROL)
        Call<CTU_Model> CTU_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CTU_01") String CTU_01,
                @Field(value = "CTU_02") String CTU_02,
                @Field(value = "CTU_03") String CTU_03,
                @Field(value = "CTU_04") String CTU_04,
                @Field(value = "CTU_05") String CTU_05,

                @Field(value = "CTU_07") String CTU_07,
                @Field(value = "CTU_08") String CTU_08,
                @Field(value = "CTU_09") String CTU_09,

                @Field(value = "CTU_10") double CTU_10,
                @Field(value = "CTU_11") double CTU_11,
                @Field(value = "CTU_12") double CTU_12,
                @Field(value = "CTU_13") double CTU_13,

                @Field(value = "CTU_97") String CTU_97,
                @Field(value = "CTU_98") String CTU_98
        );
    }

    public static IBMK bmk(TYPE type) { return (IBMK) retrofit(IBMK.class, type); }

    public interface IBMK {
        @FormUrlEncoded
        @POST(BaseConst.BMK_CONTROL)
        Call<CTD_Model> BMK_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "BMK_01") String BMK_01,
                @Field(value = "BMK_02") String BMK_02,
                @Field(value = "BMK_03") String BMK_03,
                @Field(value = "BMK_04") String BMK_04,

                @Field(value = "BMK_98") String BMK_98
        );
    }

    // 사용자 알람
    //--------------------------------------------------

    public static IARM arm(TYPE type) {
        return (IARM) retrofit(IARM.class, type);
    }

    public interface IARM {

        @FormUrlEncoded
        @POST(BaseConst.URL_ARM_CONTROL)
        Call<ARMModel> ARM_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "ARM_ID") String ARM_ID,
                @Field(value = "ARM_01") String ARM_01,
                @Field(value = "ARM_0101") String ARM_0101,
                @Field(value = "ARM_02") String ARM_02,
                @Field(value = "ARM_03") String ARM_03,
                @Field(value = "ARM_90") String ARM_90,
                @Field(value = "ARM_91") String ARM_91,
                @Field(value = "ARM_92") String ARM_92,
                @Field(value = "ARM_93") String ARM_93,
                @Field(value = "ARM_94") String ARM_94,
                @Field(value = "ARM_95") String ARM_95,
                @Field(value = "ARM_98") String ARM_98

        );

        @FormUrlEncoded
        @POST(BaseConst.ARM_SELECT)
        Call<ARM_Model> ARM_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "ARM_ID") String ARM_ID,
                @Field(value = "ARM_01") String ARM_01,
                @Field(value = "ARM_02") String ARM_02,
                @Field(value = "ARM_03") String ARM_03,
                @Field(value = "ARM_04") String ARM_04,
                @Field(value = "ARM_90") String ARM_90,
                @Field(value = "ARM_91") String ARM_91,
                @Field(value = "ARM_92") String ARM_92,
                @Field(value = "ARM_93") String ARM_93,
                @Field(value = "ARM_94") String ARM_94,
                @Field(value = "ARM_95") String ARM_95,
                @Field(value = "ARM_98") String ARM_98

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
                @Field(value = "JDM_05") String JDM_05,
                @Field(value = "JDM_06") String JDM_06,
                @Field(value = "JDM_07") String JDM_07,
                @Field(value = "JDM_08") String JDM_08,
                @Field(value = "JDM_96") String JDM_96,
                @Field(value = "JDM_97") String JDM_97,
                @Field(value = "JDM_98") String JDM_98,
                @Field(value = "ARM_03") String ARM_03

        );

    }

    //--------------------------------------------------
    // PC
    //--------------------------------------------------
    public static IPCM pcm(TYPE type) {
        return (IPCM) retrofit(IPCM.class, type);
    }

    public interface IPCM {

        @FormUrlEncoded
        @POST(BaseConst.URL_PCM_SELECT)
        Call<PCMModel> PCM_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "PCM_ID") String PCM_ID,
                @Field(value = "PCM_01") String PCM_01,
                @Field(value = "OCM_01") String OCM_01
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_PCM_CONTROL)
        Call<PCMModel> PCM_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "PCM_ID") String PCM_ID,
                @Field(value = "PCM_01") String PCM_01,
                @Field(value = "PCM_02") String PCM_02,
                @Field(value = "PCM_03") String PCM_03,
                @Field(value = "PCM_04") String PCM_04,
                @Field(value = "PCM_96") String PCM_96,
                @Field(value = "PCM_97") String PCM_97,
                @Field(value = "PCM_98") String PCM_98,
                @Field(value = "ARM_03") String ARM_03

        );


        @FormUrlEncoded
        @POST(BaseConst.URL_PCD_SELECT)
        Call<PCDModel> PCD_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "PCD_ID") String PCD_ID,
                @Field(value = "PCD_01") String PCD_01,
                @Field(value = "OCM_01") String OCM_01
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_PCD_CONTROL)
        Call<PCDModel> PCD_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "PCD_ID") String PCD_ID,
                @Field(value = "PCD_01") String PCD_01,
                @Field(value = "PCD_02") String PCD_02,
                @Field(value = "PCD_03") String PCD_03,
                @Field(value = "PCD_04") String PCD_04,
                @Field(value = "PCD_05") String PCD_05,
                @Field(value = "PCD_98") String PCD_98

        );

    }
    public static IPCD pcd(TYPE type) {
        return (IPCD) retrofit(IPCD.class, type);
    }

    public interface IPCD {


        @FormUrlEncoded
        @POST(BaseConst.URL_PCD_SELECT)
        Call<PCDModel> PCD_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "PCD_ID") String PCD_ID,
                @Field(value = "PCD_01") String PCD_01,
                @Field(value = "OCM_01") String OCM_01
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_PCD_CONTROL)
        Call<PCDModel> PCD_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "PCD_ID") String PCD_ID,
                @Field(value = "PCD_01") String PCD_01,
                @Field(value = "PCD_02") String PCD_02,
                @Field(value = "PCD_03") String PCD_03,
                @Field(value = "PCD_04") String PCD_04,
                @Field(value = "PCD_05") String PCD_05,
                @Field(value = "PCD_98") String PCD_98

        );

    }

    //--------------------------------------------------
    // 복약
    //--------------------------------------------------
    public static ITRP trp(TYPE type) {
        return (ITRP) retrofit(ITRP.class, type);
    }

    public interface ITRP {

        @FormUrlEncoded
        @POST(BaseConst.URL_TRP_SELECT)
        Call<TRPModel> TRP_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "TRP_ID") String TRP_ID,
                @Field(value = "TRP_01") String TRP_01,
                @Field(value = "OCM_01") String OCM_01
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_TRP_CONTROL)
        Call<TRPModel> TRP_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "TRP_ID") String TRP_ID,
                @Field(value = "TRP_01") String TRP_01,
                @Field(value = "TRP_02") String TRP_02,
                @Field(value = "TRP_03") String TRP_03,
                @Field(value = "TRP_04") String TRP_04,
                @Field(value = "TRP_05") String TRP_05,
                @Field(value = "TRP_06") String TRP_06,
                @Field(value = "TRP_07") String TRP_07,
                @Field(value = "TRP_97") String TRP_97,
                @Field(value = "TRP_98") String TRP_98,
                @Field(value = "ARM_03") String ARM_03

        );

        @FormUrlEncoded
        @POST(BaseConst.URL_TRD_CONTROL)
        Call<TRPModel> TRD_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "TRP_ID") String TRP_ID,
                @Field(value = "TRP_01") String TRP_01,
                @Field(value = "TRP_02") String TRP_02,
                @Field(value = "TRP_96") String TRP_96,
                @Field(value = "TRP_98") String TRP_98
        );


    }

    public static ITRD trd(TYPE type) {
        return (ITRD) retrofit(ITRD.class, type);
    }

    public interface ITRD {

        @FormUrlEncoded
        @POST(BaseConst.URL_TRD_SELECT)
        Call<TRDModel> TRD_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "TRD_ID") String TRD_ID,
                @Field(value = "TRD_01") String TRD_01,
                @Field(value = "OCM_01") String OCM_01
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_TRD_CONTROL)
        Call<TRDModel> TRD_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "TRD_ID") String TRD_ID,
                @Field(value = "TRD_01") String TRD_01,
                @Field(value = "TRD_02") String TRD_02,
                @Field(value = "TRD_96") String TRD_96,
                @Field(value = "TRD_98") String TRD_98,

                @Field(value = "TRP_02") String TRP_02,
                @Field(value = "TRP_03") String TRP_03,
                @Field(value = "TRP_04") String TRP_04,
                @Field(value = "ARM_03") String ARM_03
        );

    }


    //--------------------------------------------------
    // 냉장고
    //--------------------------------------------------
    public static IRFM rfm(TYPE type) {
        return (IRFM) retrofit(IRFM.class, type);
    }

    public interface IRFM {

        @FormUrlEncoded
        @POST(BaseConst.URL_RFM_SELECT)
        Call<RFMModel> RFM_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "RFM_ID") String RFM_ID,
                @Field(value = "RFM_01") String RFM_01,
                @Field(value = "OCM_01") String OCM_01
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_RFM_CONTROL)
        Call<RFMModel> RFM_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "RFM_ID") String RFM_ID,
                @Field(value = "RFM_01") String RFM_01,
                @Field(value = "RFM_02") String RFM_02,
                @Field(value = "RFM_03") String RFM_03,
                @Field(value = "RFM_97") String RFM_97,
                @Field(value = "RFM_98") String RFM_98

        );

    }

    public static IRFD rfd(TYPE type) {
        return (IRFD) retrofit(IRFD.class, type);
    }

    public interface IRFD {

        @FormUrlEncoded
        @POST(BaseConst.URL_RFD_SELECT)
        Call<RFDModel> RFD_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "RFD_ID") String RFD_ID,
                @Field(value = "RFD_01") String RFD_01,
                @Field(value = "OCM_01") String OCM_01
        );

        @FormUrlEncoded
        @POST(BaseConst.URL_RFD_CONTROL)
        Call<RFDModel> RFD_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "RFD_ID") String RFD_ID,
                @Field(value = "RFD_01") String RFD_01,
                @Field(value = "RFD_02") String RFD_02,
                @Field(value = "RFD_03") String RFD_03,
                @Field(value = "RFD_04") String RFD_04,
                @Field(value = "RFD_05") String RFD_05,
                @Field(value = "RFD_06") String RFD_06,
                @Field(value = "RFD_96") String RFD_96,
                @Field(value = "RFD_98") String RFD_98,
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
                @Field(value = "POT_02") String POT_02,     // 명칭
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
                @Field(value = "POT_03") String POT_03,

                @Field(value = "POT_04") int POT_04,
                @Field(value = "POT_05") String POT_05,
                @Field(value = "POT_06") String POT_06,
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
//                @Part("POT_81_F") MultipartBody.Part POT_81_F
//        );

    }

    //--------------------------------------------------
    // 필터관리
    //--------------------------------------------------
    public static IFRM frm(TYPE type) {
        return (IFRM) retrofit(IFRM.class, type);
    }

    public interface IFRM {

        @FormUrlEncoded
        @POST(BaseConst.FRM_SELECT)
        Call<FRMModel> FRM_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "FRM_ID") String FRM_ID,     // 컨테이너
                @Field(value = "FRM_01") String FRM_01,     // 코드번호
                @Field(value = "FRM_02") String FRM_02,     // 명칭
                @Field(value = "OCM_01") String OCM_01      // 사용자 아이디
        );

        @FormUrlEncoded
        @POST(BaseConst.FRM_CONTROL)
        Call<FRMModel> FRM_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "FRM_ID") String FRM_ID,
                @Field(value = "FRM_01") String FRM_01,
                @Field(value = "FRM_02") String FRM_02,
                @Field(value = "FRM_03") String FRM_03,

                @Field(value = "FRM_04") int FRM_04,
                @Field(value = "FRM_05") String FRM_05,
                @Field(value = "FRM_06") String FRM_06,
                @Field(value = "FRM_96") String FRM_96,
                @Field(value = "FRM_98") String FRM_98,

                @Field(value = "ARM_03") String ARM_03
        );

    }

    //--------------------------------------------------
    // 화장품 유효기간 관리
    //--------------------------------------------------
    public static ICOS cos(TYPE type) {
        return (ICOS) retrofit(ICOS.class, type);
    }

    public interface ICOS {

        @FormUrlEncoded
        @POST(BaseConst.COS_SELECT)
        Call<COSModel> COS_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "COS_ID") String COS_ID,     // 컨테이너
                @Field(value = "COS_01") String COS_01      // 코드번호
        );

        @FormUrlEncoded
        @POST(BaseConst.COS_CONTROL)
        Call<COSModel> COS_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "COS_ID") String COS_ID,
                @Field(value = "COS_01") String COS_01,
                @Field(value = "COS_02") String COS_02,
                @Field(value = "COS_03") String COS_03,

                @Field(value = "COS_98") String COS_98
        );

    }

    public static ICOD cod(TYPE type) {
        return (ICOD) retrofit(ICOD.class, type);
    }

    public interface ICOD {

        @FormUrlEncoded
        @POST(BaseConst.COD_SELECT)
        Call<CODModel> COD_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "COD_ID") String COD_ID,     // 컨테이너
                @Field(value = "COD_01") String COD_01,     // 일련번호
                @Field(value = "COD_95") String COD_95,     // 화장대코드(COS_01)
                @Field(value = "OCM_01") String OCM_01      // 사용자 아이디
        );

        @FormUrlEncoded
        @POST(BaseConst.COD_CONTROL)
        Call<CODModel> COD_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "COD_ID") String COD_ID,
                @Field(value = "COD_01") String COD_01,
                @Field(value = "COD_02") String COD_02,
                @Field(value = "COD_03") String COD_03,

                @Field(value = "COD_04") double COD_04,
                @Field(value = "COD_05") String COD_05,
                @Field(value = "COD_06") String COD_06,
                @Field(value = "COD_07") String COD_07,
                @Field(value = "COD_08") String COD_08,

                @Field(value = "COD_95") String COD_95,
                @Field(value = "COD_96") String COD_96,
                @Field(value = "COD_98") String COD_98,
                @Field(value = "ARM_03") String ARM_03
        );

    }

    //--------------------------------------------------
    // 차량 소모품 점검/교체 내역
    //--------------------------------------------------
    public static ICAR car(TYPE type) {
        return (ICAR) retrofit(ICAR.class, type);
    }

    public interface ICAR {

        @FormUrlEncoded
        @POST(BaseConst.CAR_SELECT)
        Call<CARModel> CAR_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CAR_ID") String CAR_ID,     // 컨테이너
                @Field(value = "CAR_01") String CAR_01      // 코드번호
        );

        @FormUrlEncoded
        @POST(BaseConst.CAR_CONTROL)
        Call<CARModel> CAR_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CAR_ID") String CAR_ID,
                @Field(value = "CAR_01") String CAR_01,
                @Field(value = "CAR_02") String CAR_02,
                @Field(value = "CAR_03") String CAR_03,

                @Field(value = "CAR_04") String CAR_04,
                @Field(value = "CAR_98") String CAR_98
        );

    }

    public static ICAD cad(TYPE type) {
        return (ICAD) retrofit(ICAD.class, type);
    }

    public interface ICAD {

        @FormUrlEncoded
        @POST(BaseConst.CAD_SELECT)
        Call<CADModel> CAD_SELECT(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CAD_ID") String CAD_ID,     // 컨테이너
                @Field(value = "CAD_01") String CAD_01,     // 차량코드
                @Field(value = "CAD_02") String CAD_02,     // 일련번호
                @Field(value = "CAD_03") String CAD_03,     // 정비일자

                @Field(value = "CAD_04") String CAD_04      // 내역
        );

        @FormUrlEncoded
        @POST(BaseConst.CAD_CONTROL)
        Call<CADModel> CAD_CONTROL(
                @Path(value = "host", encoded = true) String host,
                @Field(value = "GUBUN") String GUBUN,
                @Field(value = "CAD_ID") String CAD_ID,
                @Field(value = "CAD_01") String CAD_01,
                @Field(value = "CAD_02") String CAD_02,
                @Field(value = "CAD_03") String CAD_03,

                @Field(value = "CAD_04") String CAD_04,
                @Field(value = "CAD_05") String CAD_05,
                @Field(value = "CAD_06") String CAD_06,
                @Field(value = "CAD_07") double CAD_07,
                @Field(value = "CAD_08") int CAD_08,

                @Field(value = "CAD_09") String CAD_95,
                @Field(value = "CAD_98") String CAD_98
        );

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