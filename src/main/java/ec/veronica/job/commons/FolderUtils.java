package ec.veronica.job.commons;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class FolderUtils {

    private static final String FILE_SEPARATOR = File.separator;
    private static final String FILE_PROTOCOL_NAME = "file:";

    public static String buildFolderPath(List<String> directories, Optional<String> arguments) {
        StringBuilder sb = new StringBuilder(FILE_PROTOCOL_NAME);
        for (String directory : directories) {
            sb.append(directory);
            if (!sb.toString().endsWith(FILE_SEPARATOR)) {
                sb.append(FILE_SEPARATOR);
            }
        }
        if (arguments.isPresent()) {
            sb.append("?").append(arguments.get());
        }
        return sb.toString();
    }

    public static boolean isDirectory(String folderPath) {
        File rootFolder = new File(folderPath);
        return rootFolder.exists() && rootFolder.isDirectory();
    }

}
