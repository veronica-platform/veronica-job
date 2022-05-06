package ec.veronica.job.commons;

import lombok.SneakyThrows;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.Optional;

import static ec.veronica.job.commons.Constants.XML_XPATH_EMAILS;
import static ec.veronica.job.commons.Constants.XML_XPATH_EMISSION_POINT;
import static ec.veronica.job.commons.Constants.XML_XPATH_ESTABLISHMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XmlUtilsTest {

    @Test
    @SneakyThrows
    public void When_ReceiptXml_Expect_EmailsListNotNull() {
        String authorizedInvoice = FileUtils.readContent("utils/invoice.xml");
        Document document = XmlUtils.fromStringToDocument(authorizedInvoice);

        Optional<String> emailsList = XmlUtils.evalXPath(document, String.format(XML_XPATH_EMAILS, "Email"));
        assertTrue(emailsList.isPresent());
        assertEquals("info@veronica.ec", emailsList.get());
    }

    @Test
    @SneakyThrows
    public void When_ReceiptXml_Expect_BranchInfoNotNull() {
        String authorizedInvoice = FileUtils.readContent("utils/invoice.xml");
        Document document = XmlUtils.fromStringToDocument(authorizedInvoice);

        Optional<String> branch = XmlUtils.evalXPath(document, XML_XPATH_ESTABLISHMENT);
        Optional<String> branchOffice = XmlUtils.evalXPath(document, XML_XPATH_EMISSION_POINT);
        assertTrue(branch.isPresent());
        assertEquals("001", branch.get());
        assertTrue(branchOffice.isPresent());
        assertEquals("001", branchOffice.get());
    }

    @Test
    @SneakyThrows
    public void When_Withholding_Expect_Total() {
        String authorizedInvoice = FileUtils.readContent("utils/withholding.xml");
        Optional<String> strTotal = XmlUtils.evalXPath(XmlUtils.fromStringToDocument(authorizedInvoice), "sum(/comprobanteRetencion/impuestos/impuesto/valorRetenido)");
        assertEquals("9.14", strTotal.get());
    }

}
