/*
* Copyright (C) 2021 Optic_Fusion1
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package optic_fusion1.packets.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class RSACrypter {

  public static int getRSAKeyLength() {
    int maxKeyLength = 2048;
    try {
      maxKeyLength = Cipher.getMaxAllowedKeyLength("RSA");
      if (maxKeyLength > 2048) {
        maxKeyLength = 2048;
      }
    } catch (Exception e) {
      //Should never occur
    }
    return maxKeyLength;
  }

  public static int getAESKeyLength() {
    int maxKeyLength = 128;
    try {
      maxKeyLength = Cipher.getMaxAllowedKeyLength("AES");
    } catch (Exception e) {
      //Should never occur
    }
    return maxKeyLength > 256 ? 256 : 128;
  }

  public static byte[] encrypt(final PublicKey publicKey, final byte[] toEncrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
    return encrypt(publicKey, toEncrypt, getAESKeyLength());
  }

  public static byte[] encrypt(final PublicKey publicKey, byte[] toEncrypt, final int aesKeyLength) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    keyGenerator.init(aesKeyLength);
    SecretKey secretKey = keyGenerator.generateKey();

    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    toEncrypt = cipher.doFinal(toEncrypt);

    cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    byte[] keyBytes = cipher.doFinal(secretKey.getEncoded());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeInt(keyBytes.length);
    dos.write(keyBytes);
    dos.writeInt(toEncrypt.length);
    dos.write(toEncrypt);

    return baos.toByteArray();
  }

  public static byte[] decrypt(final PrivateKey privateKey, byte[] toDecrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(toDecrypt);
    DataInputStream dis = new DataInputStream(bais);
    byte[] keyBytes = new byte[dis.readInt()];
    dis.read(keyBytes);
    toDecrypt = new byte[dis.readInt()];
    dis.read(toDecrypt);

    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    byte[] decryptedKey = cipher.doFinal(keyBytes);

    SecretKey secretKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");

    cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return cipher.doFinal(toDecrypt);
  }

  public static KeyPair generateKeyPair(final int keySize) throws NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(keySize);
    return keyPairGenerator.genKeyPair();
  }

  public static PublicKey initPublicKey(final byte[] bytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
    return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
  }

  public static PrivateKey initPrivateKey(final byte[] bytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
    return KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(bytes));
  }

}
