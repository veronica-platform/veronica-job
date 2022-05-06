package ec.veronica.job.commons;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class FileUtils {

    public static String readContent(String resourcePath) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    public static File createTemporalFile(byte[] content, String suffix) throws IOException {
        String basePath = UUID.randomUUID().toString();
        File tmpFile = File.createTempFile(basePath, suffix);
        String filePath = tmpFile.getAbsolutePath();
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(content);
        }
        return tmpFile;
    }

}
