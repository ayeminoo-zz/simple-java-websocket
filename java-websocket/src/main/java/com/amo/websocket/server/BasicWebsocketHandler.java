package com.amo.websocket.server;

import com.amo.utility.SubArrayCollection;
import com.amo.websocket.Frame;
import com.amo.websocket.FrameType;
import com.amo.websocket.api.Session;
import com.amo.websocket.exception.BufferOverFlow;

import java.io.IOException;

/**
 * Created by ayeminoo on 1/5/18.
 */
public class BasicWebsocketHandler {

    private Session session;

    private byte[] buffer = new byte[0];
    private boolean isTextMessage = false;

    public BasicWebsocketHandler(Session session) {
        this.session = session;
        new Thread() {
            Frame frame = null;

            @Override
            public void run() {
                try {
                    while (!session.isClose()) {
                        frame = session.getFrameReader().read();
                        onReceive(frame);
                    }

                } catch (IOException e) {
                    e.printStackTrace(BasicContainer.getDebugStream());
                }
            }
        }.start();
    }

    protected void onReceive(Frame frame) {

        switch (frame.getFrameType()) {
            case PING_FRAME:
                sendPong();
                break;
            case TEXT_FRAME:
                onReceiveData(frame);
                break;
            case BINARY_FRAME:
                onReceiveData(frame);
                break;
            case PONG_FRAME:
                break;
            case CLOSE_FRAME:
                onReceiveClose(frame);
                break;
            default:
                BasicContainer.debug("Receive unsupported frame type");
        }

    }

    protected void onReceiveData(Frame frame) {
        if (buffer.length == 0) isTextMessage = frame.getFrameType() == FrameType.TEXT_FRAME;
        int length = buffer.length + frame.getPayload().length;
        if (length > session.getMaxBufferSize()) throw new BufferOverFlow();
        byte[] tmp = new byte[length];
        System.arraycopy(buffer, 0, tmp, 0, buffer.length);
        System.arraycopy(frame.getPayload(), 0, tmp, buffer.length, frame.getPayload().length);
        buffer = tmp;

        if (frame.isFinalSegment()) {
            byte[] data = buffer;
            buffer = new byte[0];
            if (isTextMessage) session.getEndpoint().onTextMessage(new String(data));
            else session.getEndpoint().onBinaryMessage(data);
        }
    }

    protected void onReceiveClose(Frame frame) {
        try {
            session.getFrameReader().close();
            session.close();
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
        //take close reason from frame a
        //create proper closeReason code from it
        //then send it to endpoint
        //todo:
        session.getEndpoint().onClose(new javax.websocket.CloseReason(javax.websocket.CloseReason.CloseCodes.NORMAL_CLOSURE,
                "received close request from other endpoint"));
    }

    protected void sendPong() {
        try {
            session.getFrameWriter().write(new PongFrame());
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
    }

    protected void sendClose() {
        try {
            //todo: add reason
            session.getFrameWriter().write(new CloseFrame());
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
    }

    public void sendMessage(String message) {
        try {
            byte[] bytes = message.getBytes("UTF-8");
            splitAndWrite(bytes, FrameType.TEXT_FRAME);
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
    }

    //splitting into 125 chunks
    //todo: implement longer chunks later
    private void splitAndWrite(byte[]data, FrameType frameType) throws IOException {
        SubArrayCollection arrayCollection = new SubArrayCollection(data, 125);
        for (int i = 0; i < arrayCollection.getSubArrays().size(); i++) {
            SubArrayCollection.SubArray subArray = arrayCollection.getSubArrays().get(i);
            boolean isFinal = i == arrayCollection.getSubArrays().size() - 1;
            FrameType type = null;
            if(i == 0){
                type = frameType;
            }else{
                type = FrameType.CONTINUE_FRAME;
            }
            session.getFrameWriter().write(new BasicFrame(isFinal,
                    false,
                    false,
                    false,
                    false,
                    type,
                    (byte) subArray.getSubArray().length, null, subArray.getSubArray()
            ));
        }
    }

    public void sendBinaryMessage(byte[] data) {
        try {
            splitAndWrite(data, FrameType.BINARY_FRAME);
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
    }

}
