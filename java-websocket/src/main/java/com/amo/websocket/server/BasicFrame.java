package com.amo.websocket.server;

import com.amo.websocket.Frame;
import com.amo.websocket.FrameType;

/**
 * Created by ayeminoo on 1/4/18.
 */
public class BasicFrame implements Frame {
    private boolean finalSegment;
    private boolean RSV1;
    private boolean RSV2;
    private boolean RSV3;
    private boolean masked;
    private FrameType frameType;
    private byte payloadLength;
    private byte[] maskingKey;
    private byte[] payload;

    public BasicFrame(boolean finalSegment, boolean RSV1, boolean RSV2, boolean RSV3, boolean masked,
                      FrameType frameType, byte payloadLength, byte[] maskingKey, byte[] payload) {
        this.finalSegment = finalSegment;
        this.RSV1 = RSV1;
        this.RSV2 = RSV2;
        this.RSV3 = RSV3;
        this.masked = masked;
        this.frameType = frameType;
        this.payloadLength = payloadLength;
        this.maskingKey = maskingKey;
        this.payload = payload;
    }

    @Override
    public boolean isFinalSegment() {
        return finalSegment;
    }

    @Override
    public boolean isRSV1() {
        return RSV1;
    }

    @Override
    public boolean isRSV2() {
        return RSV2;
    }

    @Override
    public boolean isRSV3() {
        return RSV3;
    }

    @Override
    public boolean isMasked() {
        return masked;
    }

    @Override
    public FrameType getFrameType() {
        return frameType;
    }

    public byte getPayloadLength() {
        return payloadLength;
    }

    @Override
    public byte[] getMaskingKey() {
        return maskingKey;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }
}
