package ec.veronica.job.commons;

import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

@UtilityClass
public class XmlUtils {

    private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

    public static Document fromStringToDocument(String xmlStr) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xmlStr)));
    }

    public static Optional<String> evalXPath(Document document, String xpathExpression) throws XPathExpressionException {
        XPathExpression expr;
        Node node;
        String value;
        if (document == null) {
            return Optional.empty();
        }
        XPath xpf = X_PATH_FACTORY.newXPath();
        expr = xpf.compile(xpathExpression);
        try {
            node = (Node) expr.evaluate(document, XPathConstants.NODE);
            value = node != null ? node.getTextContent() : null;
        } catch (XPathExpressionException ex) {
            value = (String) expr.evaluate(document, XPathConstants.STRING);
        }
        return Optional.ofNullable(value);
    }

}
