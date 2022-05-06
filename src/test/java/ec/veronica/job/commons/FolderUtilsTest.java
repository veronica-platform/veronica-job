package ec.veronica.job.commons;

import ec.veronica.job.types.DestinationFolderType;
import org.junit.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;

public class FolderUtilsTest {

    @Test
    public void Given_Folder_Subdirectories_Then_BuildPathSuccessfully() {
        String rootFolder = System.getProperty("user.dir");
        String result = FolderUtils.buildFolderPath(
                asList(rootFolder, DestinationFolderType.FOLDER_AUTHORIZED.getValue(), "9999999999999"),
                Optional.empty()
        );
        System.out.println(result);
        assertNotNull(result);
    }

    @Test
    public void Given_Folder_SubdirectoriesWithArguments_Then_BuildPathSuccessfully() {
        String rootFolder = System.getProperty("user.dir");
        String result = FolderUtils.buildFolderPath(
                asList(rootFolder, DestinationFolderType.FOLDER_AUTHORIZED.getValue(), "9999999999999"),
                Optional.of("abc=def&ghi=jkl")
        );
        System.out.println(result);
        assertNotNull(result);
    }
}
