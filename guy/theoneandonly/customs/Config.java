package guy.theoneandonly.customs;

import java.io.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;


public class Config {

    @SuppressWarnings("unchecked")
    public Config(Customs instance) throws IOException, InvalidConfigurationException {
        File file = new File(instance.getDataFolder(), "config.yml");
        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }
        if (!file.exists()) {
            copy(instance.getResource("config.yml"), file);
        }

        instance.getConfig().load(file);

        YamlConfiguration YCon = new YamlConfiguration();
        YCon.load(file);
        instance.getConfig().addDefaults(YCon);
        instance.getConfig().options().copyDefaults(false);
        instance.getConfig().save(file);
    }

    private void copy(InputStream src, File dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);

// Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = src.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        src.close();
        out.close();
    }
}
