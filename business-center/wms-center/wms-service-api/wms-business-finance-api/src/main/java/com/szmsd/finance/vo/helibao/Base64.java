package com.szmsd.finance.vo.helibao;


public class Base64 {

    static final int CHUNK_SIZE = 76;

    static final byte[] CHUNK_SEPARATOR = "\r\n".getBytes();


    static final int BASELENGTH = 255;


    static final int LOOKUPLENGTH = 64;


    static final int EIGHTBIT = 8;

    static final int SIXTEENBIT = 16;


    static final int TWENTYFOURBITGROUP = 24;


    static final int FOURBYTE = 4;


    static final int SIGN = -128;

    static final byte PAD = (byte) '=';

    private static byte[] base64Alphabet = new byte[BASELENGTH];
    private static byte[] lookUpBase64Alphabet = new byte[LOOKUPLENGTH];
    static {
        for (int i = 0; i < BASELENGTH; i++) {
            base64Alphabet[i] = (byte) -1;
        }
        for (int i = 'Z'; i >= 'A'; i--) {
            base64Alphabet[i] = (byte) (i - 'A');
        }
        for (int i = 'z'; i >= 'a'; i--) {
            base64Alphabet[i] = (byte) (i - 'a' + 26);
        }
        for (int i = '9'; i >= '0'; i--) {
            base64Alphabet[i] = (byte) (i - '0' + 52);
        }

        base64Alphabet['+'] = 62;
        base64Alphabet['/'] = 63;

        for (int i = 0; i <= 25; i++) {
            lookUpBase64Alphabet[i] = (byte) ('A' + i);
        }

        for (int i = 26, j = 0; i <= 51; i++, j++) {
            lookUpBase64Alphabet[i] = (byte) ('a' + j);
        }

        for (int i = 52, j = 0; i <= 61; i++, j++) {
            lookUpBase64Alphabet[i] = (byte) ('0' + j);
        }

        lookUpBase64Alphabet[62] = (byte) '+';
        lookUpBase64Alphabet[63] = (byte) '/';
    }

    private static boolean isBase64(byte octect) {
        if (octect == PAD) {
            return true;
        } else if (base64Alphabet[octect] == -1) {
            return false;
        } else {
            return true;
        }
    }



    public static byte[] encodeBase64(byte[] binaryData) {
        return encodeBase64(binaryData, false);
    }


    public static byte[] decode(byte[] pArray) {
        return decodeBase64(pArray);
    }


    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
        int lengthDataBits = binaryData.length * EIGHTBIT;
        int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
        int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
        byte encodedData[] = null;
        int encodedDataLength = 0;
        int nbrChunks = 0;

        if (fewerThan24bits != 0) {
            encodedDataLength = (numberTriplets + 1) * 4;
        } else {
            encodedDataLength = numberTriplets * 4;
        }

        if (isChunked) {

            nbrChunks =
                (CHUNK_SEPARATOR.length == 0 ? 0 : (int) Math.ceil((float) encodedDataLength / CHUNK_SIZE));
            encodedDataLength += nbrChunks * CHUNK_SEPARATOR.length;
        }

        encodedData = new byte[encodedDataLength];

        byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

        int encodedIndex = 0;
        int dataIndex = 0;
        int i = 0;
        int nextSeparatorIndex = CHUNK_SIZE;
        int chunksSoFar = 0;

        for (i = 0; i < numberTriplets; i++) {
            dataIndex = i * 3;
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            b3 = binaryData[dataIndex + 2];


            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            byte val1 =
                ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 =
                ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);
            byte val3 =
                ((b3 & SIGN) == 0) ? (byte) (b3 >> 6) : (byte) ((b3) >> 6 ^ 0xfc);

            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] =
                lookUpBase64Alphabet[val2 | (k << 4)];
            encodedData[encodedIndex + 2] =
                lookUpBase64Alphabet[(l << 2) | val3];
            encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 0x3f];

            encodedIndex += 4;

            if (isChunked) {
                if (encodedIndex == nextSeparatorIndex) {
                    System.arraycopy(
                        CHUNK_SEPARATOR,
                        0,
                        encodedData,
                        encodedIndex,
                        CHUNK_SEPARATOR.length);
                    chunksSoFar++;
                    nextSeparatorIndex =
                        (CHUNK_SIZE * (chunksSoFar + 1)) +
                        (chunksSoFar * CHUNK_SEPARATOR.length);
                    encodedIndex += CHUNK_SEPARATOR.length;
                }
            }
        }

        dataIndex = i * 3;

        if (fewerThan24bits == EIGHTBIT) {
            b1 = binaryData[dataIndex];
            k = (byte) (b1 & 0x03);
            byte val1 =
                ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[k << 4];
            encodedData[encodedIndex + 2] = PAD;
            encodedData[encodedIndex + 3] = PAD;
        } else if (fewerThan24bits == SIXTEENBIT) {

            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            byte val1 =
                ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
            byte val2 =
                ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);

            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] =
                lookUpBase64Alphabet[val2 | (k << 4)];
            encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2];
            encodedData[encodedIndex + 3] = PAD;
        }

        if (isChunked) {
            if (chunksSoFar < nbrChunks) {
                System.arraycopy(
                    CHUNK_SEPARATOR,
                    0,
                    encodedData,
                    encodedDataLength - CHUNK_SEPARATOR.length,
                    CHUNK_SEPARATOR.length);
            }
        }

        return encodedData;
    }

    public static byte[] decodeBase64(byte[] base64Data) {
        base64Data = discardNonBase64(base64Data);

        if (base64Data.length == 0) {
            return new byte[0];
        }

        int numberQuadruple = base64Data.length / FOURBYTE;
        byte decodedData[] = null;
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;


        int encodedIndex = 0;
        int dataIndex = 0;
        {
            int lastData = base64Data.length;
            while (base64Data[lastData - 1] == PAD) {
                if (--lastData == 0) {
                    return new byte[0];
                }
            }
            decodedData = new byte[lastData - numberQuadruple];
        }

        for (int i = 0; i < numberQuadruple; i++) {
            dataIndex = i * 4;
            marker0 = base64Data[dataIndex + 2];
            marker1 = base64Data[dataIndex + 3];

            b1 = base64Alphabet[base64Data[dataIndex]];
            b2 = base64Alphabet[base64Data[dataIndex + 1]];

            if (marker0 != PAD && marker1 != PAD) {
                //No PAD e.g 3cQl
                b3 = base64Alphabet[marker0];
                b4 = base64Alphabet[marker1];

                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] =
                    (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
            } else if (marker0 == PAD) {
                //Two PAD e.g. 3c[Pad][Pad]
                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
            } else if (marker1 == PAD) {
                //One PAD e.g. 3cQ[Pad]
                b3 = base64Alphabet[marker0];

                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] =
                    (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            }
            encodedIndex += 3;
        }
        return decodedData;
    }

    static byte[] discardNonBase64(byte[] data) {
        byte groomedData[] = new byte[data.length];
        int bytesCopied = 0;

        for (int i = 0; i < data.length; i++) {
            if (isBase64(data[i])) {
                groomedData[bytesCopied++] = data[i];
            }
        }

        byte packedData[] = new byte[bytesCopied];

        System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);

        return packedData;
    }
    public static byte[] encode(byte[] pArray) {
        return encodeBase64(pArray, false);
    }


}
