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
package com.semagia.cassa.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an immutable media type.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class MediaType {

    private static final List<Parameter> _EMPTY_PARAMS = Collections.emptyList();

    /**
     * XTM media type (either XTM 1.0 or 2.x).
     */
    public static final MediaType XTM = MediaType.valueOf("application/x-tm+xtm");

    /**
     * CTM 1.0 media type.
     */
    public static final MediaType CTM = MediaType.valueOf("application/x-tm+ctm");

    /**
     * RDF/XML media type.
     */
    public static final MediaType RDF_XML = MediaType.valueOf("application/rdf+xml");

    /**
     * RDF Turtle media type.
     */
    public static final MediaType TURTLE = MediaType.valueOf("text/turtle");

    /**
     * SPARQL Query media type.
     */
    public static final MediaType SPARQL_QUERY = MediaType.valueOf("application/sparql-query");

    private final String _mainType;
    private final String _subType;
    private final List<Parameter> _params;

    private MediaType(String mainType, String subType, List<Parameter> params) {
        if (mainType == null) {
            throw new IllegalArgumentException("The mainType must not be null");
        }
        if (subType == null) {
            throw new IllegalArgumentException("The subType must not be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("The parameters must not be null");
        }
        _mainType = mainType;
        _subType = subType;
        _params = Collections.unmodifiableList(params);
    }

    /**
     * Returns the type part.
     *
     * @return The type of this media type.
     */
    public String getType() {
        return _mainType;
    }

    /**
     * Returns the subtype part.
     *
     * @return The subtype of this media type.
     */
    public String getSubtype() {
        return _subType;
    }

    /**
     * Returns an immutable list of parameters.
     *
     * @return A (maybe emtpy) immutable list of parameters.
     */
    public List<Parameter> getParameters() {
        return _params;
    }

    /**
     * Returns if this media type is compatible to the <tt>other</tt> media type.
     * <p>
     * The parameters (if any) of this media type and the <tt>other</tt> media
     * type are ignored.
     * </p>
     *
     * @param other The media type to compare this media type with.
     * @return <tt>true</tt> if this media type is compatible, otherwise <tt>false</tt>.
     */
    public boolean isCompatible(MediaType other) {
        if (other == null) {
            return false;
        }
        if ("*".equals(_mainType) || "*".equals(other._mainType)) {
            return true;
        }
        if (_mainType.equals(other._mainType) 
                && ("*".equals(_subType) || "*".equals(other._subType))) {
            return true;
        }
        return _mainType.equals(other._mainType) 
                    && _subType.equals(other._subType);
    }

    /**
     * Returns if this media type is compatible to the <tt>other</tt> media type.
     * <p>
     * If <tt>includeParameters</tt> is <tt>false</tt> the result is the same as
     * invoking {@link #isCompatible(MediaType)} otherwise the parameters are
     * taken into account.
     * </p>
     *
     * @param other The other media type.
     * @param includeParameters Indicates if the media type parameters should be
     *          taken into account.
     * @return If this media type is compatible to the <tt>other</tt> media type.
     */
    public boolean isCompatible(MediaType other, boolean includeParameters) {
        boolean result = isCompatible(other);
        if (!includeParameters) {
            return result;
        }
        return result && _params.containsAll(other._params);
    }

    /**
     * Returns the (normalized) media type as string representation without
     * parameters.
     *
     * @return "type/subtype"
     */
    public String toStringWithoutParameters() {
        return new StringBuilder(_mainType)
                    .append('/')
                    .append(_subType)
                    .toString();
    }

    /**
     * Returns the (normalized) media type as string representation
     * suitable for use as the value of a corresponding HTTP header
     * 
     * @return The string representation.
     */
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append(_mainType)
            .append('/')
            .append(_subType);
        for (Parameter param: _params) {
            buff.append(";")
                .append(param.getKey())
                .append('=')
                .append(param.getValue());
        }
        return buff.toString();
    }

    /**
     * Parses the specified <tt>value</tt> and creates a media type.
     * <p>
     * The <tt>value</tt> must follow the syntax for media types.
     * </p>
     *
     * @param value A string representation of a media type.
     * @return A media type instance.
     */
    public static MediaType valueOf(String value) {
        if (value == null) {
            throw new IllegalArgumentException("The value must not be null");
        }
        String mainType = null;
        String subType = null;
        String params = null;
        int idx = value.indexOf(";");
        if (idx > -1) {
           params = value.substring(idx + 1);
           value = value.substring(0, idx);
        }
        String[] pair = value.split("/");
        if (pair.length < 2 && value.equals("*")) {
           mainType = "*";
           subType = "*";
        }
        else if (pair.length != 2) {
           throw new IllegalArgumentException("Illegal media type: " + value);
        }
        else {
            mainType = pair[0].trim().toLowerCase();
            subType = pair[1].trim().toLowerCase();
        }
        if (params == null) {
            return new MediaType(mainType, subType, _EMPTY_PARAMS);
        }
        List<Parameter> parameters = new ArrayList<Parameter>();
        String[] keyValuePairs = params.split(";");
        for (String keyValuePair : keyValuePairs) {
           idx = keyValuePair.indexOf("=");
           String key = keyValuePair.substring(0, idx).trim().toLowerCase();
           String val = keyValuePair.substring(idx + 1).trim();
           parameters.add(new Parameter(key, val));
        }
        return new MediaType(mainType, subType, parameters);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MediaType)) {
            return false;
        }
        MediaType other = (MediaType) obj;
        return _mainType.equals(other._mainType)
                    && _subType.equals(other._subType)
                    && _params.equals(other._params);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _mainType.hashCode() + _subType.hashCode() + _params.hashCode();
    }


    /**
     * Representation of a media type parameter (a key/value pair).
     */
    public static class Parameter {
        
        private final String _key;
        private final String _value;
        
        Parameter(String key, String value) {
            _key = key;
            _value = value;
        }

        public String getKey() {
            return _key;
        }

        public String getValue() {
            return _value;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Parameter)) {
                return false;
            }
            return obj instanceof Parameter 
                    && _key.equals(((Parameter) obj)._key)
                    && _value.equals(((Parameter) obj)._value);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return 31 * _key.hashCode() + _value.hashCode();
        }
        
    }

}
