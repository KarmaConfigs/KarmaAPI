package ml.karmaconfigs.api.common.utils.security.data;

/*
 * This file is part of KarmaAPI, licensed under the MIT License.
 *
 *  Copyright (c) karma (KarmaDev) <karmaconfigs@gmail.com>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.SecureRandom;

/**
 * Initialize the crypto API
 */
public class PBECryptoAPI {

    /**
     * The crypt password
     */
    private final String password;

    /**
     * The contents
     */
    private final byte[] content;

    /**
     * Initialize the crypt API
     *
     * @param pwd the password
     * @param ctn the content
     */
    public PBECryptoAPI(final String pwd, final byte[] ctn) {
        this.password = pwd;
        this.content = ctn;
    }

    /**
     * Generate a salt token
     *
     * @return a sal token
     */
    public final byte[] generateSALT() {
        SecureRandom r = new SecureRandom();
        byte[] newSeed = r.generateSeed(8);
        r.setSeed(newSeed);
        byte[] saltValue = new byte[8];
        r.nextBytes(saltValue);
        return saltValue;
    }

    /**
     * Encrypt the data
     *
     * @param salt the salt token
     * @return the encrypted data
     * @throws Exception if something goes wrong
     */
    public final byte[] encrypt(final byte[] salt) throws Exception {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 20);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(this.password.toCharArray());
        SecretKey key = keyFactory.generateSecret(pbeKeySpec);
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(1, key, pbeParamSpec);
        return pbeCipher.doFinal(this.content);
    }

    /**
     * Decrypt the data
     *
     * @param salt the salt token
     * @return the decrypted data
     * @throws Exception if something goes wrong
     */
    public final byte[] decrypt(final byte[] salt) throws Exception {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 20);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(this.password.toCharArray());
        SecretKey key = keyFactory.generateSecret(pbeKeySpec);
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(2, key, pbeParamSpec);
        return pbeCipher.doFinal(this.content);
    }
}
