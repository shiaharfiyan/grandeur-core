package io.github.shiaharfiyan.utils.helpers;

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.regex.Pattern;

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
