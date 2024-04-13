package com.hcmute.shopfee.utils;

import com.hcmute.shopfee.enums.MediaType;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MediaUtils {
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");

    public static byte[] resizeImage(byte[] originalImage, int height, int width) throws IOException {
        ByteArrayOutputStream outputStreamThumb = new ByteArrayOutputStream();

        Thumbnails.of(new ByteArrayInputStream(originalImage))
                .forceSize(height, width)
                .toOutputStream(outputStreamThumb);

        return outputStreamThumb.toByteArray();
    }
    public static boolean isValidImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            return false;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        return ALLOWED_EXTENSIONS.contains(extension);
    }

    public static MediaType getMediaType( MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType != null) {
            if (contentType.startsWith("image")) {
                return MediaType.IMAGE;
            } else if (contentType.startsWith("video")) {
                return MediaType.VIDEO;
            } else {
                return MediaType.NONE;
            }
        } else {
            return MediaType.NONE;
        }
    }
}
