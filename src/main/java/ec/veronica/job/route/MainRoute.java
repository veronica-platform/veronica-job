package ec.veronica.job.route;

import ec.veronica.job.processor.FileProcessor;
import ec.veronica.job.processor.HttpExceptionProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class MainRoute extends RouteBuilder {

    @Value("${veronica.folder.root}")
    private String rootFolder;

    @Value("${veronica.folder.inbox}")
    private String inboxFolder;

    @Value("${veronica.folder.rejected}")
    private String rejectedFolder;

    @Value("${veronica.folder.pending}")
    private String pendingFolder;

    @Value("${veronica.folder.withErrors}")
    private String withErrorsFolder;

    @Value("${veronica.folder.notAuthoraized}")
    private String notAuthoraizedFolder;

    @Value("${veronica.folder.applied}")
    private String appliedFolder;

    private final FileProcessor fileProcessor;

    private final HttpExceptionProcessor httpExceptionProcessor;

    @Override
    public void configure() {
/*        onException(Exception.class)
                .handled(true)
                .process(httpExceptionProcessor);*/

        inboxFolder = format("file:%s%s?delete=true", rootFolder, inboxFolder);
        rejectedFolder = format("file:%s%s", rootFolder, rejectedFolder);
        pendingFolder = format("file:%s%s", rootFolder, pendingFolder);
        withErrorsFolder = format("file:%s%s", rootFolder, withErrorsFolder);
        from(inboxFolder)
                .process(fileProcessor)
                .choice()
                .when().simple("${header.status} == 'PENDIENTE'")
                .to(pendingFolder)
                .when().simple("${header.status} == 'CON_ERRORES'")
                .to(withErrorsFolder)
                .when().simple("${header.status} == 'DEVUELTA'")
                .to(rejectedFolder)
                .when().simple("${header.status} == 'NO AUTORIZADO'")
                .setBody(simple("${header.appliedInvoice}"))
                .toD(format(
                        "file:%s%s${header.folderName}?fileName=${header.accessKey}.xml",
                        rootFolder,
                        notAuthoraizedFolder
                ))
                .when().simple("${header.status} == 'AUTORIZADO'")
                .setBody(simple("${header.appliedInvoice}"))
                .toD(format(
                        "file:%s%s${header.folderName}?fileName=${header.accessKey}.xml",
                        rootFolder,
                        appliedFolder
                ))
                .setBody(simple("${header.ride}"))
                .toD(format(
                        "file:%s%s${header.folderName}?fileName=${header.accessKey}.pdf",
                        rootFolder,
                        appliedFolder
                ))
                .endChoice();
    }

}
