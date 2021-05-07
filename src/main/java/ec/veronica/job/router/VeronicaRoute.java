package ec.veronica.job.router;

import ec.veronica.job.processor.FileProcessor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;

import static ec.veronica.job.commons.Status.STATUS_APPLIED;
import static ec.veronica.job.commons.Status.STATUS_INTERNAL_ERROR;
import static ec.veronica.job.commons.Status.STATUS_NOT_APPLIED;
import static ec.veronica.job.commons.Status.STATUS_PENDING;
import static ec.veronica.job.commons.Status.STATUS_REJECTED;
import static java.lang.String.format;

@Slf4j
@Builder
public class VeronicaRoute extends RouteBuilder {

    private String routeId;
    private String rootFolder;
    private FileProcessor fileProcessor;

    @Override
    public void configure() {
        String endpointFolder = format("file:%s%s?delete=true&charset=utf-8", rootFolder, "Inbox\\");
        String pendingFolder = format("file:%s%s", rootFolder, "PendienteSubir\\");
        String withErrorsFolder = format("file:%s%s", rootFolder, "ErrorProcesando\\");
        String rejectedFolder = format("file:%s%s", rootFolder, "Devueltos\\");
        String notAuthorizedFolder = format("file:%s%s", rootFolder, "NoAutorizados\\");
        String appliedFolder = format("file:%s%s", rootFolder, "Autorizados\\");
        log.debug("Registering endpoint {}", endpointFolder);
        from(endpointFolder)
                .routeId(routeId)
                .process(fileProcessor)
                .choice()
                    .when().simple(is(STATUS_PENDING.getValue())).to(pendingFolder)
                    .when().simple(is(STATUS_INTERNAL_ERROR.getValue())).to(withErrorsFolder)
                    .when().simple(is(STATUS_REJECTED.getValue())).to(rejectedFolder)
                    .when().simple(is(STATUS_NOT_APPLIED.getValue()))
                        .setBody(simple("${header.appliedInvoice}"))
                            .toD(format("%s${header.folderName}?fileName=${header.fileName}.xml", notAuthorizedFolder))
                    .when().simple(is(STATUS_APPLIED.getValue()))
                        .setBody(simple("${header.appliedInvoice}"))
                            .toD(format("%s${header.folderName}?fileName=${header.fileName}.xml", appliedFolder))
                        .setBody(simple("${header.ride}"))
                            .toD(format("%s${header.folderName}?fileName=${header.fileName}.pdf", appliedFolder))
                .endChoice();
    }

    private String is(String status) {
        return format("${header.status} == '%s'", status);
    }

}
