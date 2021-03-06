package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class ARM_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String ARM_ID;       // 컨테이너
    public String ARM_01;       // 코드번호
    public String ARM_02;       // 알림설정자
    public String ARM_03;       // 알람유무
    public String ARM_04;       // ?
    public String ARM_90;       // 푸시제목
    public String ARM_91;       // 푸시내용
    public String ARM_92;       // 푸시년월시분
    public String ARM_93;       // 푸시일월화수목금토
    public String ARM_94;       // 푸시구분
    public String ARM_95;       // 보조내부코드?
    public String ARM_98;       // 최종작성자

    public String CTM_19;       // 푸시년월시분
    public String CTM_04;       // 푸시년월시분
    public String ARM_0101;       // 푸시년월시분

    public String SVCL_04;
    public String SVCL_05;

    public String CTD_07;   // 관리자 아이디
    public String CTD_09;   // 서비스 구분
    public String CTD_10;   // 공유명

    public boolean Validation;

}
