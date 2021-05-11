package ec.veronica.job.commons;

import com.rolandopalermo.facturacion.ec.common.exception.VeronicaException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;

import javax.validation.constraints.NotEmpty;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import static java.lang.String.format;

@Slf4j
@UtilityClass
public final class XmlUtils {

    public static String xpath(@NotEmpty InputSource inputXML, @NotEmpty String xpath) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            return xPath.evaluate(xpath, inputXML);
        } catch (XPathExpressionException e) {
            String message = format("Error al ejecutar la expresión XPATH %s", xpath);
            throw new VeronicaException(message);
        }
    }

}
