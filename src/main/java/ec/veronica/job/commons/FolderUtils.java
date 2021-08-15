package ec.veronica.job.commons;

import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.io.File;

import static java.lang.String.format;

@UtilityClass
public class FolderUtils {

    private static final String FILE_SEPARATOR = File.separator;
    private static final String FOLDER_PATTERN = "file:%s%s%s";

    public static String buildDestinationFolder(@NotNull String rootFolder, DestinationFolder destinationFolder) {
        return buildFolderPath(FOLDER_PATTERN, rootFolder, destinationFolder);
    }

    public static String buildDestinationFolder(String pattern, @NotNull String rootFolder, DestinationFolder destinationFolder) {
        return buildFolderPath(pattern, rootFolder, destinationFolder);
    }

    private static String buildFolderPath(String pattern, @NotNull String rootFolder, DestinationFolder destinationFolder) {
        StringBuilder sb = new StringBuilder(rootFolder);
        if (!rootFolder.endsWith(FILE_SEPARATOR)) {
            sb.append(FILE_SEPARATOR);
        }
        return format(pattern, sb, destinationFolder.getValue(), FILE_SEPARATOR);
    }

}
