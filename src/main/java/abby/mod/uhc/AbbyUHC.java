package abby.mod.uhc;

import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.data.BiomeProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.client.ForgeWorldTypeScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("abbyuhc")
public class AbbyUHC {
	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	public static ForgeWorldType uhcWorldType = new UHCWorldType();

	public AbbyUHC() {
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the enqueueIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ForgeWorldType.class,
				this::registerWorldTypes);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerWorldTypeScreenFactories);

	}

	private void setup(final FMLCommonSetupEvent event) {
		// some preinit code
		LOGGER.info("HELLO FROM PREINIT");
		LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		// do something that can only be done on the client
		LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		// some example code to dispatch IMC to another mod
		InterModComms.sendTo("examplemod", "helloworld", () -> {
			LOGGER.info("Hello world from the MDK");
			return "Hello world";
		});
	}

	private void processIMC(final InterModProcessEvent event) {
		// some example code to receive and process InterModComms from other mods
		LOGGER.info("Got IMC {}",
				event.getIMCStream().map(m -> m.getMessageSupplier().get()).collect(Collectors.toList()));
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		// do something when the server starts
		LOGGER.info("HELLO from server starting");
	}

	// You can use EventBusSubscriber to automatically subscribe events on the
	// contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
			// register a new block here
			LOGGER.info("HELLO from Register Block");
			LOGGER.fatal("Stuff");
			Registry.register(Registry.BIOME_PROVIDER_CODEC, new ResourceLocation("abbyuhc", "uhcbiomeprovider"),
					UHCBiomeProvider.CODEC);
		}
	}

	private void registerWorldTypes(RegistryEvent.Register<ForgeWorldType> event) {
		event.getRegistry()
				.registerAll(new ForgeWorldType(DimensionGeneratorSettings::func_242750_a).setRegistryName("uhc"));
//		event.getRegistry()
//				.registerAll(new ForgeWorldType(this::createChunkGenerator).setRegistryName("test_world_type2"));
	}

	private ChunkGenerator createChunkGenerator(Registry<Biome> biomes, Registry<DimensionSettings> dimensionSettings,
			long seed, String settings) {
		return DimensionGeneratorSettings.func_242750_a(biomes, dimensionSettings, seed);
	}

	private void registerWorldTypeScreenFactories(FMLClientSetupEvent event) {
		ForgeWorldTypeScreens.registerFactory(uhcWorldType,
				(returnTo, dimensionGeneratorSettings) -> new Screen(uhcWorldType.getDisplayName()) {
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
