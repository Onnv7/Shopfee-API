package com.hcmute.shopfee.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DialogFlowRequest {
    @JsonProperty("responseId")
    private String responseId;

    @JsonProperty("queryResult")
    private QueryResult queryResult;


    @Data
    public class QueryResult {
        @JsonProperty("queryText")
        private String queryText;

        @JsonProperty("action")
        private String action;

        @JsonProperty("parameters")
        private Parameters parameters;

        // Other fields and getters/setters
    }

    @Data
    public class Parameters {
        @JsonProperty("branch-id")
        private String branchId;

        // Other fields and getters/setters
    }
}
