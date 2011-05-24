/*
 * Copyright 2007 - 2011 Lars Heuer (heuer[at]semagia.com)
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a serialization syntax.
 * 
 * A syntax has a default file extension and a default MIME type.
 * 
 * Currently it is not possible to create instances of this class, this may
 * change in the future.
 * 
 * @author Lars Heuer (heuer[at]semagia.com) <a href="http://www.semagia.com/">Semagia</a>
 */
public final class Syntax {

    /**
     * AsTMa= syntax.
     */
    public static final Syntax ASTMA = new Syntax("AsTMa", 
            Arrays.asList("application/x-tm+astma", 
                    "text/plain"), 
            Arrays.asList("atm", "astma"));

    /**
     * Binary Topic Maps (BTM).
     */
    public static final Syntax BTM = new Syntax("BTM", 
            Arrays.asList("application/x-tm+btm"), 
            "btm");

    /**
     * Compact Topic Maps syntax (CTM).
     */
    public static final Syntax CTM = new Syntax("CTM", 
            Arrays.asList("application/x-tm+ctm", 
                    "text/plain"), 
            Arrays.asList("ctm", "tmcl"));

    /**
     * Comma Seperated Values format (CSV).
     */
    public static final Syntax CSV = new Syntax("CSV",
            "text/csv",
            "csv");

    /**
     * Compact RDF to Topic Maps mapping syntax (CRTM).
     */
    public static final Syntax CRTM = new Syntax("CRTM",
            Arrays.asList("application/x-tm+crtm", 
                "text/plain"), 
             "crtm");

    /**
     * Canonical XML Topic Maps (CXTM).
     */
    public static final Syntax CXTM = new Syntax("CXTM", 
            Arrays.asList("application/x-tm+cxtm", 
                    "application/xml"), 
            "cxtm");

    /**
     * JSON Topic Maps (JTM).
     */
    public static final Syntax JTM = new Syntax("JTM", 
            Arrays.asList("application/x-tm+jtm",
                    "application/json"), 
            Arrays.asList("jtm"));

    /**
     * JSON Topic Maps 1.0.
     */
    public static final Syntax JTM10 = new Syntax("JTM 1.0", 
            Arrays.asList("application/x-tm+jtm;version=1.0",
                    "application/x-tm+jtm",
                    "application/json"), 
            Arrays.asList("jtm", "jtm1"));

    /**
     * JSON Topic Maps 1.1.
     */
    public static final Syntax JTM_11 = new Syntax("JTM 1.1", 
            Arrays.asList("application/x-tm+jtm;version=1.1",
                    "application/x-tm+jtm",
                    "application/json"), 
            Arrays.asList("jtm", "jtm11"));

    /**
     * Linear Topic Maps notation (LTM).
     */
    public static final Syntax LTM = new Syntax("LTM", 
            Arrays.asList("application/x-tm+ltm", 
                    "text/plain"),
            "ltm");

    /**
     * XML Topic Maps (XTM).
     */
    public static final Syntax XTM = new Syntax("XTM", 
            Arrays.asList("application/x-tm+xtm", 
                    "application/xml"), 
            "xtm");

    /**
     * XML Topic Maps 1.0 (XTM 1.0).
     */
    public static final Syntax XTM_10 = new Syntax("XTM 1.0", 
            Arrays.asList("application/x-tm+xtm;version=1.0", 
                    "application/xml"), 
                    Arrays.asList("xtm", "xtm1"));

    /**
     * XML Topic Maps 2.0 (XTM 2.0).
     */
    public static final Syntax XTM_20 = new Syntax("XTM 2.0", 
            Arrays.asList("application/x-tm+xtm;version=2.0", 
                    "application/xml"), 
                    Arrays.asList("xtm", "xtm2"));

    /**
     * XML Topic Maps 2.1 (XTM 2.1).
     */
    public static final Syntax XTM_21 = new Syntax("XTM 2.1", 
            Arrays.asList("application/x-tm+xtm;version=2.1", 
                    "application/xml"), 
                    Arrays.asList("xtm", "xtm2", "xtm21"));

    /**
     * TM/XML Topic Maps.
     */
    public static final Syntax TMXML = new Syntax("TM/XML", 
            Arrays.asList("application/x-tm+tmxml", 
                    "application/xml"), 
            Arrays.asList("tmx", "tmxml", "xml"));

    /**
     * XFML (eXchangeable Faceted Metadata Language).
     */
    public static final Syntax XFML = new Syntax("XFML", 
            Arrays.asList("application/xfml+xml",
                    "application/xml"), 
            "xfml");

    /**
     * Snello Topic Maps syntax (STM).
     */
    public static final Syntax SNELLO = new Syntax("Snello", 
            Arrays.asList("application/x-tm+snello", 
                    "text/plain"), 
            Arrays.asList("stm", "snello"));

    /**
     * RDF/XML.
     */
    public static final Syntax RDFXML = new Syntax("RDF/XML", 
            Arrays.asList("application/rdf+xml", 
                    "application/xml"),
            Arrays.asList("rdf", "xml", "rdfs", "owl"));

    /**
     * RDF N3 syntax.
     */
    public static final Syntax N3 = new Syntax("N3",
            Arrays.asList("application/rdf+n3",
                    "text/rdf+n3"), 
            "n3");

    /**
     * RDF N-Triples syntax.
     */
    public static final Syntax NTRIPLES = new Syntax("N-Triples",
            "text/plain",
            Arrays.asList("nt", "ntriple", "ntriples"));

    /**
     * RDF Turtle syntax.
     */
    public static final Syntax TURTLE = new Syntax("Turtle",
            Arrays.asList("text/turtle", 
                    "application/x-turtle"),
            "ttl");

    /**
     * RDF TriX syntax.
     */
    public static final Syntax TRIX = new Syntax("TriX",
            "application/trix",
            Arrays.asList("trix", "xml"));

    /**
     * RDF TriG syntax.
     */
    public static final Syntax TRIG = new Syntax("TriG",
            "application/x-trig",
            "trig");

    /**
     * RDFa HTML (EXPERIMENTAL!)
     */
    public static final Syntax RDFA = new Syntax("RDFa",
            Arrays.asList("text/html", "application/xhtml+xml"),
            Arrays.asList("html", "htm", "xhtml", "xhtm"));

    private static Syntax[] _SYNTAXES = new Syntax[] {
        ASTMA, BTM, CTM, JTM, LTM, XTM, XTM_10, XTM_20, XTM_21, TMXML, XFML, 
        SNELLO, RDFXML, N3, NTRIPLES, TURTLE, TRIX, TRIG, RDFA, CRTM
    };

    private final String _name;
    private final String[] _mimeTypes;
    private final String[] _fileExtensions;
    private final String[] _psis;

    private Syntax(final String name, final String mimeType, final String fileExtension) {
        this(name, Arrays.asList(mimeType), Arrays.asList(fileExtension));
    }

    private Syntax(final String name, final String mimeType, final List<String> fileExtensions) {
        this(name, Arrays.asList(mimeType), fileExtensions);
    }

    private Syntax(final String name, final List<String> mimeTypes, final String fileExtension) {
        this(name, mimeTypes, Arrays.asList(fileExtension));
    }

    private Syntax(final String name, final List<String> mimeTypes, 
            final List<String> fileExtensions) {
        this(name, mimeTypes, fileExtensions, Collections.<String>emptyList());
    }

    private Syntax(final String name, final List<String> mimeTypes, 
                    final List<String> fileExtensions, final List<String> psis) {
        _name = name;
        _mimeTypes = mimeTypes.toArray(new String[mimeTypes.size()]);
        _fileExtensions = fileExtensions.toArray(new String[fileExtensions.size()]);
        _psis = psis.toArray(new String[psis.size()]);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Syntax) {
            return _name.equalsIgnoreCase(((Syntax) other)._name);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return _name.toLowerCase().hashCode();
    }

    /**
     * Returns the name of the syntax.
     *
     * @return The name of the syntax.
     */
    public String getName() {
        return _name;
    }

    /**
     * Returns the default MIME type of this syntax.
     *
     * @return The default MIME type.
     */
    public String getDefaultMIMEType() {
        return _mimeTypes[0];
    }

    /**
     * Returns the MIME types of this syntax.
     *
     * @return The MIME types of this syntax.
     */
    public String[] getMIMETypes() {
        return _mimeTypes;
    }

    /**
     * Returns the default file extension.
     *
     * @return The default file extension.
     */
    public String getDefaultFileExtension() {
        return _fileExtensions[0];
    }

    /**
     * Returns all file extensions handled by this syntax.
     *
     * @return Returns the file extensions handled by this syntax.
     */
    public String[] getFileExtensions() {
        return _fileExtensions;
    }

    /**
     * 
     *
     * @return
     */
    public String getDefaultPSI() {
        return _psis[0];
    }

    /**
     * 
     *
     * @return
     */
    public String[] getPSIs() {
        return _psis;
    }

    /**
     * Returns a syntax for the specified MIME type.
     *
     * @param mimeType
     * @return A syntax for the specified <tt>mimeType</tt> or <tt>null</tt>.
     */
    public static final Syntax forMIMEType(final String mimeType) {
        return forMIMEType(mimeType, null);
    }

    /**
     * Returns a syntax for the specified MIME type.
     *
     * @param mimeType
     * @param defaultSyntax The syntax to return if no appropiate syntax was 
     *                      found.
     * @return A syntax for the specified <tt>mimeType</tt> or the 
     *          <tt>defaultSyntax</tt> if no syntax matches the specified
     *          <tt>mimeType</tt>.
     */
    public static final Syntax forMIMEType(final String mimeType, final Syntax defaultSyntax) {
        for (Syntax syntax : _SYNTAXES) {
            for (String mt: syntax.getMIMETypes()) {
                if (mt.equalsIgnoreCase(mimeType)) {
                    return syntax;
                }
            }
        }
        return defaultSyntax;
    }

    /**
     * Returns a syntax by its file extension.
     * <p>
     * The file extension is matched case-insensitive.
     * </p>
     *
     * @param extension The file extension.
     * @return A syntax for the specified <tt>extension</tt> or 
     *          <tt>null</tt> if no syntax for the specified 
     *          <tt>extension</tt> exists.
     */
    public static final Syntax forFileExtension(final String extension) {
        return forFileExtension(extension, null);
    }

    /**
     * Returns a syntax by its file extension.
     * <p>
     * The file extension is matched case-insensitive.
     * </p>
     *
     * @param extension The file extension without the leading dot ".".
     * @param defaultSyntax The syntax to return if no appropiate syntax was 
     *                      found.
     * @return A syntax for the specified <tt>extension</tt> or the 
     *          <tt>defaultSyntax</tt> if no syntax matches the specified
     *          <tt>extension</tt>.
     */
    public static final Syntax forFileExtension(final String extension, final Syntax defaultSyntax) {
        for (Syntax syntax : _SYNTAXES) {
            for (String ext: syntax.getFileExtensions()) {
                if (ext.equalsIgnoreCase(extension)) {
                    return syntax;
                }
            }
        }
        return defaultSyntax;
    }

    /**
     * Returns a syntax for the specified PSI.
     *
     * @param psi
     * @return A syntax for the specified <tt>psi</tt> or <tt>null</tt>.
     */
    public static final Syntax forPSI(final String psi) {
        return forPSI(psi, null);
    }

    /**
     * Returns a syntax for the specified PSI.
     *
     * @param psi
     * @param defaultSyntax The syntax to return if no appropiate syntax was 
     *                      found.
     * @return A syntax for the specified <tt>psi</tt> or the 
     *          <tt>defaultSyntax</tt> if no syntax matches the specified
     *          <tt>psi</tt>.
     */
    public static final Syntax forPSI(final String psi, final Syntax defaultSyntax) {
        for (Syntax syntax : _SYNTAXES) {
            for (String p: syntax.getPSIs()) {
                if (p.equals(psi)) {
                    return syntax;
                }
            }
        }
        return defaultSyntax;
    }

    /**
     * Returns a syntax by its name.
     * <p>
     * The name is matched case-insensitive.
     * </p>
     *
     * @param name The name of the syntax.
     * @return A syntax implementation with the specified <tt>name</tt> or
     *          <tt>null</tt> if no syntax was found.
     */
    public static final Syntax valueOf(final String name) {
        for (Syntax syntax : _SYNTAXES) {
            if (syntax.getName().equalsIgnoreCase(name)) {
                return syntax;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return _name;
    }

}
