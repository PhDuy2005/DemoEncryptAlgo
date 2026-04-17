package com.encrpt.demo.DemoEncryptAlgo.domain.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RijndaelRequest {
    private String data;
    private String key;
}