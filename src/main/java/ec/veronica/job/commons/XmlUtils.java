package ec.veronica.job.commons;

import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;

import javax.validation.constraints.NotEmpty;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

import static java.lang.String.format;

@Slf4j
@UtilityClass
public final class XmlUtils {

    public static String xpath(@NotEmpty String xml, @NotEmpty String xpath) {
        try {
            InputSource inputXML = new InputSource(new StringReader(xml));
            XPath xPath = XPathFactory.newInstance().newXPath();
            return xPath.evaluate(xpath, inputXML);
        } catch (XPathExpressionException e) {
            String message = format("Error al ejecutar la expresión XPATH %s", xpath);
            log.error(message, e);
            throw new VeronicaException(message);
        }
    }

}
