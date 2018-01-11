package com.amo.websocket.server;

import com.amo.websocket.FrameType;

/**
 * Created by ayeminoo on 1/7/18.
 */
public class CloseFrame extends BasicFrame {
    private CloseFrame(boolean finalSegment, boolean RSV1, boolean RSV2, boolean RSV3, boolean masked, FrameType frameType,
                       byte payloadLength, byte[] maskingKey, byte[] payload) {
        super(finalSegment, RSV1, RSV2, RSV3, masked, frameType, payloadLength, maskingKey, payload);
    }

    public CloseFrame(){
        //todo: add close reason
        this(true, false, false, false, false, FrameType.CLOSE_FRAME, (byte)0,
                null, null);
    }
}
