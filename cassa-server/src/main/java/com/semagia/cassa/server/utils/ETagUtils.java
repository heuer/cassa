/*
 * Copyright 2011 Lars Heuer (heuer[at]semagia.com). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.semagia.cassa.server.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.common.dm.IGraphInfo;

/**
 * Utility functions to create ETags.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class ETagUtils {

    private ETagUtils() {
        // noop.
    }

    /**
     * Returns a string which could be used as ETag for the provided graph 
     * and media type combination.
     * 
     * Returns {@code null} if an ETag could not be created.
     * 
     * @param graphInfo {@link IGraphInfo} instance, never {@code null}.
     * @param mediaType {@link MediaType} instance or {@code null}.
     * @return A string which serves as ETag or {@code null}.
     */
    public static String generateETag(final IGraphInfo graphInfo, final MediaType mediaType) {
        return generateETag(graphInfo.getURI(), graphInfo.getLastModification(), mediaType);
    }

    /**
     * Returns a string which could be used as ETag for the provided graph 
     * and media type combination.
     * 
     * Returns {@code null} if an ETag could not be created.
     * 
     * @param uri A unique resource identifier.
     * @param lastModification Last modification time or {@code -1} if it's unknown.
     * @param mediaType {@link MediaType} instance or {@code null}.
     * @return A string which serves as ETag or {@code null}.
     */
    public static String generateETag(final URI uri, final long lastModification, final MediaType mediaType) {
        if (lastModification == -1 || mediaType == null) {
            return null;
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } 
        catch (NoSuchAlgorithmException ex) {
            return null;
        }
        byte[] digest = null;
        try {
            md.update((uri.toString() + "-" + lastModification + "-" + mediaType.toString()).getBytes("utf-8"));
            digest = md.digest();
        } 
        catch (UnsupportedEncodingException ex) {
            return null;
        }
        return new BigInteger(1, digest).toString(16);
    }
}
