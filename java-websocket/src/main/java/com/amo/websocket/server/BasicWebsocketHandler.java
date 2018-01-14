package com.amo.websocket.server;

import com.amo.utility.ArrayUtils;
import com.amo.utility.SubArrayCollection;
import com.amo.websocket.Frame;
import com.amo.websocket.FrameType;
import com.amo.websocket.api.Session;
import com.amo.websocket.exception.BufferOverFlow;
import com.amo.websocket.exception.InvalidFrameException;
import org.omg.CORBA.FREE_MEM;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.nio.ByteBuffer;

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
                }catch (InvalidFrameException e){
                    e.printStackTrace(BasicContainer.getDebugStream());
                    session.getWebsocketHandler().sendClose(CloseReason.CloseCodes.PROTOCOL_ERROR);
                    session.close();
                }catch (IOException e) {
                    e.printStackTrace(BasicContainer.getDebugStream());
                }
            }
        }.start();
    }

    protected void onReceive(Frame frame) {

        switch (frame.getFrameType()) {
            case PING_FRAME:
                onReceivePing(frame);
                break;
            case TEXT_FRAME:
                onReceiveData(frame);
                break;
            case CONTINUE_FRAME:
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
        if (buffer.length == 0 && frame.getFrameType() == FrameType.TEXT_FRAME) {
            isTextMessage = true;
        }else if(buffer.length == 0 && frame.getFrameType() == FrameType.BINARY_FRAME){
            isTextMessage = false;
        }else if(buffer.length == 0){
            //wrong frame since at the start of the message, message type has to be present
            throw new InvalidFrameException();
        }
        //if a frame is not the start of fragmented message, then the type has to be CONINTUE
        if(buffer.length > 0 && frame.getFrameType() != FrameType.CONTINUE_FRAME) throw new InvalidFrameException();
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

//    public static void main(String[]args){
//        ByteBuffer.wrap(new byte[]{0,0,1,2}).getInt();
//    }

    protected void onReceiveClose(Frame frame) {
        javax.websocket.CloseReason.CloseCode closeCode = null;
        try {
            if(frame.getPayload()!= null && frame.getPayload().length != 0){
                closeCode = CloseReason.CloseCodes.getCloseCode(ByteBuffer.wrap(
                        ArrayUtils.concatenate(new byte[]{0,0}, frame.getPayload())
                        ).getInt());
            }
            session.getWebsocketHandler().sendClose(closeCode);
            session.getFrameReader().close();
            session.close();
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
        //take close reason from frame a
        //create proper closeReason code from it
        //then send it to endpoint
        //todo:
        session.getEndpoint().onClose(new javax.websocket.CloseReason(closeCode,
                "received close request from other endpoint"));
    }

    protected void onReceivePing(Frame frame) {
        try {
            session.getFrameWriter().write(new PongFrame(frame.getPayload()));
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
    }

    protected void sendClose(CloseReason.CloseCode closeCode) {
        try {
            session.getFrameWriter().write(new CloseFrame(closeCode));
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
    }

    protected void sendClose() {
        try {
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
