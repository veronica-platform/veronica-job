package ec.veronica.job.router;

import ec.veronica.job.processor.FileProcessor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.camel.builder.RouteBuilder;

import java.util.Optional;

import static ec.veronica.common.types.SriStatusType.STATUS_APPLIED;
import static ec.veronica.common.types.SriStatusType.STATUS_INTERNAL_ERROR;
import static ec.veronica.common.types.SriStatusType.STATUS_NOT_APPLIED;
import static ec.veronica.common.types.SriStatusType.STATUS_PENDING;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_AUTHORIZED;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_INBOX;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_PENDING;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_PROCESSING_ERROR;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_REJECTED;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_UNAUTHORIZED;
import static ec.veronica.job.commons.FolderUtils.buildFolderPath;
import static java.lang.String.format;
import static java.util.Arrays.asList;

@Slf4j
@Builder
public class VeronicaRoute extends RouteBuilder {

    private String routeId;
    private String rootFolder;
    private FileProcessor fileProcessor;

    @Override
    public void configure() {
        log.debug("Registering route for folder: [{}]", rootFolder);
        var endpointFolder = buildFolderPath(asList(rootFolder, FOLDER_INBOX.getValue()), Optional.of("delete=true&charset=utf-8"));
        var xmlFileName = Optional.of("fileName=${header.fileName}.xml");
        var pdfFileName = Optional.of("fileName=${header.fileName}.pdf");
        from(endpointFolder)
                .routeId(routeId)
                .process(fileProcessor)
                .choice()
                .when().simple(is(STATUS_PENDING.getValue()))
                .to(buildFolderPath(asList(rootFolder, FOLDER_PENDING.getValue()), xmlFileName))
                .when().simple(is(STATUS_INTERNAL_ERROR.getValue()))
                .to(buildFolderPath(asList(rootFolder, FOLDER_PROCESSING_ERROR.getValue()), xmlFileName))
                .when().simple(is(STATUS_INTERNAL_ERROR.getValue()))
                .to(buildFolderPath(asList(rootFolder, FOLDER_REJECTED.getValue()), xmlFileName))
                .when().simple(is(STATUS_NOT_APPLIED.getValue()))
                .setBody(simple("${header.appliedInvoice}")).toD(buildFolderPath(asList(rootFolder, FOLDER_UNAUTHORIZED.getValue()), xmlFileName))
                .when().simple(is(STATUS_APPLIED.getValue()))
                .setBody(simple("${header.appliedInvoice}")).toD(buildFolderPath(asList(rootFolder, FOLDER_AUTHORIZED.getValue(), "${header.folderName}"), xmlFileName))
                .setBody(simple("${header.ride}")).toD(buildFolderPath(asList(rootFolder, FOLDER_AUTHORIZED.getValue(), "${header.folderName}"), pdfFileName))
        ;
    }

    private String is(String status) {
        return format("${header.status} == '%s'", status);
    }

}
