package com.funniray.minimap.common.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import java.nio.charset.StandardCharsets;

public class NetworkUtils {
    private static final int MAX_VARINT_SIZE = 5;
    public static final short MAX_STRING_LENGTH = 32767;

    public static void writeVarInt(int value, ByteArrayDataOutput out) {
        while ((value & -128) != 0) {
            out.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        out.writeByte(value);
    }

    public static void writeUtf(String value, ByteArrayDataOutput out) {
        byte[] src = value.getBytes(StandardCharsets.UTF_8);

        if (src.length > MAX_STRING_LENGTH) {
            throw new RuntimeException("String too big (was " + src.length + " bytes encoded, max " + MAX_STRING_LENGTH + ")");
        } else {
            writeVarInt(src.length, out);
            out.write(src);
        }
    }

    public static int readVarInt(ByteArrayDataInput in) {
        int i = 0;
        int j = 0;

        byte b0;

        do {
            b0 = in.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > MAX_VARINT_SIZE) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);

        return i;
    }

    public static String readUtf(ByteArrayDataInput in) {
        int j = readVarInt(in);

        if (j > MAX_STRING_LENGTH * 4) {
            throw new RuntimeException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + MAX_STRING_LENGTH * 4 + ")");
        } else if (j < 0) {
            throw new RuntimeException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            byte[] out = new byte[j];
            in.readFully(out, 0, j);

            String s = new String(out);

            if (s.length() > MAX_STRING_LENGTH) {
                throw new RuntimeException("The received string length is longer than maximum allowed (" + j + " > " + MAX_STRING_LENGTH + ")");
            } else {
                return s;
            }
        }
    }

    public static byte booleanToByte(boolean bool) {
        if (bool) {
            return 1;
        } else {
            return 0;
        }
    }
}
