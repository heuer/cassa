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

import java.io.IOException;

/**
 * A feed handler receives events about the content of a SDShare feed.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
interface IFeedHandler {

    /**
     * Notification about a feed.
     * 
     * @param id
     *            The feed identifier.
     * @param title
     *            The feed title.
     * @param updated
     *            The time when the feed was updated.
     * @throws IOException
     *            In case of an error.
     */
    public void startFeed(String id, String title, long updated)
            throws IOException;

    /**
     * The last notification, sent after a
     * {@link #startFeed(String, String, long)} notification.
     * 
     * @throws IOException
     *            In case of an error.
     */
    public void endFeed() throws IOException;

    /**
     * Notification sent after a {@link #startFeed(String, String, long)} event.
     * 
     * @param id
     *            The entry identifier.
     * @param title
     *            The entry title.
     * @param updated
     *            The time when the entry was updated.
     * @throws IOException
     *            In case of an error.
     */
    public void startEntry(String id, String title, long updated)
            throws IOException;

    /**
     * Notification which appears after a
     * {@link #startEntry(String, String, long)} notification.
     * 
     * @throws IOException
     *            In case of an error.
     */
    public void endEntry() throws IOException;

    /**
     * Notification that appears after a
     * {@link #startFeed(String, String, long)} or
     * {@link #startEntry(String, String, long)} event.
     * 
     * @param href
     *            The link, an absolute IRI.
     * @param relation
     *            The link kind, the relation or {@code null}.
     * @throws IOException
     *            In case of an error.
     */
    public void link(String href, String relation) throws IOException;

    /**
     * Notification that appears after a
     * {@link #startFeed(String, String, long)} or
     * {@link #startEntry(String, String, long)} event.
     * 
     * @param href
     *            The link, an absolute IRI.
     * @param relation
     *            The link kind, the relation or {@code null}.
     * @param mediaType
     *            The associated media type of the link or {@code null}.
     * @throws IOException
     *            In case of an error.
     */
    public void link(String href, String relation, String mediaType)
            throws IOException;

    /**
     * Notification that may appear after a
     * {@link #startFeed(String, String, long)} event.
     * 
     * @param iri
     *            IRI of the resource.
     * @param role
     *            Role of the resource or {@code null}.
     * @throws IOException
     *            In case of an error.
     */
    public void resource(String iri, String role) throws IOException;

    /**
     * Notificiation which appears after a
     * {@link #startFeed(String, String, long)} notification.
     * 
     * @param name
     *            Name of the author
     * @param email
     *            E-mail of the author or {@code null}.
     * @param iri
     *            IRI of the author or {@code null}.
     * @throws IOException
     *             In case of an error.
     */
    public void author(String name, String email, String iri) throws IOException;

    /**
     * Notification which may appear after a
     * {@link #startFeed(String, String, long)} or after a
     * {@link #startEntry(String, String, long)} notification.
     * 
     * @param summary A string, never {@code null}.
     * @throws IOException
     *            In case of an error.
     */
    public void summary(String summary) throws IOException;

    /**
     * Notification which appears after a
     * {@link #startFeed(String, String, long)} notification.
     * 
     * @param name
     *            The name of the generator.
     * @param iri
     *            The IRI of the generator or {@code null}.
     * @throws IOException
     *            In case of an error.
     */
    public void generator(String name, String iri) throws IOException;

}
