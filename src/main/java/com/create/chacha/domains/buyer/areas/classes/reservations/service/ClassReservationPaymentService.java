package com.create.chacha.domains.buyer.areas.classes.reservations.service;

import com.create.chacha.domains.buyer.areas.classes.reservations.dto.request.ClassReservationPaymentRequestDTO;
import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;

public interface ClassReservationPaymentService {
    String payAndSaveReservation(Long classId,Long memberId, ClassReservationPaymentRequestDTO request);
}