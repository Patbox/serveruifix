package eu.pb4.serveruifix;


import eu.pb4.serveruifix.polydex.PolydexCompat;
import eu.pb4.serveruifix.util.FixConfig;
import eu.pb4.serveruifix.util.GuiTextures;
import eu.pb4.serveruifix.util.UiResourceCreator;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModInit implements ModInitializer {
	public static final String ID = "serveruifix";
	public static final String VERSION = FabricLoader.getInstance().getModContainer(ID).get().getMetadata().getVersion().getFriendlyString();
	public static final Logger LOGGER = LoggerFactory.getLogger("Server Ui Fix");
    public static final boolean DEV_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();
    public static final boolean DEV_MODE = VERSION.contains("-dev.") || DEV_ENV;
    @SuppressWarnings("PointlessBooleanExpression")
	public static final boolean DYNAMIC_ASSETS = true && DEV_ENV;

	public static FixConfig config = FixConfig.loadOrCreateConfig();

    public static Identifier id(String path) {
		return Identifier.of(ID, path);
	}

	@Override
	public void onInitialize() {
		if (VERSION.contains("-dev.")) {
			LOGGER.warn("=====================================================");
			LOGGER.warn("You are using development version of ServerUiFix!");
			LOGGER.warn("Support is limited, as features might be unfinished!");
			LOGGER.warn("You are on your own!");
			LOGGER.warn("=====================================================");
		}

		UiResourceCreator.setup();
		GuiTextures.register();
		PolydexCompat.register();
		PolymerResourcePackUtils.addModAssets(ID);
		PolymerResourcePackUtils.markAsRequired();

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((a, b, c) -> {
			config = FixConfig.loadOrCreateConfig();
		});
	}

}
