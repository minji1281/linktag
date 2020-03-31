package com.linktag.linkapp.value_object;

import java.io.Serializable;

public class VOT_VO implements Serializable {
    private static final long serialVersionUID = 4635771219693349629L;

    public String VOT_ID;       // 컨테이너
    public String VOT_01;       // 일련번호(자동생성)
    public String VOT_02;       // 명칭
    public String VOT_03;       // 작성일자(YYYYMMDD)
    public String VOT_04;       // 마감일자(YYYYMMDD)
    public String VOT_05;       // 복수여부(Y/N)
    public String VOT_06;       // 익명여부(Y/N)
    public String VOT_97;       // 작성자
    public String VOT_98;       // 최종수정자
    //public Date VOT_99;       // 최종수정일시

    public boolean Validation;
}
