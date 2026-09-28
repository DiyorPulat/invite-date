package com.example.invitedate.service;

import com.example.invitedate.entity.Invitation;
import com.example.invitedate.repository.InvitationRepository;
import com.example.invitedate.util.QrCodeGenerator;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationPdfService {
    private final InvitationRepository repository;
    private final QrCodeGenerator qrCodeGenerator;
    @Value("${app.public-base-url}") private String publicBaseUrl;

    public byte[] generate(UUID id) {
        Invitation invitation = repository.findById(id).orElseThrow();
        String url = publicBaseUrl.replaceAll("/$", "") + "/invite/" + id;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Document document = new Document(new Rectangle(420, 595), 26, 24, 26, 24);
            PdfWriter.getInstance(document, output);
            document.open();
            Font title = new Font(Font.HELVETICA, 24, Font.BOLD, new java.awt.Color(55, 42, 50));
            Font subtitle = new Font(Font.HELVETICA, 10, Font.NORMAL, new java.awt.Color(139, 103, 111));
            Font accent = new Font(Font.HELVETICA, 11, Font.BOLD, new java.awt.Color(213, 72, 98));
            Font body = new Font(Font.HELVETICA, 10, Font.NORMAL, new java.awt.Color(99, 77, 85));
            Font label = new Font(Font.HELVETICA, 8, Font.BOLD, new java.awt.Color(188, 117, 128));

            PdfPTable top = new PdfPTable(1);
            top.setWidthPercentage(100);
            top.addCell(cell("UCHRASHAMIZMI?  *", new Font(Font.HELVETICA, 18, Font.BOLD, new java.awt.Color(55, 42, 50)), new java.awt.Color(255, 235, 235), Element.ALIGN_CENTER, 13));
            top.addCell(cell("SIZGA MAXSUS, KICHKINA TAKLIFNOMA", subtitle, new java.awt.Color(255, 246, 244), Element.ALIGN_CENTER, 6));
            document.add(top);

            Paragraph invite = new Paragraph(invitation.getReceiverName() + ",\n" + "uchrashamizmi?", title);
            invite.setAlignment(Element.ALIGN_CENTER);
            document.add(invite);
            if (invitation.getCustomMessage() != null && !invitation.getCustomMessage().isBlank()) {
                Paragraph message = new Paragraph("\"" + invitation.getCustomMessage() + "\"", new Font(Font.HELVETICA, 11, Font.ITALIC, new java.awt.Color(126, 91, 101)));
                message.setAlignment(Element.ALIGN_CENTER);
                document.add(message);
            }

            PdfPTable details = new PdfPTable(2);
            details.setWidthPercentage(100);
            details.setSpacingBefore(10);
            details.setWidths(new float[]{1, 1});
            details.addCell(cell("KIMDAN\n" + invitation.getSenderName(), label, new java.awt.Color(255, 244, 241), Element.ALIGN_LEFT, 8));
            details.addCell(cell("UCHRASHUV\n" + invitation.getDateType().name(), label, new java.awt.Color(255, 244, 241), Element.ALIGN_LEFT, 8));
            if (invitation.getProposedDate() != null) details.addCell(cell("TAKLIF QILINGAN VAQT\n" + invitation.getProposedDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")), label, new java.awt.Color(255, 244, 241), Element.ALIGN_LEFT, 8));
            details.addCell(cell("TAKLIF KODI\n" + id.toString().substring(0, 8).toUpperCase(), label, new java.awt.Color(255, 244, 241), Element.ALIGN_LEFT, 8));
            document.add(details);

            PdfPTable qrCard = new PdfPTable(1);
            qrCard.setWidthPercentage(100);
            qrCard.addCell(cell("TAKLIFNI OCHISH UCHUN", accent, new java.awt.Color(255, 241, 242), Element.ALIGN_CENTER, 7));
            Image qr = Image.getInstance(qrCodeGenerator.png(url));
            qr.scaleToFit(120, 120);
            qr.setAlignment(Element.ALIGN_CENTER);
            PdfPCell qrCell = new PdfPCell(qr, false);
            qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            qrCell.setPadding(5);
            qrCell.setBorderColor(new java.awt.Color(244, 201, 207));
            qrCell.setBackgroundColor(new java.awt.Color(255, 250, 249));
            qrCard.addCell(qrCell);
            qrCard.addCell(cell("Telefon kamerasi bilan skanerlang\n" + url, body, new java.awt.Color(255, 250, 249), Element.ALIGN_CENTER, 8));
            document.add(qrCard);
            Paragraph footer = new Paragraph("Yaxshi uchrashuv uchun bir bahona kifoya  *", new Font(Font.HELVETICA, 11, Font.BOLD, new java.awt.Color(213, 72, 98)));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(8);
            document.add(footer);
            document.close();
            return output.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate invitation PDF", ex);
        }
    }

    private PdfPCell cell(String text, Font font) {
        return cell(text, font, java.awt.Color.WHITE, Element.ALIGN_LEFT, 8);
    }

    private PdfPCell cell(String text, Font font, java.awt.Color background, int alignment, float padding) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(background);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(padding);
        return cell;
    }
}
