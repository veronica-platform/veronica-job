package ec.veronica.job.commons;

import org.junit.Test;

import static ec.veronica.job.types.SriStatusType.STATUS_APPLIED;
import static ec.veronica.job.types.SriStatusType.STATUS_NOT_APPLIED;
import static ec.veronica.job.types.SriStatusType.STATUS_REJECTED;
import static org.junit.Assert.assertEquals;

public class StringUtilsTest {

    @Test
    public void Given_VeronicaResponse_WhenIsAuthorized_Then_Return_STATUS_APPLIED() {
        String response = "{\n" +
                "    \"estado\": \"AUTORIZADO\",\n" +
                "    \"claveAcceso\": \"1303202301092150540000110010030000003541289158615\",\n" +
                "    \"fechaAutorizacion\": \"13/03/2023 23:10:22\"\n" +
                "}";
        assertEquals(STATUS_APPLIED, StringUtils.getSriStatus(response));
    }

}
