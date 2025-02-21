package vazkii.quark.content.building.module;

import java.util.function.ToIntFunction;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.building.block.SoulFurnaceBlock;
import vazkii.quark.content.building.block.VariantFurnaceBlock;
import vazkii.quark.content.building.block.be.VariantFurnaceBlockEntity;

@LoadModule(category = ModuleCategory.BUILDING)
public class VariantFurnacesModule extends QuarkModule {

	public static BlockEntityType<VariantFurnaceBlockEntity> blockEntityType;

	public static Block deepslateFurnace;
	@Hint public static Block blackstoneFurnace;
	
	@Override
	public void register() {
		deepslateFurnace = new VariantFurnaceBlock("deepslate", this, Properties.copy(Blocks.DEEPSLATE).lightLevel(litBlockEmission(13)));
		blackstoneFurnace = new SoulFurnaceBlock("blackstone", this, Properties.copy(Blocks.BLACKSTONE).lightLevel(litBlockEmission(13)));

		blockEntityType = BlockEntityType.Builder.of(VariantFurnaceBlockEntity::new, deepslateFurnace, blackstoneFurnace).build(null);
		RegistryHelper.register(blockEntityType, "variant_furnace", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
	}

	private static ToIntFunction<BlockState> litBlockEmission(int lvl) {
		return s -> s.getValue(BlockStateProperties.LIT) ? lvl : 0;
	}

}
