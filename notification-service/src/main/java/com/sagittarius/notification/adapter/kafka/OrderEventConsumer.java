package com.sagittarius.notification.adapter.kafka;

import com.fasterxml.jackson.databind.JsonNode;
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
public class OrderEventConsumer {
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Value("${app.test-recipient}")
    private String customerEmail;

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void listen(String message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message);

            if (!rootNode.has("type") || !rootNode.has("orderId")) {
                return;
            }

            String eventType = rootNode.get("type").asText();
            String orderNumber = rootNode.get("orderId").asText();

            JsonNode dataNode = rootNode.get("data");
            if (!dataNode.has("email") || dataNode.get("email").isNull()) {
                log.warn("Missing email in event payload for order: {}", orderNumber);
                return;
            }

            if ("OrderCompleted".equals(eventType)) {
                String subject = "Xác nhận đơn hàng thành công: " + orderNumber;
                String body = "Kính gửi khách hàng,\n\n" +
                        "Đơn hàng [" + orderNumber + "] của bạn đã được thanh toán và chốt kho thành công.\n" +
                        "Chúng tôi đang tiến hành đóng gói và sẽ giao hàng đến bạn trong thời gian sớm nhất!\n\n" +
                        "Cảm ơn bạn đã mua sắm tại hệ thống của chúng tôi.";
                emailService.sendEmail(customerEmail, subject, body);
                log.info("Sent SUCCESS email to {}", customerEmail);
            }

            else if ("OrderCancelled".equals(eventType)) {
                String subject = "Thông báo hủy đơn hàng: " + orderNumber;

                String reason = dataNode.has("reason") ? dataNode.get("reason").asText() : "Số dư không đủ hoặc lỗi hệ thống";

                String body = "Kính gửi khách hàng,\n\n" +
                        "Rất tiếc, đơn hàng [" + orderNumber + "] của bạn đã bị hủy.\n" +
                        "Lý do: " + reason + ".\n\n" +
                        "Mong bạn thông cảm và tiếp tục ủng hộ chúng tôi trong tương lai.";

                emailService.sendEmail(customerEmail, subject, body);
                log.info("Sent CANCELLED email to {}", customerEmail);
            }

        } catch (Exception e) {
            log.error("Error in processing Email", e);
        }
    }
}
