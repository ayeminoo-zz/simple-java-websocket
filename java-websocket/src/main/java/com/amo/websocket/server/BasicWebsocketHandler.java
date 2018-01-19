package com.amo.websocket.server;

import com.amo.utility.ArrayUtils;
import com.amo.utility.BitUtility;
import com.amo.utility.SubArrayCollection;
import com.amo.websocket.Frame;
import com.amo.websocket.FrameType;
import com.amo.websocket.api.Session;
import com.amo.websocket.exception.BufferOverFlow;
import com.amo.websocket.exception.InvalidFrameException;
import com.amo.websocket.exception.WebsocketException;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by ayeminoo on 1/5/18.
 */
public class BasicWebsocketHandler {

    private Session session;

    private byte[] buffer = new byte[0];
    private boolean isStart = true;
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
                } catch (WebsocketException e) {
                    e.printStackTrace(BasicContainer.getDebugStream());
                    session.getWebsocketHandler().sendClose(e.getCloseCode());
                    try {
                        session.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {
                    session.getWebsocketHandler().sendClose(CloseReason.CloseCodes.UNEXPECTED_CONDITION);
                    try {
                        session.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
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
        if (isStart && frame.getFrameType() == FrameType.TEXT_FRAME) {
            isTextMessage = true;
        } else if (isStart && frame.getFrameType() == FrameType.BINARY_FRAME) {
            isTextMessage = false;
        } else if (isStart) {
            //wrong frame since at the start of the message, message type has to be present
            throw new InvalidFrameException();
        }
        //if a frame is not the start of fragmented message, then the type has to be CONINTUE
        if (!isStart && frame.getFrameType() != FrameType.CONTINUE_FRAME) throw new InvalidFrameException();
        int length = buffer.length + frame.getPayload().length;
        if (length > session.getMaxBufferSize()) throw new BufferOverFlow();
        byte[] tmp = new byte[length];
        System.arraycopy(buffer, 0, tmp, 0, buffer.length);
        System.arraycopy(frame.getPayload(), 0, tmp, buffer.length, frame.getPayload().length);
        buffer = tmp;
        isStart = false;
        if (frame.isFinalSegment()) {
            byte[] data = buffer;
            isStart = true;
            buffer = new byte[0];
            if (isTextMessage) {
                if (BitUtility.validate(data)) {
                    session.getEndpoint().onTextMessage(new String(data));
                } else {
                    throw new WebsocketException(CloseReason.CloseCodes.NOT_CONSISTENT);
                }
            } else {
                session.getEndpoint().onBinaryMessage(data);
            }
        }
    }

    protected void onReceiveClose(Frame frame) {
        javax.websocket.CloseReason.CloseCode closeCode = null;
        if (frame.getPayload() != null && frame.getPayload().length == 1)
            throw new InvalidFrameException();
        try {
            if (frame.getPayload() != null && frame.getPayload().length != 0) {
                closeCode = CloseReason.CloseCodes.getCloseCode(ByteBuffer.wrap(
                        ArrayUtils.concatenate(new byte[]{0, 0}, new byte[]{frame.getPayload()[0], frame.getPayload()[1]})
                ).getInt());
                //check valid closecode
                if (!validateCloseCode(closeCode)) throw new InvalidFrameException();
                if (frame.getPayload().length > 2 && !BitUtility.validate(Arrays.copyOfRange(frame.getPayload(), 2, frame.getPayload().length - 2))) {
                    throw new InvalidFrameException();
                }
            }
            session.getWebsocketHandler().sendClose(closeCode);
            Thread.sleep(50);
            session.close();
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            throw new InvalidFrameException();
        }
        if (closeCode == null) closeCode = CloseReason.CloseCodes.NO_STATUS_CODE;
        session.getEndpoint().onClose(new javax.websocket.CloseReason(closeCode,
                "received close request from other endpoint"));
    }

    protected boolean validateCloseCode(CloseReason.CloseCode closeCode) {
        return (closeCode != CloseReason.CloseCodes.NO_STATUS_CODE &&
                closeCode != CloseReason.CloseCodes.RESERVED &&
                closeCode != CloseReason.CloseCodes.CLOSED_ABNORMALLY &&
                closeCode != CloseReason.CloseCodes.TLS_HANDSHAKE_FAILURE &&
                closeCode instanceof CloseReason.CloseCodes)
                ||
                (!(closeCode.getCode() >= 0 && closeCode.getCode() <= 2999) &&
                        closeCode.getCode() >= 3000 && closeCode.getCode() <= 4999);
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
            session.getFrameWriter().close();
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
    }

    public void sendMessage(String message) {
        System.out.println("Sending message " + message);
        try {
            byte[] bytes = message.getBytes("UTF-8");
            splitAndWrite(bytes, FrameType.TEXT_FRAME);
        } catch (IOException e) {
            e.printStackTrace(BasicContainer.getDebugStream());
        }
    }

    //splitting into 125 chunks
    //todo: implement longer chunks later
    private void splitAndWrite(byte[] data, FrameType frameType) throws IOException {
        SubArrayCollection arrayCollection = new SubArrayCollection(data, 125);
        for (int i = 0; i < arrayCollection.getSubArrays().size(); i++) {
            SubArrayCollection.SubArray subArray = arrayCollection.getSubArrays().get(i);
            boolean isFinal = i == arrayCollection.getSubArrays().size() - 1;
            FrameType type = null;
            if (i == 0) {
                type = frameType;
            } else {
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
