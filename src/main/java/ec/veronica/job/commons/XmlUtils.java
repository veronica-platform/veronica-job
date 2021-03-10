package ec.veronica.job.commons;

import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public final class XmlUtils {

    public static String xpath(String xml, String xpath) {
        try {
            InputSource inputXML = new InputSource(new StringReader(xml));
            XPath xPath = XPathFactory.newInstance().newXPath();
            return xPath.evaluate(xpath, inputXML);
        } catch (XPathExpressionException e) {
            //TODO
            return null;
        }
    }

}
