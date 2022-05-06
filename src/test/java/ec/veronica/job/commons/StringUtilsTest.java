package ec.veronica.job.commons;

import org.junit.Test;

import static ec.veronica.job.types.SriStatusType.STATUS_APPLIED;
import static ec.veronica.job.types.SriStatusType.STATUS_NOT_APPLIED;
import static ec.veronica.job.types.SriStatusType.STATUS_REJECTED;
import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void Given_VeronicaResponse_WhenIsAuthorized_Then_Return_STATUS_APPLIED() {
        String response = "{\"success\":true,\"result\":{\"timestamp\":1651813145000,\"claveAccesoConsultada\":\"0105202201170128712800110010010000000193572506915\",\"numeroComprobantes\":\"1\",\"autorizaciones\":[{\"estado\":\"AUTORIZADO\",\"numeroAutorizacion\":\"0105202201170128712800110010010000000193572506915\",\"fechaAutorizacion\":\"05/05/2022 23:59:05\",\"ambiente\":\"PRUEBAS\",\"mensajes\":[]}]}}";
        assertEquals(STATUS_APPLIED, StringUtils.getSriStatus(response));
    }

    @Test
    public void Given_VeronicaResponse_WhenIsNotAuthorized_Then_Return_STATUS_NOT_APPLIED() {
        String response = "{\"success\":false,\"result\":{\"timestamp\":1651813492000,\"claveAccesoConsultada\":\"0105202201170128712800110010010000000222112731813\",\"numeroComprobantes\":\"1\",\"autorizaciones\":[{\"estado\":\"NO AUTORIZADO\",\"numeroAutorizacion\":null,\"fechaAutorizacion\":\"06/05/2022 00:04:52\",\"ambiente\":\"PRUEBAS\",\"mensajes\":[{\"identificador\":\"52\",\"mensaje\":\"ERROR EN DIFERENCIAS\",\"informacionAdicional\":\"\\n\\n--- Inventario de errores ---\\n\\n- Factura:\\n\\tEl importe total esperado 50.0 no coincide con el calculado 111160.0: total sin impuestos 49.83 - total descuento adicional 0.0 - total devolucion iva 0.0 - total compensaciones 0.0 + total impuestos 111110.17 + propina 0.0 + total retenciones 0.0 + totalExportaciones 0.0 + total rubros terceros 0.0\\n\\tEl valor del total de descuento 1115.38 debe ser igual el calculado (total descuento + total descuento adicional) 5.38 + 0.0\\n- ImpuestoTotalizado:\\n\\tEl valor total FacturaTotalConImpuesto [codigo=2, codigoPorcentaje=2] esperado 111110.17 no coincide con el calculado 0.1692\",\"tipo\":\"ERROR\"}]}]}}";
        assertEquals(STATUS_NOT_APPLIED, StringUtils.getSriStatus(response));
    }

    @Test
    public void Given_VeronicaResponse_WhenIsRejected_Then_Return_STATUS_REJECTED() {
        String response = "{\"success\":false,\"result\":{\"estado\":\"DEVUELTA\",\"comprobantes\":[{\"claveAcceso\":\"N/A\",\"mensajes\":[{\"identificador\":\"35\",\"mensaje\":\"ARCHIVO NO CUMPLE ESTRUCTURA XML\",\"informacionAdicional\":\"Se encontró el siguiente error en la estructura del comprobante: 3 no corresponde a ningun tipo de ambiente.\",\"tipo\":\"ERROR\"}]}]}}";
        assertEquals(STATUS_REJECTED, StringUtils.getSriStatus(response));
    }

}