import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {
    private static FileReader reader;

    private static Properties loadProperties() {
        Properties p = new Properties();
        {
            try {
                reader = new FileReader("src/main/resources/config.properties");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        {
            try {
                p.load(reader);
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    public static String get(String key) {
        return loadProperties().getProperty(key);
    }
}
