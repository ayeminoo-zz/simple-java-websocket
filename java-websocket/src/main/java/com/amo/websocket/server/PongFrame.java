package com.amo.websocket.server;

import com.amo.websocket.FrameType;

/**
 * Created by ayeminoo on 1/7/18.
 */
public class PongFrame extends BasicFrame {
    private PongFrame(boolean finalSegment, boolean RSV1, boolean RSV2, boolean RSV3, boolean masked,
                      FrameType frameType, byte payloadLength, byte[] maskingKey, byte[] payload) {
        super(finalSegment, RSV1, RSV2, RSV3, masked, frameType, payloadLength, maskingKey, payload);
    }

    public PongFrame(){
        this(null);
    }

    public PongFrame(byte[] data){
        this(true, false, false, false, false, FrameType.PONG_FRAME, (byte)(data==null?0: data.length), null, data == null? null: data);

    }
}
