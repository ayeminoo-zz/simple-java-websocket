package com.amo.websocket.server;

import com.amo.websocket.Frame;
import com.amo.websocket.FrameType;
import com.amo.websocket.exception.InvalidFrameException;

/**
 * Created by ayeminoo on 1/14/18.
 */
public class FrameUtils {
    static boolean isControlFrame(Frame frame){
        return isControlFrame(frame.getFrameType());
    }

    static boolean isControlFrame(FrameType frameType){
        int type = frameType.getByte();
        if( type >=0 && type <=7) return false;
        if(type >=8 && type <=15) return true;
        throw new InvalidFrameException();
    }
}
