package com.create.chacha.domains.buyer.areas.classes.reservations.service;

import com.create.chacha.domains.buyer.areas.classes.reservations.dto.request.ClassReservationPaymentRequestDTO;
import com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationCompleteResponseDTO;
import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;

public interface ClassReservationPaymentService {
    ClassReservationCompleteResponseDTO payAndSaveReservation(Long classId, Long memberId, ClassReservationPaymentRequestDTO request);
}