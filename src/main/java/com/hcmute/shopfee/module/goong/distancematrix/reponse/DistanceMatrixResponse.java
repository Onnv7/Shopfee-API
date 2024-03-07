package com.hcmute.shopfee.module.goong.distancematrix.reponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DistanceMatrixResponse {
    @JsonProperty("rows")
    private Row[] rows;

    @Data
    public static class Row {
        @JsonProperty("elements")
        private Element[] elements;

        @Data
        public static class Element {
            @JsonProperty("distance")
            private Distance distance;

            @JsonProperty("duration")
            private Duration duration;

            @JsonProperty("status")
            private String status;

            @Data
            public static class Distance {
                @JsonProperty("text")
                private String text;

                @JsonProperty("value")
                private int value;

            }
            @Data
            public static class Duration {
                @JsonProperty("text")
                private String text;

                @JsonProperty("value")
                private int value;
            }
        }
    }
}

