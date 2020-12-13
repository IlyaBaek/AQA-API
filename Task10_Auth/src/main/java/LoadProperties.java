import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LoadProperties {
    private Properties p = new Properties();
    private FileReader reader;

    public Properties loadProperties() {
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
}
