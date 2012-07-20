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
package com.semagia.cassa.server.store.tmapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.tmapi.core.TopicMap;
import org.tmapix.io.CTMTopicMapWriter;
import org.tmapix.io.MapHandlerFactory;
import org.tmapix.io.TopicMapWriter;
import org.tmapix.io.XTM2TopicMapWriter;
import org.tmapix.io.XTMVersion;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;
import com.semagia.mio.DeserializerRegistry;
import com.semagia.mio.IDeserializer;
import com.semagia.mio.MIOException;
import com.semagia.mio.Property;
import com.semagia.mio.Source;
import com.semagia.mio.Syntax;

/**
 * Internal utility functions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class TMAPIUtils {

    private static final List<MediaType> _READABLE_MEDIATYPES = Arrays.asList(MediaType.XTM, MediaType.CTM);

    private static final List<MediaType> _WRITABLE_MEDIATYPES = _READABLE_MEDIATYPES;

    private TMAPIUtils() {
        // noop.
    }

    /**
     * Returns all supported readable media types.
     *
     * @return An unordered list of readable media types.
     */
    public static List<MediaType> getReadableMediaTypes() {
        return _READABLE_MEDIATYPES;
    }

    /**
     * Returns all supported writable media types.
     *
     * @return A list of writable media types.
     */
    public static List<MediaType> getWritableMediaTypes() {
        return _WRITABLE_MEDIATYPES;
    }

    public static void ensureReadableMediaType(MediaType mt) throws UnsupportedMediaTypeException {
        if (!getReadableMediaTypes().contains(mt)) {
            throw new UnsupportedMediaTypeException(mt, getReadableMediaTypes());
        }
    }

    public static void ensureWritableMediaType(MediaType mt) throws UnsupportedMediaTypeException {
        if (!getWritableMediaTypes().contains(mt)) {
            throw new UnsupportedMediaTypeException(mt, getWritableMediaTypes());
        }
    }

    public static void write(final TopicMap tm, final MediaType mediaType,
            final OutputStream out) throws IOException {
        final TopicMapWriter writer = mediaType.equals(MediaType.XTM) ? new XTM2TopicMapWriter(
                out, tm.getLocator().toExternalForm(), XTMVersion.XTM_2_1)
                : new CTMTopicMapWriter(out, tm.getLocator().toExternalForm());
        writer.write(tm);
    }

    private static String toMIMEType(MediaType mediaType) {
        return mediaType.toString();
    }

    public static void read(final TopicMap tm, final URI baseURI, 
            final InputStream in, final MediaType mediaType) throws IOException {
        final IDeserializer deser = DeserializerRegistry.getInstance().createDeserializer(Syntax.forMIMEType(toMIMEType(mediaType)));
        // Enable more lenient topic map parsing
        deser.setProperty(Property.VALIDATE, Boolean.FALSE);
        deser.setProperty(Property.LTM_LEGACY, Boolean.FALSE);
        deser.setMapHandler(MapHandlerFactory.createMapHandler(tm));
        try {
            deser.parse(new Source(in, baseURI.toString()));
        } 
        catch (MIOException ex) {
            throw new IOException(ex);
        }
    }

}
