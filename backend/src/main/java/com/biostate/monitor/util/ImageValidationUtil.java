package com.biostate.monitor.util;

import java.util.Base64;

public final class ImageValidationUtil {

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final String[] ALLOWED_MIME_TYPES = {"image/jpeg", "image/png", "image/webp"};

    private ImageValidationUtil() {
    }

    public static boolean isValidBase64Image(String base64Data) {
        if (base64Data == null || base64Data.trim().isEmpty()) {
            return false;
        }

        try {
            byte[] decodedData = Base64.getDecoder().decode(base64Data);
            return decodedData.length <= MAX_IMAGE_SIZE;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String getMimeType(byte[] imageData) {
        if (imageData == null || imageData.length < 4) {
            return null;
        }

        // Check magic bytes for common image formats
        if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8 && imageData[2] == (byte) 0xFF) {
            return "image/jpeg";
        }
        if (imageData[0] == (byte) 0x89 && imageData[1] == 0x50 && imageData[2] == 0x4E && imageData[3] == 0x47) {
            return "image/png";
        }
        if (imageData[0] == 0x52 && imageData[1] == 0x49 && imageData[2] == 0x46 && imageData[3] == 0x46) {
            return "image/webp";
        }

        return null;
    }

    public static boolean isMimeTypeAllowed(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        for (String allowed : ALLOWED_MIME_TYPES) {
            if (allowed.equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }
}
