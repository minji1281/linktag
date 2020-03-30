package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class VIT_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String VIT_ID;       // 컨테이너
    public String VIT_01;       // 투표코드(VOT_01)
    public String VIT_02;       // 일련번호(자동생성)
    public String VIT_03;       // 명칭
    public String VIT_98;       // 최종수정자
    //public Date VIT_99;       // 최종수정일시

}
