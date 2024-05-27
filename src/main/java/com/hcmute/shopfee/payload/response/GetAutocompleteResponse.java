package com.hcmute.shopfee.payload.response;

import lombok.Data;

import java.util.List;

@Data
public class GetAutocompleteResponse {
    private List<String> autocompleteTextList;
    private List<String> highlightTextList;
}
