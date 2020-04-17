package org.grandeur.utils.helpers;

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.regex.Pattern;
/**
 *     Grandeur - a tool for logging, create config file based on ini and
 *     utils
 *     Copyright (C) 2020 Harfiyan Shia.
 *
 *     This file is part of grandeur-core.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/.
 */
public final class XMLHelper {
    public static final String keep_attributes_and_content_regex = "<[^>]([a-zA-Z_0-9]+|[a-zA-Z0-9 =-]+\\\"(\\d+)\\\")(|/)>";

    public static String ToString(Document xml, boolean beautify) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

//        HashMap<String, String> outputProperties = new HashMap<String, String>() {{
//            put(OutputKeys.OMIT_XML_DECLARATION, "yes");
//        }};

        if (xml == null)
            return null;

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        if (beautify) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        }

        StringWriter out = new StringWriter();
        transformer.transform(new DOMSource(xml), new StreamResult(out));

        return out.toString();
    }

    public static String InLine(String xmlContent) {
        StringBuilder sb = new StringBuilder();

        String[] xmlLines = xmlContent.split(Pattern.quote("\r\n"));
        for (String xmlLine : xmlLines) {
            sb.append(xmlLine.trim());
        }

        return sb.toString();
    }
}
