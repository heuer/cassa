/*
 * Copyright 2008 - 2011 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.jaxrs;

import java.util.List;

import javax.ws.rs.core.Variant;

import com.semagia.cassa.common.MediaType;

/**
 * Utility functions to convert media types from {@link MediaType} to JAX-RS and 
 * vice-versa.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
final class MediaTypeUtils {

    private MediaTypeUtils() {
        // noop.
    }

    /**
     * Returns the JAX-RS equivalent of the provided Atomico media type.
     *
     * @param mt The media type.
     * @return The JAX-RS media type or {@code null} if the provided media type is null.
     */
    public static javax.ws.rs.core.MediaType toJaxRSMediaType(final MediaType mt) {
        return mt == null ? null : javax.ws.rs.core.MediaType.valueOf(mt.toString());
    }

    /**
     * Returns the equivalent of the provided JAX-RS media type.
     *
     * @param mt The JAX-RS media type.
     * @return The Atomico media type or {@code null} if the provided media type is null.
     */
    public static MediaType toMediaType(final javax.ws.rs.core.MediaType mt) {
        return mt == null ? null : MediaType.valueOf(mt.toString());
    }

    /**
     * Returns a list of variants which represent the provided {@code mediaTypes}.
     *
     * @param mediaTypes The media types to convert.
     * @return A list of variants representing the provided media types.
     */
    public static List<Variant> asVariants(final List<MediaType> mediaTypes) {
        Variant.VariantListBuilder builder = Variant.VariantListBuilder.newInstance();
        for (MediaType mt: mediaTypes) {
            builder.mediaTypes(toJaxRSMediaType(mt));
            builder.add();
        }
        return builder.build();
    }

}
