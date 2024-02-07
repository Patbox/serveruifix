package eu.pb4.serveruifix.util;

import com.google.gson.annotations.SerializedName;
import eu.pb4.serveruifix.ModInit;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FixConfig {
    @SerializedName("replace_enchanting_table")
    public boolean enchanting = false;
    @SerializedName("replace_stonecutter")
    public boolean stonecutter = false;

    public static FixConfig loadOrCreateConfig() {
        try {
            FixConfig config;
            File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "serveruifix.json");

            if (configFile.exists()) {
                String json = IOUtils.toString(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));

                config = BaseGson.GSON.fromJson(json, FixConfig.class);
            } else {
                config = new FixConfig();
            }

            saveConfig(config);
            return config;
        } catch (IOException exception) {
            ModInit.LOGGER.error("Something went wrong while reading config!", exception);
            return new FixConfig();
        }
    }

    public static void saveConfig(FixConfig config) {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "serveruifix.json");
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
            writer.write(BaseGson.GSON.toJson(config));
            writer.close();
        } catch (Exception e) {
            ModInit.LOGGER.error("Something went wrong while saving config!");
            e.printStackTrace();
        }
    }
}
