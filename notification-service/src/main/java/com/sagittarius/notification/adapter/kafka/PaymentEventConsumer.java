package com.sagittarius.notification.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagittarius.notification.application.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Value("${app.test-recipient}")
    private String customerEmail;

    @KafkaListener(topics = "payment-events", groupId = "notification-group")
    public void listen(String message) {
        try {
            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(message);

            // Outbox của PaymentService dùng "aggregateId", "type" và "payload"
            if (!rootNode.has("type") || !rootNode.has("aggregateId")) {
                return;
            }

            String eventType = rootNode.get("type").asText();
            String customerId = rootNode.get("aggregateId").asText();

            if ("WalletOpened".equals(eventType)) {
                log.info("Received WalletOpened event for customer: {}", customerId);

                String subject = "🎉 Chúc mừng bạn đã mở Ví Sagittarius thành công!";
                String body = "Kính gửi khách hàng " + customerId + ",\n\n" +
                        "Ví điện tử Sagittarius của bạn đã được kích hoạt thành công với số dư ban đầu là 0đ.\n" +
                        "Bây giờ bạn đã có thể nạp tiền và trải nghiệm mua sắm mượt mà không lo gián đoạn!\n\n" +
                        "Trân trọng,\nĐội ngũ Sagittarius.";

                emailService.sendEmail(customerEmail, subject, body);
                log.info("Sent WALLET_OPENED email to {}", customerEmail);
            }

        } catch (Exception e) {
            log.error("Error in processing Payment/Wallet Email", e);
        }
    }
}
