package abby.mod.uhc;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.layer.Layer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UHCBiomeProvider extends BiomeProvider {
	public static void registerBiomeProvider() {
		Registry.register(Registry.BIOME_PROVIDER_CODEC, new ResourceLocation("abbyuhc", "uhcbiomeprovider"), UHCBiomeProvider.CODEC);
	}

	public static final Codec<UHCBiomeProvider> CODEC = RecordCodecBuilder.create((builder) -> builder.group(
			Codec.LONG.fieldOf("seed").stable().forGetter((overworldProvider) -> overworldProvider.seed),
			Codec.BOOL.optionalFieldOf("legacy_biome_init_layer", false, Lifecycle.stable()).forGetter((overworldProvider) -> overworldProvider.legacyBiomes),
			Codec.BOOL.fieldOf("large_biomes").orElse(false).stable().forGetter((overworldProvider) -> overworldProvider.largeBiomes),
			RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY).forGetter((overworldProvider) -> overworldProvider.lookupRegistry))
		.apply(builder, builder.stable(UHCBiomeProvider::new)));

	private final Layer genBiomes;
	public static final List<RegistryKey<Biome>> BLACKLISTED_BIOMES = ImmutableList.of(Biomes.MOUNTAINS);

	private final long seed;
	private final boolean legacyBiomes;
	private final boolean largeBiomes;
	private final Registry<Biome> lookupRegistry;

	public UHCBiomeProvider(long seed, boolean legacyBiomes, boolean largeBiomes, Registry<Biome> lookupRegistry) {
		super(generateListOfSpawnableBiomes(lookupRegistry));
		this.seed = seed;
		this.legacyBiomes = legacyBiomes;
		this.largeBiomes = largeBiomes;
		this.lookupRegistry = lookupRegistry;
		this.genBiomes = UHCLayerUtil.func_237215_a_(lookupRegistry, seed, legacyBiomes, largeBiomes ? 6 : 4, 4);
		AbbyUHC.LOGGER.fatal("UHC WORLD GEN");
	}
	private static Stream<Supplier<Biome>> generateListOfSpawnableBiomes(Registry<Biome> lookupRegistry){
		List<Supplier<Biome>> biomes = new ArrayList<>();

		// iterate over all temp categories
		for (BiomeManager.BiomeType type : BiomeManager.BiomeType.values()){
			// Collect all biomes we will spawn including modded biomes that want to spawn in overworld
			biomes.addAll(
					BiomeManager.getBiomes(type).stream()
					.map(BiomeManager.BiomeEntry::getKey)
					.filter(biomeRegistryKey -> !BLACKLISTED_BIOMES.contains(biomeRegistryKey)) // Remove all blacklisted biomes
					.map((Function<? super RegistryKey<Biome>, ? extends Supplier<Biome>>) biomeRegistryKey -> () -> lookupRegistry.getOrThrow(biomeRegistryKey))
					.collect(Collectors.toList())
			);
		}

		return biomes.stream();
	}

	@OnlyIn(Dist.CLIENT)
	public BiomeProvider getBiomeProvider(long seed) {
		return new UHCBiomeProvider(seed, this.legacyBiomes, this.largeBiomes, this.lookupRegistry);
	}

	public Biome getNoiseBiome(int x, int y, int z) {
		return this.genBiomes.func_242936_a(this.lookupRegistry, x, z);
	}

	@Override
	protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
		return CODEC;
	}
}
