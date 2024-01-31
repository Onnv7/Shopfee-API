package com.hcmute.shopfee.utils;

import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static byte[] resizeImage(byte[] originalImage, int height, int width) throws IOException {
        ByteArrayOutputStream outputStreamThumb = new ByteArrayOutputStream();

        Thumbnails.of(new ByteArrayInputStream(originalImage))
                .forceSize(height, width)
                .toOutputStream(outputStreamThumb);

        return outputStreamThumb.toByteArray();
    }
}
