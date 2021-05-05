package ec.veronica.job.router;

import ec.veronica.job.processor.FileProcessor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;

import static java.lang.String.format;

@Slf4j
@Builder
public class VeronicaRoute extends RouteBuilder {

    private String inboxFolder;
    private String routeId;
    private String rootFolder;
    private FileProcessor fileProcessor;

    @Override
    public void configure() throws Exception {
        String endpointFolder = format("file:%s%s?delete=true&charset=utf-8", rootFolder, inboxFolder);
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
                    .when().simple(is("PENDIENTE")).to(pendingFolder)
                    .when().simple(is("CON_ERRORES")).to(withErrorsFolder)
                    .when().simple(is("DEVUELTA")).to(rejectedFolder)
                    .when().simple(is("NO AUTORIZADO"))
                        .setBody(simple("${header.appliedInvoice}"))
                            .toD(format("file:%s%s${header.folderName}?fileName=${header.fileName}.xml", rootFolder, notAuthorizedFolder))
                    .when().simple(is("AUTORIZADO"))
                        .setBody(simple("${header.appliedInvoice}"))
                            .toD(format("file:%s%s${header.folderName}?fileName=${header.fileName}.xml", rootFolder, appliedFolder))
                        .setBody(simple("${header.ride}"))
                            .toD(format("file:%s%s${header.folderName}?fileName=${header.fileName}.pdf", rootFolder, appliedFolder))
                .endChoice();
    }

    private String is(String status) {
        return format("${header.status} == '%s'", status);
    }

}
