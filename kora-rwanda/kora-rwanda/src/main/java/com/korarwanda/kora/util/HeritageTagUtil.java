package com.korarwanda.kora.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Component
public class HeritageTagUtil {

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a cryptographically unique Heritage Hash for a product.
     * Binds: artisanId + productName + district + timestamp + random salt
     */
    public String generateHeritageHash(Long artisanId, String productName, String district) {
        try {
            String salt = UUID.randomUUID().toString();
            String raw = artisanId + "|" + productName + "|" + district + "|"
                    + System.currentTimeMillis() + "|" + salt;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return "KR-" + Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate heritage hash", e);
        }
    }

    /**
     * Generates QR code data string that encodes the verification URL.
     */
    public String generateQrCodeData(String heritageHash) {
        return "https://kora-rwanda.rw/verify/" + heritageHash;
    }

    /**
     * Generates a QR code PNG image as a Base64 string.
     */
    public String generateQrCodeBase64(String qrCodeData) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 300, 300);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return "data:image/png;base64,"
                    + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Generates a unique transaction reference for payments.
     */
    public String generateTransactionRef(String paymentMethod) {
        String prefix = paymentMethod.startsWith("MTN") ? "MTN" : "ATL";
        return prefix + "-" + System.currentTimeMillis() + "-"
                + (secureRandom.nextInt(9000) + 1000);
    }
}
