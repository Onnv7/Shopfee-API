package com.hcmute.shopfee.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CloudinaryUploadResponse {
    private String signature;

    private String format;

    private String resourceType;

    private String secureUrl;

    private String createdAt;

    private String assetId;

    private String versionId;

    private String type;

    private String version;

    private String accessMode;

    private String url;

    private String publicId;

    private String[] tags;

    private String folder;

    private String originalFilename;

    private String apiKey;

    private int bytes;

    private int width;

    private String etag;

    private boolean placeholder;

    private int height;
    private boolean overwritten;

    public CloudinaryUploadResponse(Map<String, String> map) {
        this.signature = map.get("signature");
        this.format = map.get("format");
        this.resourceType = map.get("resource_type");
        this.secureUrl = map.get("secure_url");
        this.createdAt = map.get("created_at");
        this.assetId = map.get("asset_id");
        this.versionId = map.get("version_id");
        this.type = map.get("type");
        this.version = String.valueOf(Integer.parseInt(map.get("version")));
        this.accessMode = map.get("access_mode");
        this.url = map.get("url");
        this.publicId = map.get("public_id");
        this.tags = map.get("tags").isEmpty() ? new String[0] : map.get("tags").split(","); // Handle empty tags
        this.folder = map.get("folder");
        this.originalFilename = map.get("original_filename");
        this.apiKey = map.get("api_key");
        this.bytes = (int) Long.parseLong(map.get("bytes"));
        this.overwritten = Boolean.parseBoolean(map.get("overwritten"));
        this.width = Integer.parseInt(map.get("width"));
        this.etag = map.get("etag");
        this.placeholder = Boolean.parseBoolean(map.get("placeholder"));
        this.height = Integer.parseInt(map.get("height"));
    }
    public static CloudinaryUploadResponse fromString(String string) {
        Map<String, String> map = new HashMap<>();
        for (String keyValue : string.substring(1, string.length() - 1).split(", ")) {
            String[] parts = keyValue.split("=");
            map.put(parts[0], parts[1]);
        }
        return new CloudinaryUploadResponse(map);
    }

}
