package com.example.invitedate.service.impl;

import com.example.invitedate.entity.Invitation;
import com.example.invitedate.enums.InvitationStatus;
import com.example.invitedate.service.InvitationNotificationService;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import jakarta.mail.internet.MimeMessage;

@Service
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class EmailInvitationNotificationService implements InvitationNotificationService {
    private final JavaMailSender mailSender;
    @Value("${app.mail.enabled:false}") private boolean enabled;
    @Value("${app.mail.from:}") private String from;

    @Override
    public void sendResponse(Invitation invitation, InvitationStatus status, Map<String, Object> details, String ticketCode) {
        if (!enabled) return;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(invitation.getSenderEmail());
            if (!from.isBlank()) helper.setFrom(from);
            boolean accepted = status == InvitationStatus.ACCEPTED;
            helper.setSubject((accepted ? "💌 U 'ha' dedi!" : "Taklifga javob keldi") + " — " + invitation.getReceiverName());
            String body = accepted
                    ? "<h2>Zo‘r yangilik! " + safe(invitation.getReceiverName()) + " taklifingizni qabul qildi 💕</h2>"
                    : "<h2>" + safe(invitation.getReceiverName()) + " taklifga javob berdi.</h2>";
            helper.setText(body + "<p><b>Taklif:</b> " + safe(invitation.getDateType().name()) + "</p>"
                    + "<p><b>Tanlovlar:</b> " + safe(String.valueOf(details == null ? Map.of() : details)) + "</p>"
                    + "<p>PDF-chipta ilovada biriktirilgan.</p>", true);
            helper.addAttachment("birga-" + ticketCode + ".pdf", new ByteArrayResource(pdf(invitation, details, ticketCode)));
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Could not send invitation response email for {}", invitation.getId(), ex);
        }
    }

    private byte[] pdf(Invitation invitation, Map<String, Object> details, String ticketCode) throws Exception {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph("BIRGA — UCHRASHUV CHIPTASI", new Font(Font.HELVETICA, 18, Font.BOLD)));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Kimdan: " + invitation.getSenderName()));
            document.add(new Paragraph("Kimga: " + invitation.getReceiverName()));
            document.add(new Paragraph("Holat: " + (invitation.getStatus() == InvitationStatus.ACCEPTED ? "QABUL QILINDI" : "JAVOB KELDI")));
            document.add(new Paragraph("Tanlovlar: " + String.valueOf(details == null ? Map.of() : details)));
            document.add(new Paragraph("Chipta kodi: " + ticketCode));
            document.add(new Paragraph("Yaratilgan: " + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(invitation.getUpdatedAt())));
            document.close();
            return output.toByteArray();
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
