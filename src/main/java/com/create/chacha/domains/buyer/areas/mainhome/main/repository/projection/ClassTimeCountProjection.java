package com.create.chacha.domains.buyer.areas.mainhome.main.repository.projection;

public interface ClassTimeCountProjection {
    Long getClassId();
    String getTime();   // "HH:mm"
    Long getCount();    // 해당 시간 예약 수
}