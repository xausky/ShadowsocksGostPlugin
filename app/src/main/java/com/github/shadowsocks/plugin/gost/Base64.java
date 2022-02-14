package com.github.shadowsocks.plugin.gost;

public class Base64 {
    private char paddingChar = '=';

    public void setPaddingChar(char c) throws Base64Exception {
        if (
                (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') ||
                (c >= '0' && c <= '9') ||
                c == '+' || c == '/'
        ) throw new Base64Exception("INVALID_PADDING_CHAR");
        this.paddingChar = c;
    }

    public static class Base64Exception extends Exception {
        String msg;
        @Override
        public String getMessage() {
            return this.msg;
        }
        Base64Exception(String msg) {
            this.msg = msg;
        }
    }

    public byte[] decode(String encoded) throws Base64Exception {
        char[] input = new char[encoded.length()];
        encoded.getChars(0, encoded.length(), input, 0);
        return this.decode(input);
    }
    public byte[] decode(char[] input) throws Base64Exception {
        if (input.length % 4 != 0)
            throw new Base64Exception("BASE64_DECODE_INVALID_LENGTH");
        if (input.length == 0) return new byte[0];
        int endPos = 0;
        for (int i = input.length - 1; i >= input.length - 4; i--) {
            char c = input[i];
            if (
                    (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') ||
                    (c >= '0' && c <= '9') ||
                    c == '+' || c == '/'
            ) {
                endPos = i + 1;
                break;
            } else if (c != this.paddingChar) {
                throw new Base64Exception("BASE64_DECODE_INVALID_CHAR");
            } else if (i == input.length - 4) {
                throw new Base64Exception("BASE64_DECODE_INVALID_PADDING");
            }
        }
        int resultLen = (endPos * 6 + 8 - 1) / 8;
        byte[] result = new byte[resultLen];
        for (int i = 0, o = 0, buf = 0; i < endPos; i++) {
            char c = input[i];
            if (c >= 'A' && c <= 'Z') {
                c -= 'A';
            } else if (c >= 'a' && c <= 'z') {
                c += 26 - 'a';
            } else if (c >= '0' && c <= '9') {
                c += 26 + 26 - '0';
            } else if (c == '+') {
                c = 26 + 26 + 10;
            } else if (c == '/') {
                c = 26 + 26 + 10 + 1;
            } else throw new Base64Exception("BASE64_DECODE_INVALID_CHAR");
            buf |= (((int) c) & 0xFF) << (3 - (i % 4)) * 6;
            if ((i + 1) % 4 == 0 || i == endPos - 1) {
                for (int j = 0; j < 3 && o < resultLen; j++, o++) {
                    result[o] = (byte) ((buf >> (2 - j) * 8) & 0xFF);
                }
                buf = 0;
            }
        }
        return result;
    }
    public String encode(byte[] bin) {
        int resultLen = (bin.length * 8 + 6 - 1) / 6;
        int outputLen = (resultLen + 4 - 1) / 4;
        outputLen *= 4;
        char[] output = new char[outputLen];
        for (int i = 0, o = 0, buf = 0; i < bin.length; i++) {
            buf |= (((int) bin[i]) & 0xFF) << (2 - (i % 3)) * 8;
            if ((i + 1) % 3 == 0 || i == bin.length - 1) {
                for (int j = 0; j < 4 && o < resultLen; j++, o++) {
                    int c = (buf >> (3 - j) * 6) & 0x3F;
                    if (c < 26) {
                        output[o] = (char) ('A' + c);
                    } else if (c < 26 + 26) {
                        output[o] = (char) ('a' + c - 26);
                    } else if (c < 26 + 26 + 10) {
                        output[o] = (char) ('0' + c - (26 + 26));
                    } else if (c == 26 + 26 + 10) {
                        output[o] = '+';
                    } else { // always (c == 26 + 26 + 10 + 1)
                        output[o] = '/';
                    }
                }
                buf = 0;
            }
        }
        for (int i = resultLen; i < outputLen; i++) {
            output[i] = this.paddingChar;
        }
        return new String(output);
    }
}
