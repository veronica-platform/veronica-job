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
        log.debug("Registering endpoint {}", endpointFolder);
        from(endpointFolder)
                .routeId(routeId)
                .process(fileProcessor);
    }

}
