package com.create.chacha.domains.buyer.areas.classes.reservations.service.serviceimpl;

import com.create.chacha.config.app.toss.TossProperties;
import com.create.chacha.domains.buyer.areas.classes.classlist.repository.ClassInfoRepository;
import com.create.chacha.domains.buyer.areas.classes.reservations.dto.request.ClassReservationPaymentRequestDTO;
import com.create.chacha.domains.buyer.areas.classes.reservations.dto.response.ClassReservationCompleteResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.reservations.repository.ClassReservationPaymentRepository;
import com.create.chacha.domains.buyer.areas.classes.reservations.service.ClassReservationPaymentService;
import com.create.chacha.domains.buyer.exception.payment.PaymentFailedException;
import com.create.chacha.domains.buyer.exception.payment.PaymentRequestException;
import com.create.chacha.domains.buyer.exception.classes.ReservationException;
import com.create.chacha.domains.shared.constants.OrderAndReservationStatusEnum;
import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;
import com.create.chacha.domains.shared.entity.classcore.ClassReservationEntity;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import com.create.chacha.domains.shared.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ClassReservationPaymentServiceImpl implements ClassReservationPaymentService {
    private final TossProperties tossProperties;
    private final ClassReservationPaymentRepository reservationRepository;
    private final ClassInfoRepository classInfoRepository;
    private final MemberRepository memberRepository;

    @Override
    public ClassReservationCompleteResponseDTO payAndSaveReservation(Long classId, Long memberId, ClassReservationPaymentRequestDTO request) {
        // 클래스와 회원 정보 조회
        ClassInfoEntity classInfo = classInfoRepository.findById(classId)
                .orElseThrow(() -> new ReservationException("클래스 정보 없음"));

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ReservationException("회원 정보 없음"));

        try {
            // Toss Payments 결제 승인 요청
            String encodedAuth = Base64.getEncoder()
                    .encodeToString((tossProperties.getClientSecret() + ":").getBytes());

            String requestBody = String.format(
                    "{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":%d}",
                    request.getPaymentKey(),
                    request.getId(),
                    request.getAmount()
            );

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new PaymentFailedException("결제 승인 실패: " + response.body());
            }

            // 예약 정보 DB 저장
            ClassReservationEntity reservation = ClassReservationEntity.builder()
                    .classInfo(classInfo)
                    .member(member)
                    .status(OrderAndReservationStatusEnum.ORDER_OK)
                    .paymentKey(request.getPaymentKey())
                    .reservedTime(request.getReservedTime())
                    .build();

            ClassReservationEntity complete = reservationRepository.save(reservation);

            // 결제 후 예약 완료 정보 반환
            return ClassReservationCompleteResponseDTO.builder()
                    .memberName(complete.getMember().getName())
                    .classTitle(complete.getClassInfo().getTitle())
                    .reservedTime(complete.getReservedTime())
                    .reservationNumber(complete.getReservationNumber())
                    .amount(request.getAmount())
                    .build();

        } catch (InterruptedException | IOException e) {
            throw new PaymentRequestException("결제 요청 중 오류 발생", e);
        }
    }

}
