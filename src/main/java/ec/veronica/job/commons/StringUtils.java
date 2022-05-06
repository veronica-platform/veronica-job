package ec.veronica.job.commons;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import ec.veronica.job.types.SriStatusType;
import lombok.experimental.UtilityClass;

import java.util.Optional;

import static ec.veronica.job.types.SriStatusType.STATUS_INTERNAL_ERROR;
import static ec.veronica.job.types.SriStatusType.STATUS_PENDING;

@UtilityClass
public class StringUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static SriStatusType getSriStatus(String responseBody) {
        if (StringUtils.isEmpty(responseBody)) {
            return STATUS_INTERNAL_ERROR;
        }
        Optional<SriStatusType> status;
        try {
            status = SriStatusType.fromValue(JsonPath.read(responseBody, "$.result.autorizaciones[0].estado"));
        } catch (PathNotFoundException ex1) {
            try {
                status = SriStatusType.fromValue(JsonPath.read(responseBody, "$.result.estado"));
            } catch (PathNotFoundException ex2) {
                status = Optional.of(STATUS_INTERNAL_ERROR);
            }
        }
        return status.orElse(STATUS_PENDING);
    }

}
