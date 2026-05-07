package com.infy.ekart.paymentservice.service.impl;

import com.infy.ekart.paymentservice.dto.request.PaymentRequest;
import com.infy.ekart.paymentservice.dto.request.RefundRequest;
import com.infy.ekart.paymentservice.dto.response.PaymentResponse;
import com.infy.ekart.paymentservice.dto.response.PaymentStatusResponse;
import com.infy.ekart.paymentservice.entity.Payment;
import com.infy.ekart.paymentservice.entity.Refund;
import com.infy.ekart.paymentservice.enums.PaymentMethod;
import com.infy.ekart.paymentservice.enums.PaymentStatus;
import com.infy.ekart.paymentservice.enums.RefundStatus;
import com.infy.ekart.paymentservice.exception.PaymentNotFoundException;
import com.infy.ekart.paymentservice.repository.PaymentRepository;
import com.infy.ekart.paymentservice.repository.RefundRepository;
import com.infy.ekart.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, RefundRepository refundRepository) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for order: {}", request.getOrderId());

        Payment payment = new Payment();
        payment.setPaymentNumber("PAY-" + System.currentTimeMillis());
        payment.setOrderId(UUID.fromString(request.getOrderId()));
        payment.setUserId(UUID.fromString(request.getUserId()));
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        if (request.getUpiId() != null) {
            payment.setUpiId(request.getUpiId());
        }

        if (request.getCardNumber() != null && request.getCardNumber().length() >= 4) {
            payment.setCardLastFour(request.getCardNumber().substring(request.getCardNumber().length() - 4));
            payment.setCardType(detectCardType(request.getCardNumber()));
        }

        if (request.getBankName() != null) {
            payment.setBankName(request.getBankName());
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 12);
        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        payment.setGatewayResponse("Payment processed successfully");

        payment = paymentRepository.save(payment);
        log.info("Payment completed: {} for order: {}", payment.getPaymentNumber(), request.getOrderId());

        return toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(String paymentId) {
        UUID id = UUID.fromString(paymentId);
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new PaymentNotFoundException(id));
        return toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusResponse getPaymentByOrderId(String orderId) {
        UUID oid = UUID.fromString(orderId);
        Payment payment = paymentRepository.findTopByOrderIdOrderByCreatedAtDesc(oid)
            .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
        return new PaymentStatusResponse(
            payment.getOrderId().toString(),
            payment.getId().toString(),
            payment.getStatus().toString(),
            payment.getTransactionId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getUserPayments(String userId) {
        UUID uid = UUID.fromString(userId);
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(uid).stream()
            .map(this::toPaymentResponse)
            .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse refundPayment(String paymentId, RefundRequest request) {
        UUID pid = UUID.fromString(paymentId);
        Payment payment = paymentRepository.findById(pid)
            .orElseThrow(() -> new PaymentNotFoundException(pid));

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Refund refund = new Refund();
        refund.setRefundNumber("REF-" + System.currentTimeMillis());
        refund.setPaymentId(pid);
        refund.setOrderId(payment.getOrderId());
        refund.setAmount(payment.getAmount());
        refund.setReason(request.getReason());
        refund.setStatus(RefundStatus.COMPLETED);
        refund.setTransactionId("REF-TXN-" + UUID.randomUUID().toString().substring(0, 8));
        refund.setProcessedAt(LocalDateTime.now());
        refund.setCreatedAt(LocalDateTime.now());
        refundRepository.save(refund);

        log.info("Refund processed for payment: {}", paymentId);
        return toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatusResponse verifyPayment(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return new PaymentStatusResponse(
            payment.getOrderId().toString(),
            payment.getId().toString(),
            payment.getStatus().toString(),
            payment.getTransactionId()
        );
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getPaymentNumber(),
            payment.getOrderId(),
            payment.getUserId(),
            payment.getAmount(),
            payment.getCurrency(),
            payment.getPaymentMethod().toString(),
            payment.getStatus().toString(),
            payment.getTransactionId(),
            payment.getUpiId(),
            payment.getCardLastFour(),
            payment.getCardType(),
            payment.getBankName(),
            payment.getPaidAt(),
            payment.getCreatedAt()
        );
    }

    private String detectCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) return "VISA";
        if (cardNumber.startsWith("5")) return "MASTERCARD";
        if (cardNumber.startsWith("3")) return "AMEX";
        return "UNKNOWN";
    }
}