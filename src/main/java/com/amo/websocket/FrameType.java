package com.amo.websocket;

/**
 * Created by ayeminoo on 1/4/18.
 */
public enum FrameType {
    CONTINUE_FRAME((byte) 0),
    TEXT_FRAME((byte) 1),
    BINARY_FRAME((byte) 2),
    RESERVE_NON_CONTROL_FRAME((byte) 3),
    RESERVE_NON_CONTROL_FRAME1((byte) 4),
    RESERVE_NON_CONTROL_FRAME2((byte) 5),
    RESERVE_NON_CONTROL_FRAME3((byte) 6),
    RESERVE_NON_CONTROL_FRAME4((byte) 7),
    CLOSE_FRAME((byte) 8),
    PING_FRAME((byte) 9),
    PONG_FRAME((byte) 10),
    RESERVE_CONTROL_FRAME((byte) 11),
    RESERVE_CONTROL_FRAME1((byte) 12),
    RESERVE_CONTROL_FRAME2((byte) 13),
    RESERVE_CONTROL_FRAME3((byte) 14),
    RESERVE_CONTROL_FRAME4((byte) 15);

    private byte data;

    FrameType(byte data) {
        this.data = data;
    }

    public byte getByte() {
        return data;
    }
}
