package com.goslint_judge_movil_backend.goslint_judge_movil_backend.util;
import java.util.Base64;
import java.util.Scanner;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * AES.java
 * Implementación AES-128 (Rijndael) completa desde cero en Java.
 * Soporta ECB y CBC. Usa PKCS#7 padding.
 *
 * NOTAS:
 * - Para pruebas: provee una clave de 16 bytes (por ejemplo "1234567890123456") o
 *   escribe una frase; el programa truncará/expandirá a 16 bytes (no es KDF seguro).
 * - No usa javax.crypto ni Cipher.
 */
public class AES {

    // --- constantes AES ---
    private static final int Nb = 4; // columnas de estado (siempre 4 para AES)
    private static final int Nk = 4; // palabras en la clave (4 para AES-128)
    private static final int Nr = 10; // rondas para AES-128

    private static final int[] sbox = new int[] {
        // 256-byte S-box
        0x63,0x7c,0x77,0x7b,0xf2,0x6b,0x6f,0xc5,0x30,0x01,0x67,0x2b,0xfe,0xd7,0xab,0x76,
        0xca,0x82,0xc9,0x7d,0xfa,0x59,0x47,0xf0,0xad,0xd4,0xa2,0xaf,0x9c,0xa4,0x72,0xc0,
        0xb7,0xfd,0x93,0x26,0x36,0x3f,0xf7,0xcc,0x34,0xa5,0xe5,0xf1,0x71,0xd8,0x31,0x15,
        0x04,0xc7,0x23,0xc3,0x18,0x96,0x05,0x9a,0x07,0x12,0x80,0xe2,0xeb,0x27,0xb2,0x75,
        0x09,0x83,0x2c,0x1a,0x1b,0x6e,0x5a,0xa0,0x52,0x3b,0xd6,0xb3,0x29,0xe3,0x2f,0x84,
        0x53,0xd1,0x00,0xed,0x20,0xfc,0xb1,0x5b,0x6a,0xcb,0xbe,0x39,0x4a,0x4c,0x58,0xcf,
        0xd0,0xef,0xaa,0xfb,0x43,0x4d,0x33,0x85,0x45,0xf9,0x02,0x7f,0x50,0x3c,0x9f,0xa8,
        0x51,0xa3,0x40,0x8f,0x92,0x9d,0x38,0xf5,0xbc,0xb6,0xda,0x21,0x10,0xff,0xf3,0xd2,
        0xcd,0x0c,0x13,0xec,0x5f,0x97,0x44,0x17,0xc4,0xa7,0x7e,0x3d,0x64,0x5d,0x19,0x73,
        0x60,0x81,0x4f,0xdc,0x22,0x2a,0x90,0x88,0x46,0xee,0xb8,0x14,0xde,0x5e,0x0b,0xdb,
        0xe0,0x32,0x3a,0x0a,0x49,0x06,0x24,0x5c,0xc2,0xd3,0xac,0x62,0x91,0x95,0xe4,0x79,
        0xe7,0xc8,0x37,0x6d,0x8d,0xd5,0x4e,0xa9,0x6c,0x56,0xf4,0xea,0x65,0x7a,0xae,0x08,
        0xba,0x78,0x25,0x2e,0x1c,0xa6,0xb4,0xc6,0xe8,0xdd,0x74,0x1f,0x4b,0xbd,0x8b,0x8a,
        0x70,0x3e,0xb5,0x66,0x48,0x03,0xf6,0x0e,0x61,0x35,0x57,0xb9,0x86,0xc1,0x1d,0x9e,
        0xe1,0xf8,0x98,0x11,0x69,0xd9,0x8e,0x94,0x9b,0x1e,0x87,0xe9,0xce,0x55,0x28,0xdf,
        0x8c,0xa1,0x89,0x0d,0xbf,0xe6,0x42,0x68,0x41,0x99,0x2d,0x0f,0xb0,0x54,0xbb,0x16
    };

    private static final int[] invSbox = new int[] {
        // inverse S-box
        0x52,0x09,0x6a,0xd5,0x30,0x36,0xa5,0x38,0xbf,0x40,0xa3,0x9e,0x81,0xf3,0xd7,0xfb,
        0x7c,0xe3,0x39,0x82,0x9b,0x2f,0xff,0x87,0x34,0x8e,0x43,0x44,0xc4,0xde,0xe9,0xcb,
        0x54,0x7b,0x94,0x32,0xa6,0xc2,0x23,0x3d,0xee,0x4c,0x95,0x0b,0x42,0xfa,0xc3,0x4e,
        0x08,0x2e,0xa1,0x66,0x28,0xd9,0x24,0xb2,0x76,0x5b,0xa2,0x49,0x6d,0x8b,0xd1,0x25,
        0x72,0xf8,0xf6,0x64,0x86,0x68,0x98,0x16,0xd4,0xa4,0x5c,0xcc,0x5d,0x65,0xb6,0x92,
        0x6c,0x70,0x48,0x50,0xfd,0xed,0xb9,0xda,0x5e,0x15,0x46,0x57,0xa7,0x8d,0x9d,0x84,
        0x90,0xd8,0xab,0x00,0x8c,0xbc,0xd3,0x0a,0xf7,0xe4,0x58,0x05,0xb8,0xb3,0x45,0x06,
        0xd0,0x2c,0x1e,0x8f,0xca,0x3f,0x0f,0x02,0xc1,0xaf,0xbd,0x03,0x01,0x13,0x8a,0x6b,
        0x3a,0x91,0x11,0x41,0x4f,0x67,0xdc,0xea,0x97,0xf2,0xcf,0xce,0xf0,0xb4,0xe6,0x73,
        0x96,0xac,0x74,0x22,0xe7,0xad,0x35,0x85,0xe2,0xf9,0x37,0xe8,0x1c,0x75,0xdf,0x6e,
        0x47,0xf1,0x1a,0x71,0x1d,0x29,0xc5,0x89,0x6f,0xb7,0x62,0x0e,0xaa,0x18,0xbe,0x1b,
        0xfc,0x56,0x3e,0x4b,0xc6,0xd2,0x79,0x20,0x9a,0xdb,0xc0,0xfe,0x78,0xcd,0x5a,0xf4,
        0x1f,0xdd,0xa8,0x33,0x88,0x07,0xc7,0x31,0xb1,0x12,0x10,0x59,0x27,0x80,0xec,0x5f,
        0x60,0x51,0x7f,0xa9,0x19,0xb5,0x4a,0x0d,0x2d,0xe5,0x7a,0x9f,0x93,0xc9,0x9c,0xef,
        0xa0,0xe0,0x3b,0x4d,0xae,0x2a,0xf5,0xb0,0xc8,0xeb,0xbb,0x3c,0x83,0x53,0x99,0x61,
        0x17,0x2b,0x04,0x7e,0xba,0x77,0xd6,0x26,0xe1,0x69,0x14,0x63,0x55,0x21,0x0c,0x7d
    };

    private static final int[] Rcon = new int[] {
        0x00,0x01,0x02,0x04,0x08,0x10,0x20,0x40,0x80,0x1B,0x36
    };

    // --- operaciones Galois ---
    private static int xtime(int a) {
        a &= 0xFF;
        return ((a << 1) ^ (((a >>> 7) & 1) * 0x1b)) & 0xFF;
    }

    private static int mul(int a, int b) {
        // multiplicación en GF(2^8)
        int res = 0;
        a &= 0xFF; b &= 0xFF;
        while (b != 0) {
            if ((b & 1) != 0) res ^= a;
            a = xtime(a);
            b >>>= 1;
        }
        return res & 0xFF;
    }

    // --- key expansion ---
    public static byte[] expandKey(byte[] key) {
        // key: 16 bytes (AES-128). Retorna key schedule de 176 bytes (11*16)
        int expandedKeySize = Nb * (Nr + 1) * 4; // 176
        byte[] expanded = new byte[expandedKeySize];
        System.arraycopy(key, 0, expanded, 0, 16);

        int bytesGenerated = 16;
        int rconIteration = 1;
        byte[] temp = new byte[4];

        while (bytesGenerated < expandedKeySize) {
            // last 4 bytes
            for (int i = 0; i < 4; i++) temp[i] = expanded[bytesGenerated - 4 + i];

            if (bytesGenerated % 16 == 0) {
                // rotate
                byte t = temp[0];
                temp[0] = temp[1];
                temp[1] = temp[2];
                temp[2] = temp[3];
                temp[3] = t;
                // sub bytes
                for (int i = 0; i < 4; i++) temp[i] = (byte) (sbox[temp[i] & 0xFF]);
                // Rcon
                temp[0] ^= (byte) (Rcon[rconIteration] & 0xFF);
                rconIteration++;
            }

            for (int i = 0; i < 4; i++) {
                expanded[bytesGenerated] = (byte) (expanded[bytesGenerated - 16] ^ temp[i]);
                bytesGenerated++;
            }
        }

        return expanded;
    }

    // --- core AES round ops ---
    private static void subBytes(byte[] state) {
        for (int i = 0; i < state.length; i++) state[i] = (byte) (sbox[state[i] & 0xFF]);
    }

    private static void invSubBytes(byte[] state) {
        for (int i = 0; i < state.length; i++) state[i] = (byte) (invSbox[state[i] & 0xFF]);
    }

    private static void shiftRows(byte[] state) {
        // state is column-major 4x4 (state[ c*4 + r ])
        byte[] temp = new byte[16];
        // row 0 no cambia
        temp[0]  = state[0];
        temp[4]  = state[4];
        temp[8]  = state[8];
        temp[12] = state[12];
        // row1 shift left 1
        temp[1]  = state[5];
        temp[5]  = state[9];
        temp[9]  = state[13];
        temp[13] = state[1];
        // row2 shift left 2
        temp[2]  = state[10];
        temp[6]  = state[14];
        temp[10] = state[2];
        temp[14] = state[6];
        // row3 shift left 3
        temp[3]  = state[15];
        temp[7]  = state[3];
        temp[11] = state[7];
        temp[15] = state[11];

        System.arraycopy(temp, 0, state, 0, 16);
    }

    private static void invShiftRows(byte[] state) {
        byte[] temp = new byte[16];
        // row 0
        temp[0]  = state[0];
        temp[4]  = state[4];
        temp[8]  = state[8];
        temp[12] = state[12];
        // row1 shift right 1
        temp[1]  = state[13];
        temp[5]  = state[1];
        temp[9]  = state[5];
        temp[13] = state[9];
        // row2 shift right 2
        temp[2]  = state[10];
        temp[6]  = state[14];
        temp[10] = state[2];
        temp[14] = state[6];
        // row3 shift right 3
        temp[3]  = state[7];
        temp[7]  = state[11];
        temp[11] = state[15];
        temp[15] = state[3];

        System.arraycopy(temp, 0, state, 0, 16);
    }

    private static void mixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int col = c * 4;
            int a0 = state[col] & 0xFF;
            int a1 = state[col+1] & 0xFF;
            int a2 = state[col+2] & 0xFF;
            int a3 = state[col+3] & 0xFF;

            int r0 = (mul(a0,2) ^ mul(a1,3) ^ a2 ^ a3) & 0xFF;
            int r1 = (a0 ^ mul(a1,2) ^ mul(a2,3) ^ a3) & 0xFF;
            int r2 = (a0 ^ a1 ^ mul(a2,2) ^ mul(a3,3)) & 0xFF;
            int r3 = (mul(a0,3) ^ a1 ^ a2 ^ mul(a3,2)) & 0xFF;

            state[col]   = (byte) r0;
            state[col+1] = (byte) r1;
            state[col+2] = (byte) r2;
            state[col+3] = (byte) r3;
        }
    }

    private static void invMixColumns(byte[] state) {
        for (int c = 0; c < 4; c++) {
            int col = c * 4;
            int a0 = state[col] & 0xFF;
            int a1 = state[col+1] & 0xFF;
            int a2 = state[col+2] & 0xFF;
            int a3 = state[col+3] & 0xFF;

            int r0 = (mul(a0,0x0e) ^ mul(a1,0x0b) ^ mul(a2,0x0d) ^ mul(a3,0x09)) & 0xFF;
            int r1 = (mul(a0,0x09) ^ mul(a1,0x0e) ^ mul(a2,0x0b) ^ mul(a3,0x0d)) & 0xFF;
            int r2 = (mul(a0,0x0d) ^ mul(a1,0x09) ^ mul(a2,0x0e) ^ mul(a3,0x0b)) & 0xFF;
            int r3 = (mul(a0,0x0b) ^ mul(a1,0x0d) ^ mul(a2,0x09) ^ mul(a3,0x0e)) & 0xFF;

            state[col]   = (byte) r0;
            state[col+1] = (byte) r1;
            state[col+2] = (byte) r2;
            state[col+3] = (byte) r3;
        }
    }

    private static void addRoundKey(byte[] state, byte[] roundKey, int round) {
        // roundKey offset = round * 16
        int off = round * 16;
        for (int i = 0; i < 16; i++) {
            state[i] ^= roundKey[off + i];
        }
    }

    // --- block encrypt/decrypt (16 bytes) ---
    private static byte[] encryptBlock(byte[] in, byte[] expandedKey) {
        byte[] state = new byte[16];
        System.arraycopy(in, 0, state, 0, 16);

        addRoundKey(state, expandedKey, 0);

        for (int round = 1; round < Nr; round++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, expandedKey, round);
        }

        subBytes(state);
        shiftRows(state);
        addRoundKey(state, expandedKey, Nr);

        return state;
    }

    private static byte[] decryptBlock(byte[] in, byte[] expandedKey) {
        byte[] state = new byte[16];
        System.arraycopy(in, 0, state, 0, 16);

        addRoundKey(state, expandedKey, Nr);
        invShiftRows(state);
        invSubBytes(state);

        for (int round = Nr - 1; round >= 1; round--) {
            addRoundKey(state, expandedKey, round);
            invMixColumns(state);
            invShiftRows(state);
            invSubBytes(state);
        }

        addRoundKey(state, expandedKey, 0);
        return state;
    }

    // --- padding PKCS#7 ---
    private static byte[] pkcs7Pad(byte[] data) {
        int blockSize = 16;
        int pad = blockSize - (data.length % blockSize);
        if (pad == 0) pad = blockSize;
        byte[] out = Arrays.copyOf(data, data.length + pad);
        for (int i = data.length; i < out.length; i++) out[i] = (byte) pad;
        return out;
    }

    private static byte[] pkcs7Unpad(byte[] data) throws Exception {
        if (data.length == 0 || data.length % 16 != 0) throw new Exception("Invalid padded data");
        int pad = data[data.length - 1] & 0xFF;
        if (pad < 1 || pad > 16) throw new Exception("Invalid padding");
        for (int i = data.length - pad; i < data.length; i++) {
            if ((data[i] & 0xFF) != pad) throw new Exception("Invalid padding bytes");
        }
        return Arrays.copyOf(data, data.length - pad);
    }

    // --- ECB mode ---
    public static byte[] encryptECB(byte[] plaintext, byte[] expandedKey) {
        byte[] padded = pkcs7Pad(plaintext);
        byte[] out = new byte[padded.length];
        for (int i = 0; i < padded.length; i += 16) {
            byte[] block = Arrays.copyOfRange(padded, i, i + 16);
            byte[] enc = encryptBlock(block, expandedKey);
            System.arraycopy(enc, 0, out, i, 16);
        }
        return out;
    }

    private static byte[] decryptECB(byte[] ciphertext, byte[] expandedKey) throws Exception {
        if (ciphertext.length % 16 != 0) throw new Exception("Ciphertext not multiple of block size");
        byte[] out = new byte[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i += 16) {
            byte[] block = Arrays.copyOfRange(ciphertext, i, i + 16);
            byte[] dec = decryptBlock(block, expandedKey);
            System.arraycopy(dec, 0, out, i, 16);
        }
        return pkcs7Unpad(out);
    }

    // --- CBC mode ---
    private static byte[] encryptCBC(byte[] plaintext, byte[] expandedKey, byte[] iv) {
        byte[] padded = pkcs7Pad(plaintext);
        byte[] out = new byte[padded.length];
        byte[] prev = Arrays.copyOf(iv, 16);

        for (int i = 0; i < padded.length; i += 16) {
            byte[] block = Arrays.copyOfRange(padded, i, i + 16);
            byte[] xored = new byte[16];
            for (int j = 0; j < 16; j++) xored[j] = (byte) (block[j] ^ prev[j]);
            byte[] enc = encryptBlock(xored, expandedKey);
            System.arraycopy(enc, 0, out, i, 16);
            prev = enc;
        }
        return out;
    }

    private static byte[] decryptCBC(byte[] ciphertext, byte[] expandedKey, byte[] iv) throws Exception {
        if (ciphertext.length % 16 != 0) throw new Exception("Ciphertext not multiple of block size");
        byte[] out = new byte[ciphertext.length];
        byte[] prev = Arrays.copyOf(iv, 16);

        for (int i = 0; i < ciphertext.length; i += 16) {
            byte[] block = Arrays.copyOfRange(ciphertext, i, i + 16);
            byte[] dec = decryptBlock(block, expandedKey);
            byte[] plain = new byte[16];
            for (int j = 0; j < 16; j++) plain[j] = (byte) (dec[j] ^ prev[j]);
            System.arraycopy(plain, 0, out, i, 16);
            prev = block;
        }

        return pkcs7Unpad(out);
    }

    // --- util: prepare 16-byte key from password (simple) ---
    public static byte[] prepareKey(String keyStr) {
        // Simple: UTF-8 bytes, truncated or zero-padded to 16 bytes.
        byte[] kb = keyStr.getBytes();
        byte[] k = new byte[16];
        for (int i = 0; i < 16; i++) {
            k[i] = i < kb.length ? kb[i] : 0;
        }
        return k;
    }

    // --- main CLI ---
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("AES-128 (Rijndael) implementado desde cero");
            System.out.print("Modo (ECB/CBC) [ECB]: ");
            String modo = sc.nextLine().trim();
            if (modo.isEmpty()) modo = "ECB";
            modo = modo.toUpperCase();

            System.out.print("Acción (E para encriptar / D para desencriptar) [E]: ");
            String accion = sc.nextLine().trim();
            if (accion.isEmpty()) accion = "E";
            accion = accion.toUpperCase();

            System.out.print("Clave (16 bytes recomendados, por ejemplo '1234567890123456'): ");
            String keyStr = sc.nextLine();
            if (keyStr.isEmpty()) {
                System.out.println("Usando clave por defecto '0123456789abcdef'");
                keyStr = "0123456789abcdef";
            }
            byte[] key = prepareKey(keyStr);
            byte[] expandedKey = expandKey(key);

            SecureRandom rng = new SecureRandom();

            if (accion.equals("E")) {
                System.out.print("Texto a encriptar: ");
                String texto = sc.nextLine();
                byte[] plain = texto.getBytes();

                byte[] cipher;
                byte[] iv = null;
                if (modo.equals("CBC")) {
                    iv = new byte[16];
                    rng.nextBytes(iv);
                    cipher = encryptCBC(plain, expandedKey, iv);
                    // Prepend IV for convenience
                    byte[] combined = new byte[iv.length + cipher.length];
                    System.arraycopy(iv, 0, combined, 0, iv.length);
                    System.arraycopy(cipher, 0, combined, iv.length, cipher.length);
                    System.out.println("Cipher (Base64, IV prefijado si CBC):");
                    System.out.println(Base64.getEncoder().encodeToString(combined));
                } else {
                    cipher = encryptECB(plain, expandedKey);
                    System.out.println("Cipher (Base64):");
                    System.out.println(Base64.getEncoder().encodeToString(cipher));
                }

            } else if (accion.equals("D")) {
                System.out.print("Texto cifrado (Base64): ");
                String b64 = sc.nextLine().trim();
                byte[] cipherAll = Base64.getDecoder().decode(b64);

                byte[] plain;
                if (modo.equals("CBC")) {
                    if (cipherAll.length < 16) {
                        System.out.println("Datos demasiado cortos para CBC con IV prefijado.");
                        return;
                    }
                    byte[] iv = Arrays.copyOfRange(cipherAll, 0, 16);
                    byte[] cipher = Arrays.copyOfRange(cipherAll, 16, cipherAll.length);
                    plain = decryptCBC(cipher, expandedKey, iv);
                } else {
                    plain = decryptECB(cipherAll, expandedKey);
                }

                System.out.println("Texto desencriptado:");
                System.out.println(new String(plain));
            } else {
                System.out.println("Acción desconocida.");
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
