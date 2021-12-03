package ec.veronica.job.router;

import ec.veronica.job.processor.FileProcessor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;

import java.util.Optional;

import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_INTERNAL_ERROR;
import static com.rolandopalermo.facturacion.ec.common.types.SriStatusType.STATUS_PENDING;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_INBOX;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_PENDING;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_PROCESSING_ERROR;
import static ec.veronica.job.commons.DestinationFolder.FOLDER_REJECTED;
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
        String endpointFolder = buildFolderPath(asList(rootFolder, FOLDER_INBOX.getValue()), Optional.empty());
        Optional<String> fileName = Optional.of("fileName=${header.fileName}.xml");
        from(endpointFolder)
                .routeId(routeId)
                .process(fileProcessor)
                .choice()
                    .when().simple(is(STATUS_PENDING.getValue()))
                        .to(buildFolderPath(asList(rootFolder, FOLDER_PENDING.getValue()), fileName))
                    .when().simple(is(STATUS_INTERNAL_ERROR.getValue()))
                        .to(buildFolderPath(asList(rootFolder, FOLDER_PROCESSING_ERROR.getValue()), fileName))
                    .when().simple(is(STATUS_INTERNAL_ERROR.getValue()))
                        .to(buildFolderPath(asList(rootFolder, FOLDER_REJECTED.getValue()), fileName))
        ;
    }

    private String is(String status) {
        return format("${header.status} == '%s'", status);
    }

}
