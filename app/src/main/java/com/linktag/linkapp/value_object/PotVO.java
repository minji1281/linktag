package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class PotVO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String POT_ID;       // 컨테이너
    public String POT_01;       // 코드번호
    public String POT_02;       // 명칭
    //public Date POT_03;       // 최근 물주기 시각
    public int POT_04;       // 주기
    public String POT_05;       // 주기구분
    public String POT_06;       // 메모
    public String POT_81;       // 이미지
    public String POT_96;       // 알림시각
    public String POT_97;       // 작성자
    public String POT_98;       // 최종수정자
    //public Date POT_99;       // 최종수정일시

    public String POT_03_T;       // 최근 물주기 시각(YYYY-MM-DD HH:mm)
    public String DDAY;       // 다음 알림시간까지 D-day
    public String ARM_03;       // 해당 사용자의 알림여부
    public int ARM_04;       // 알림ID

    public boolean Validation;

}
