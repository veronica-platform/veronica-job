package ec.veronica.job.router;

import ec.veronica.job.processor.FileProcessor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;

import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_APPLIED;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_INTERNAL_ERROR;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_NOT_APPLIED;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_PENDING;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_REJECTED;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_AUTHORIZED;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_INBOX;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_PENDING;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_PROCESSING_ERROR;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_REJECTED;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_UNAUTHORIZED;
import static ec.veronica.job.commons.FolderUtils.buildDestinationFolder;
import static java.lang.String.format;

@Slf4j
@Builder
public class VeronicaRoute extends RouteBuilder {

    private String routeId;
    private String rootFolder;
    private FileProcessor fileProcessor;

    @Override
    public void configure() {
        log.debug("Registering route for folder: [{}]", rootFolder);
        String endpointFolder = buildDestinationFolder("file:%s%s?delete=true&charset=utf-8", rootFolder, FOLDER_INBOX);
        from(endpointFolder)
                .routeId(routeId)
                .process(fileProcessor)
                .choice()
                .when().simple(is(STATUS_PENDING.getValue())).to(buildDestinationFolder(rootFolder, FOLDER_PENDING))
                .when().simple(is(STATUS_INTERNAL_ERROR.getValue())).to(buildDestinationFolder(rootFolder, FOLDER_PROCESSING_ERROR))
                .when().simple(is(STATUS_REJECTED.getValue())).to(buildDestinationFolder(rootFolder, FOLDER_REJECTED))
                .when().simple(is(STATUS_NOT_APPLIED.getValue()))
                    .setBody(simple("${header.appliedInvoice}"))
                        .toD(format("%s${header.folderName}?fileName=${header.fileName}.xml", buildDestinationFolder(rootFolder, FOLDER_UNAUTHORIZED)))
                .when().simple(is(STATUS_APPLIED.getValue()))
                    .setBody(simple("${header.appliedInvoice}"))
                        .toD(format("%s${header.folderName}?fileName=${header.fileName}.xml", buildDestinationFolder(rootFolder, FOLDER_AUTHORIZED)))
                    .setBody(simple("${header.ride}"))
                        .toD(format("%s${header.folderName}?fileName=${header.fileName}.pdf", buildDestinationFolder(rootFolder, FOLDER_AUTHORIZED)))
                .endChoice();
    }

    private String is(String status) {
        return format("${header.status} == '%s'", status);
    }

}
