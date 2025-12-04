package com.edos.Middleware.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NetworkFlowInput {

    @JsonProperty("dst_port")
    private int dstPort;
    @JsonProperty("flow_duration")
    private float flowDuration;
    @JsonProperty("tot_fwd_pkts")
    private int totFwdPkts;
    @JsonProperty("tot_bwd_pkts")
    private int totBwdPkts;
    @JsonProperty("fwd_pkt_len_max")
    private int fwdPktLenMax;
    @JsonProperty("fwd_pkt_len_min")
    private int fwdPktLenMin;
    @JsonProperty("bwd_pkt_len_max")
    private int bwdPktLenMax;
    @JsonProperty("bwd_pkt_len_mean")
    private float bwdPktLenMean;
    @JsonProperty("flow_byts_s")
    private float flowBytsS;
    @JsonProperty("flow_pkts_s")
    private float flowPktsS;
    @JsonProperty("flow_iat_mean")
    private float flowIatMean;
    @JsonProperty("flow_iat_std")
    private float flowIatStd;
    @JsonProperty("flow_iat_max")
    private float flowIatMax;
    @JsonProperty("fwd_iat_std")
    private float fwdIatStd;
    @JsonProperty("bwd_pkts_s")
    private float bwdPktsS;
    @JsonProperty("psh_flag_cnt")
    private int pshFlagCnt;
    @JsonProperty("ack_flag_cnt")
    private int ackFlagCnt;
    @JsonProperty("init_fwd_win_byts")
    private int initFwdWinByts;
    @JsonProperty("init_bwd_win_byts")
    private int initBwdWinByts;
    @JsonProperty("fwd_seg_size_min")
    private int fwdSegSizeMin;

}
