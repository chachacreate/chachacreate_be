package com.create.chacha.domains.buyer.areas.classes.reservations.repository;

import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassReservationPaymentRepository extends JpaRepository<ClassReservationEntity, String> {

}
