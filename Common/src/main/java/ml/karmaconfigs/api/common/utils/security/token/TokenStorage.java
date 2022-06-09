package ml.karmaconfigs.api.common.utils.security.token;

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

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.file.KarmaMain;
import ml.karmaconfigs.api.common.karma.file.element.KarmaElement;
import ml.karmaconfigs.api.common.karma.file.element.KarmaObject;
import ml.karmaconfigs.api.common.utils.security.data.PBECryptoAPI;
import ml.karmaconfigs.api.common.utils.security.token.exception.TokenExpiredException;
import ml.karmaconfigs.api.common.utils.security.token.exception.TokenIncorrectPasswordException;
import ml.karmaconfigs.api.common.utils.security.token.exception.TokenNotFoundException;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Karma token storage
 */
@SuppressWarnings("unused")
public final class TokenStorage {

    /**
     * The token source
     */
    private final KarmaSource source;

    /**
     * Initialize the token storage
     *
     * @param src the token source
     */
    public TokenStorage(final KarmaSource src) {
        this.source = src;
    }

    /**
     * Destroy a token
     *
     * @param tokenID the token ID
     * @param password the token password
     */
    public void destroy(final UUID tokenID, final String password) {
        KarmaMain tokenFile = new KarmaMain(this.source, tokenID.toString().replace("-", ""), "cache", "tokens");
        if (tokenFile.exists() && tokenFile.isSet("token") && tokenFile.isSet("salt")) {
            KarmaElement storedToken = tokenFile.get("token");
            KarmaElement storedSalt = tokenFile.get("salt");

            if (storedToken.isString() && storedSalt.isString()) {
                byte[] salt = Base64.getUrlDecoder().decode(tokenFile.get("salt").getObjet().getString());
                if (!StringUtils.isNullOrEmpty(storedToken.getObjet().getString())) {
                    PBECryptoAPI api = new PBECryptoAPI(password, Base64.getUrlDecoder().decode(storedToken.getObjet().getString()));
                    try {
                        api.decrypt(salt);
                        tokenFile.delete();
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
    }

    /**
     * Store a token
     *
     * @param token the token
     * @param password the token password
     * @param expiration the token expiration date (null for no expiration)
     * @return the token ID
     */
    public UUID store(final String token, final String password, final Instant expiration) {
        UUID tokenID = UUID.nameUUIDFromBytes(Base64.getUrlDecoder().decode(token));
        KarmaMain tokenFile = new KarmaMain(this.source, tokenID.toString().replace("-", ""), "cache", "tokens");
        PBECryptoAPI api = new PBECryptoAPI(password, Base64.getUrlDecoder().decode(token));
        byte[] salt = api.generateSALT();
        tokenFile.set("salt", new KarmaObject(new String(Base64.getUrlEncoder().encode(salt))));
        try {
            tokenFile.set("token", new KarmaObject(new String(Base64.getUrlEncoder().encode(api.encrypt(salt)))));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        if (expiration != null) {
            tokenFile.set("expiration", new KarmaObject(expiration.toString()));
        } else {
            tokenFile.set("expiration", new KarmaObject("N/A"));
        }
        return tokenID;
    }

    /**
     * Load a token
     *
     * @param tokenID the token ID
     * @param password the token password
     * @return the token
     *
     * @throws TokenNotFoundException if the token couldn't be found
     * @throws TokenExpiredException if the token is expired
     * @throws TokenIncorrectPasswordException if the token password's is incorrect
     */
    public String load(final UUID tokenID, final String password) throws TokenNotFoundException, TokenExpiredException, TokenIncorrectPasswordException {
        KarmaMain tokenFile = new KarmaMain(this.source, tokenID.toString().replace("-", ""), "cache", "tokens");
        String token = null;
        Instant expiration = null;
        if (tokenFile.exists() && tokenFile.isSet("token") && tokenFile.isSet("salt")) {
            KarmaElement storedToken = tokenFile.get("token");
            KarmaElement storedSalt = tokenFile.get("salt");

            if (storedToken.isString() && storedSalt.isString()) {
                byte[] salt = Base64.getUrlDecoder().decode(storedSalt.getObjet().getString());
                if (!StringUtils.isNullOrEmpty(storedToken)) {
                    PBECryptoAPI api = new PBECryptoAPI(password, Base64.getUrlDecoder().decode(storedToken.getObjet().getString()));
                    try {
                        String tmp_token = new String(api.decrypt(salt));
                        if (tokenFile.isSet("expiration")) {
                            KarmaElement instant = tokenFile.get("expiration");
                            if (!instant.getObjet().getString().equalsIgnoreCase("N/A")) {
                                expiration = Instant.parse(instant.getObjet().getString());
                                token = tmp_token;
                            }
                        }
                    } catch (Throwable ex) {
                        throw new TokenIncorrectPasswordException(tokenID);
                    }
                }
            }
        }
        if (token != null) {
            if (expiration == null || Instant.now().isBefore(expiration))
                return token;
            throw new TokenExpiredException(tokenID);
        }
        throw new TokenNotFoundException(tokenID);
    }

    /**
     * Get the token expiration date
     *
     * @param tokenID the token ID
     * @return the token expiration date
     */
    public Instant expiration(final UUID tokenID) {
        KarmaMain tokenFile = new KarmaMain(this.source, tokenID.toString().replace("-", ""), "cache", "tokens");
        if (tokenFile.exists() && tokenFile.isSet("expiration")) {
            KarmaElement instant = tokenFile.get("expiration");
            if (instant.isString()) {
                if (!instant.getObjet().getString().equals("N/A"))
                    return Instant.parse(instant.getObjet().getString());
            }
        }

        return null;
    }
}
