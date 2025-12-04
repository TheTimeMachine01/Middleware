package com.edos.Middleware.dto.ML;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Flow {

    @JsonProperty("src_ip")
    private String srcIp;

    @JsonProperty("dst_ip")
    private String dstIp;

    @JsonProperty("src_port")
    private int srcPort;

    @JsonProperty("dst_port")
    private int dstPort;

    private String protocol;
}
