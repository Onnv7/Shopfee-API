package com.hcmute.shopfee.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CloudinaryUploadResponse {
    @JsonProperty("signature")
    private String signature;

    @JsonProperty("format")
    private String format;

    @JsonProperty("resource_type")
    private String resourceType;

    @JsonProperty("secure_url")
    private String secureUrl;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("asset_id")
    private String assetId;

    @JsonProperty("version_id")
    private String versionId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("version")
    private String version;

    @JsonProperty("access_mode")
    private String accessMode;

    @JsonProperty("url")
    private String url;

    @JsonProperty("public_id")
    private String publicId;

    @JsonProperty("tags")
    private String[] tags;

    @JsonProperty("folder")
    private String folder;

    @JsonProperty("original_filename")
    private String originalFilename;

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("bytes")
    private int bytes;

    @JsonProperty("width")
    private int width;

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("placeholder")
    private boolean placeholder;

    @JsonProperty("height")
    private int height;
}
