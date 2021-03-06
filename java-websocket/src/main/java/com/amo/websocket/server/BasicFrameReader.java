package com.amo.websocket.server;

import com.amo.utility.ArrayUtils;
import com.amo.websocket.Frame;
import com.amo.websocket.FrameReader;
import com.amo.websocket.FrameType;
import com.amo.websocket.exception.BufferOverFlow;
import com.amo.websocket.exception.InvalidFrameException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Created by ayeminoo on 1/4/18.
 */
public class BasicFrameReader implements FrameReader {
    private InputStream in;
    private int maxBufferSize;

    public BasicFrameReader(InputStream in, int maxBufferSize) {
        this.in = in;
        this.maxBufferSize = maxBufferSize;
    }
    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    /**
     * Implementation base on Base Framing Protocol
     * <p>
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-------+-+-------------+-------------------------------+
     * |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
     * |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
     * |N|V|V|V|       |S|             |   (if payload len==126/127)   |
     * | |1|2|3|       |K|             |                               |
     * +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
     * |     Extended payload length continued, if payload len == 127  |
     * + - - - - - - - - - - - - - - - +-------------------------------+
     * |                               |Masking-key, if MASK set to 1  |
     * +-------------------------------+-------------------------------+
     * | Masking-key (continued)       |          Payload Data         |
     * +-------------------------------- - - - - - - - - - - - - - - - +
     * :                     Payload Data continued ...                :
     * + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
     * |                     Payload Data continued ...                |
     * +---------------------------------------------------------------+
     *
     * @return
     */
    @Override
    public Frame read() throws IOException {
        boolean finalSegment;
        boolean RSV1;
        boolean RSV2;
        boolean RSV3;
        boolean masked;
        FrameType frameType;
        byte[] maskingKey = null;
        byte[] payload;


        BigInteger length;
        byte[] header = new byte[2];
        read(header);
        finalSegment = ((header[0] & 0b11111111) >>> 7) == 1;
        RSV1 = ((header[0] & 0b01111111) >> 6) == 1;
        RSV2 = ((header[0] & 0b00111111) >> 5) == 1;
        RSV3 = ((header[0] & 0b00011111) >> 4) == 1;
        masked = ((header[1] & 0b11111111) >>> 7) == 1;
        frameType = getFrameType(header[0]);

        //
        if(isReserveFrameType(frameType)){
            throw new InvalidFrameException();
        }

        //MUST be 0 unless an extension is negotiated that defines meanings
        //for non-zero values.
        if(RSV1 == true || RSV2 == true || RSV3 == true) throw new InvalidFrameException();

        //calculated payload length and read frame until the end of payload length segment
        final int payloadSegment = Byte.toUnsignedInt(header[1]) - 128;

        // All control frames MUST have a payload length of 125 bytes or less
        //and MUST NOT be fragmented.
        if(FrameUtils.isControlFrame(frameType) &&
                (finalSegment == false || payloadSegment > 125)){
            throw new InvalidFrameException();
        }

        //if 0-125, that is the payload length
        if (payloadSegment >= 0 && payloadSegment <= 125) {
            length = BigInteger.valueOf(payloadSegment);
        } else if (payloadSegment == 126) {
            // return frame[2] + frame[3] as 16 bit unsigned integer
            byte[] twoByte = new byte[2];
            read(twoByte);
            length = new BigInteger(ArrayUtils.concatenate(new byte[]{0, 0}, twoByte));
        } else if (payloadSegment == 127) {
            //If 127, the following 8 bytes interpreted as a 64-bit unsigned integer
            byte[] eightByte = new byte[8];
            read(eightByte);
            length = new BigInteger(ArrayUtils.concatenate(new byte[]{0, 0}, eightByte));
        } else {
            throw new InvalidFrameException();
        }

        //if masked, read masking key
        if (masked) {
            maskingKey = new byte[4];
            read(maskingKey);
        }

        //check frame is bigger than allowed buffer
        if (length.compareTo(BigInteger.valueOf(maxBufferSize)) > 0)
            throw new BufferOverFlow();
        byte[] rawData = new byte[length.intValue()];
        read(rawData);
        if (masked) { //if masked, then decoded it
            byte[] decoded = new byte[length.intValue()];
            for (int i = 0; i < rawData.length; i++) {
                decoded[i] = (byte) (rawData[i] ^ maskingKey[i & 0x3]);
            }
            payload = decoded;

        } else {
            payload = rawData;
        }
        return new BasicFrame(
                finalSegment,
                RSV1,
                RSV2,
                RSV3,
                masked,
                frameType,
                (byte) payloadSegment,
                maskingKey,
                payload
        );
    }

    private boolean isReserveFrameType(FrameType frameType){
        switch(frameType){
            case RESERVE_CONTROL_FRAME: return true;
            case RESERVE_CONTROL_FRAME1:return true;
            case RESERVE_CONTROL_FRAME2:return true;
            case RESERVE_CONTROL_FRAME3:return true;
            case RESERVE_CONTROL_FRAME4:return true;
            case RESERVE_NON_CONTROL_FRAME:return true;
            case RESERVE_NON_CONTROL_FRAME1:return true;
            case RESERVE_NON_CONTROL_FRAME2:return true;
            case RESERVE_NON_CONTROL_FRAME3:return true;
            case RESERVE_NON_CONTROL_FRAME4:return true;
            default: return false;
        }
    }

    @Override
    public void read(byte[] buffer) throws IOException {
        for(int i = 0; i< buffer.length; i++){
            int tmp = in.read();
            if(tmp == -1) throw new EOFException();
            buffer[i] = (byte) tmp;
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    private FrameType getFrameType(byte firstByte) {
        int optcode = firstByte;
        optcode = optcode << 28;
        optcode = optcode >>> 28;
        switch (optcode) {
            case 0:
                return FrameType.CONTINUE_FRAME;
            case 1:
                return FrameType.TEXT_FRAME;
            case 2:
                return FrameType.BINARY_FRAME;
            case 3:
                return FrameType.RESERVE_NON_CONTROL_FRAME;
            case 4:
                return FrameType.RESERVE_NON_CONTROL_FRAME;
            case 5:
                return FrameType.RESERVE_NON_CONTROL_FRAME;
            case 6:
                return FrameType.RESERVE_NON_CONTROL_FRAME;
            case 7:
                return FrameType.RESERVE_NON_CONTROL_FRAME;
            case 8:
                return FrameType.CLOSE_FRAME;
            case 9:
                return FrameType.PING_FRAME;
            case 10:
                return FrameType.PONG_FRAME;
            case 11:
                return FrameType.RESERVE_CONTROL_FRAME;
            case 12:
                return FrameType.RESERVE_CONTROL_FRAME;
            case 13:
                return FrameType.RESERVE_CONTROL_FRAME;
            case 14:
                return FrameType.RESERVE_CONTROL_FRAME;
            case 15:
                return FrameType.RESERVE_CONTROL_FRAME;
            default:
                throw new InvalidFrameException();
        }
    }
}
