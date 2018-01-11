package com.amo.websocket.server;

import com.amo.utility.BitUtility;
import com.amo.websocket.Frame;
import com.amo.websocket.FrameWriter;
import com.amo.websocket.exception.InvalidFrameException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ayeminoo on 1/5/18.
 */
public class BasicFrameWriter implements FrameWriter {
    private OutputStream out;

    public BasicFrameWriter(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(Frame frame) throws IOException {
        validate(frame);
        byte firstByte = BitUtility.toBytes(new boolean[]{frame.isFinalSegment(),
                frame.isRSV1(), frame.isRSV2(), frame.isRSV3(), false, false, false, false})[0];
        firstByte |= frame.getFrameType().getByte();
        byte secondByte = frame.getPayloadLength();
        byte[] extendedPayLoad = null;
        byte[] maskingKey = null;

        if (frame.isMasked()) {
            //need to merge with payload length
            secondByte |= ((byte) 0b10000000);
        }

        //write out first two byte
        out.write(new byte[]{firstByte, secondByte});

        int payloadSegment = Byte.toUnsignedInt(frame.getPayloadLength());
        if (payloadSegment == 126) {
            boolean[] bits = BitUtility.toBitArray(frame.getPayload().length, 16);
            out.write(BitUtility.toBytes(bits));
        } else if (payloadSegment == 127) {
            //8 byte
            //no need to check bigger size frame since 4 byte int will always less than 8 byte length
            boolean[] bits = BitUtility.toBitArray(frame.getPayload().length, 8 * 8);
            out.write(BitUtility.toBytes(bits));
        }

        //write out masking key
        if (frame.isMasked()) {
            out.write(frame.getMaskingKey());
        }
        if(frame.getPayloadLength() > 0)
            out.write(frame.getPayload());
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public void validate(Frame frame) {
        if ((frame.isMasked() && frame.getMaskingKey() == null)
                || (frame.getMaskingKey() != null && frame.getMaskingKey().length != 4)
                || (Byte.toUnsignedInt(frame.getPayloadLength()) == 126 && frame.getPayload().length > 0b1111111111111111)
                || (Byte.toUnsignedInt(frame.getPayloadLength()) > 127 || Byte.toUnsignedInt(frame.getPayloadLength()) < 0)
                ) throw new InvalidFrameException();
    }
}
