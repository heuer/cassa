/*
 * Copyright 2008 - 2009 Lars Heuer (heuer[at]semagia.com). All rights reserved.
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
package com.semagia.cassa.server.sdshare;

import java.util.regex.Pattern;

import com.semagia.cassa.common.DateTimeUtils;

/**
 * Generator for globally unique identifiers (IRIs).
 * <p>
 * The generator uses the "tag" scheme 
 * (<a href="http://tools.ietf.org/html/rfc4151">RFC 4151</a>) and must be 
 * initialized with a domain name. 
 * The generator is thread-safe and may be utilized by {@link IStorage}
 * implementations.
 * </p>
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class TagURIGenerator {

    private static final Pattern _DOMAIN_VALIDATOR = Pattern
            .compile("(\\p{Alpha}+\\.)?[^\\s\\.]+\\.\\p{Alpha}+");

    private final String _domain;

    /**
     * Initializes the instance with the specified <tt>domain</tt>.
     * 
     * @param domain
     *            A domain name, never <tt>null</tt>.
     */
    public TagURIGenerator(final String domain) {
        if (domain == null) {
            throw new IllegalArgumentException("The domain must not be null");
        }
        if (!_DOMAIN_VALIDATOR.matcher(domain).matches()) {
            throw new IllegalArgumentException(
                    "Expected a domain name like 'www.example.org', got: "
                            + domain);
        }
        _domain = domain;
    }

    /**
     * Returns a unique URI for the specified collection identifier taking the
     * provided <tt>time</tt> into account.
     * 
     * @param time
     *            The when the collection specified by <tt>collectionId</tt> was
     *            updated.
     * @param collectionId
     *            A collection identifier.
     * @return A unique URI for the specified time / collection id pair.
     */
    public String generateCollectionIRI(final long time, final String collectionId) {
        return _generate(time, "collection", collectionId);
    }

    /**
     * Generates a fragment IRI.
     * 
     * @param time
     *            The when the fragment specified by <tt>fragmentId</tt> was
     *            updated.
     * @param fragmentId
     *            A fragment identifier.
     * @return A unique URI for the specified time / fragment id pair.
     */
    public String generateFragmentIRI(final long time, final String fragmentId) {
        return _generate(time, "fragment", fragmentId);
    }

    /**
     * Generates a snapshot IRI.
     * 
     * @param time
     *            The when the snapshot specified by <tt>snapshotId</tt> was
     *            updated.
     * @param snapshotId
     *            A snapshot identifier.
     * @return A unique URI for the specified time / snapshot id pair.
     */
    public String generateSnapshotIRI(final long time, final String snapshotId) {
        return _generate(time, "snapshot", snapshotId);
    }

    /**
     * Generates a URI.
     * 
     * @param time
     *            The time when the entity specified by <tt>id</tt> was created.
     * @param kind
     *            A string indicating the kind of link (snapshot, collection,
     *            fragment).
     * @param id
     *            The identifier of the snapshot or collection or fragment.
     * @return A unique URI.
     */
    private String _generate(final long time, final String kind, final String id) {
        if (id == null) {
            throw new IllegalArgumentException("The identifier must not be null");
        }
        final StringBuilder buff = new StringBuilder("tag:");
        buff.append(_domain)
            .append(',')
            .append(DateTimeUtils.toISO8601Date(time).substring(0, 10))
            .append(":cassa:")
            .append(time)
            .append(':')
            .append(kind)
            .append(':')
            .append(id);
        return buff.toString();
    }

}
