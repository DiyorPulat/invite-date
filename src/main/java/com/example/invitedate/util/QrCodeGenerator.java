package com.example.invitedate.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.io.*;
import java.util.Base64;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class QrCodeGenerator {
    private static final int SIZE = 512;
    public byte[] png(String value) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            BitMatrix matrix = new MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, SIZE, SIZE,
                    Map.of(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M,
                           EncodeHintType.MARGIN, 2));
            MatrixToImageWriter.writeToStream(matrix, "PNG", output);
            return output.toByteArray();
        } catch (WriterException | IOException ex) { throw new IllegalStateException("Could not generate QR code", ex); }
    }
    public String pngDataUrl(String value) { return "data:image/png;base64," + Base64.getEncoder().encodeToString(png(value)); }
}
