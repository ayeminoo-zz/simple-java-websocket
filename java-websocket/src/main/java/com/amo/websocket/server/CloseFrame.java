package com.amo.websocket.server;

import com.amo.websocket.FrameType;

import javax.websocket.CloseReason;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by ayeminoo on 1/7/18.
 */
public class CloseFrame extends BasicFrame {
    private CloseFrame(boolean finalSegment, boolean RSV1, boolean RSV2, boolean RSV3, boolean masked, FrameType frameType,
                       byte payloadLength, byte[] maskingKey, byte[] payload) {
        super(finalSegment, RSV1, RSV2, RSV3, masked, frameType, payloadLength, maskingKey, payload);
    }

    public CloseFrame(){
        this(true, false, false, false, false, FrameType.CLOSE_FRAME, (byte)0,
                null, null);
    }

    public CloseFrame(byte[] closeCode){
        this(true, false, false, false, false, FrameType.CLOSE_FRAME, (byte)2,
                null, closeCode);
    }

    public CloseFrame(CloseReason.CloseCode closeCode){
        this(true, false, false, false, false, FrameType.CLOSE_FRAME, (byte)2,
                null, closeCode != null ?
                        Arrays.copyOfRange(ByteBuffer.allocate(4).putInt(closeCode.getCode()).array(),2, 4)
                        : null);
    }
}
