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
package com.semagia.cassa.server.store.sesame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.Rio;

import com.semagia.cassa.common.MediaType;
import com.semagia.cassa.server.store.UnsupportedMediaTypeException;

/**
 * Internal utility functions.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class SesameUtils {

    private static List<MediaType> _READABLE_MEDIATYPES;
    private static List<MediaType> _WRITABLE_MEDIATYPES;

    static {
        _READABLE_MEDIATYPES = new ArrayList<MediaType>();
        for (RDFFormat format: RDFParserRegistry.getInstance().getKeys()) {
            _READABLE_MEDIATYPES.add(asMediaType(format));
        }
        _READABLE_MEDIATYPES = Collections.unmodifiableList(_READABLE_MEDIATYPES);

        _WRITABLE_MEDIATYPES = new ArrayList<MediaType>();
        for (RDFFormat format: RDFWriterRegistry.getInstance().getKeys()) {
            _WRITABLE_MEDIATYPES.add(asMediaType(format));
        }
        _WRITABLE_MEDIATYPES = Collections.unmodifiableList(_WRITABLE_MEDIATYPES);
    }

    private SesameUtils() {
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
     * @return An unordered list of writable media types.
     */
    public static List<MediaType> getWritableMediaTypes() {
        return _WRITABLE_MEDIATYPES;
    }

    /**
     * Converts the RDFFormat instance into a media type.
     *
     * @param format The RDFFormat to convert.
     * @return The RDFFormat equivalent.
     */
    private static MediaType asMediaType(final RDFFormat format) {
        return MediaType.valueOf(format.getDefaultMIMEType());
    }

    /**
     * Returns a readable RDFFormat.
     *
     * @param mediaType The requested media type.
     * @return The supported RDFFormat.
     * @throws UnsupportedMediaTypeException In case no reader for the media type can be found.
     */
    public static RDFFormat asReadableRDFFormat(final MediaType mediaType) throws UnsupportedMediaTypeException {
        final RDFFormat format = Rio.getParserFormatForMIMEType(mediaType.toString());
        if (format == null) {
            throw new UnsupportedMediaTypeException("The media type " + mediaType + " cannot be read", _READABLE_MEDIATYPES);
        }
        return format; 
    }

    /**
     * Returns a writable RDFFormat.
     *
     * @param mediaType The requested media type.
     * @return The supported RDFFormat.
     * @throws UnsupportedMediaTypeException In case no writer for the media type can be found.
     */
    public static RDFFormat asWritableRDFFormat(final MediaType mediaType) throws UnsupportedMediaTypeException {
        final RDFFormat format = Rio.getWriterFormatForMIMEType(mediaType.toString());
        if (format == null) {
            throw new UnsupportedMediaTypeException("The media type " + mediaType + " cannot be written", _WRITABLE_MEDIATYPES);
        }
        return format;
    }

}
