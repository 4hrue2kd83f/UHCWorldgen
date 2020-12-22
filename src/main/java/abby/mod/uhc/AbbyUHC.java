package abby.mod.uhc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.ForgeWorldTypeScreens;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("abbyuhc")
public class AbbyUHC {
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();

	@ObjectHolder("abbyuhc:uhc")
	public static ForgeWorldType UHC_WORLDTYPE;

	public AbbyUHC() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ForgeWorldType.class, this::registerWorldTypes);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerWorldTypeScreenFactories);
	}

	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("HELLO FROM COMMON SETUP EVENT");
		event.enqueueWork(UHCBiomeProvider::registerBiomeProvider);
	}

	private void registerWorldTypes(RegistryEvent.Register<ForgeWorldType> event) {
		event.getRegistry().registerAll(new ForgeWorldType(UHCWorldType::createChunkGeneratorStatic).setRegistryName("uhc"));
	}

	private void registerWorldTypeScreenFactories(FMLClientSetupEvent event) {
		ForgeWorldTypeScreens.registerFactory(UHC_WORLDTYPE,
				(returnTo, dimensionGeneratorSettings) -> new Screen(UHC_WORLDTYPE.getDisplayName()) {
					@Override
					protected void init() {
						super.init();

						addButton(new Button(0, 0, 120, 20, new StringTextComponent("close"), btn -> {
							Minecraft.getInstance().displayGuiScreen(returnTo);
						}));
					}
				});
	}

}
