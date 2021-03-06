package com.github.wolfshotz.wyrmroost.data;

import com.github.wolfshotz.wyrmroost.Wyrmroost;
import com.github.wolfshotz.wyrmroost.items.CoinDragonItem;
import com.github.wolfshotz.wyrmroost.items.LazySpawnEggItem;
import com.github.wolfshotz.wyrmroost.registry.WRBlocks;
import com.github.wolfshotz.wyrmroost.registry.WRItems;
import com.github.wolfshotz.wyrmroost.util.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.TieredItem;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("ConstantConditions")
class ModelData
{
    private static ExistingFileHelper theGOODExistingFileHelper;

    static void provide(DataGenerator gen, ExistingFileHelper fileHelper)
    {
        theGOODExistingFileHelper = fileHelper;

        gen.addProvider(new Blocks(gen));
        gen.addProvider(new Items(gen));
    }

    private static class Blocks extends BlockStateProvider
    {
        private final List<String> MISSING_TEXTURES = new ArrayList<>();

        public Blocks(DataGenerator generator)
        {
            super(generator, Wyrmroost.MOD_ID, theGOODExistingFileHelper);
        }

        @Override
        protected void registerStatesAndModels()
        {
            // All unregistered blocks will be done here. They will be simple blocks with all sides of the same texture
            // If this is unwanted, it is important to define so above
            for (Block block : ModUtils.getRegistryEntries(WRBlocks.REGISTRY))
            {
                if (registeredBlocks.containsKey(block)) continue;
                if (block instanceof FlowingFluidBlock) continue;

                ResourceLocation name = block.getRegistryName();
                if (!theGOODExistingFileHelper.exists(new ResourceLocation(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath()), ResourcePackType.CLIENT_RESOURCES, ".png", "textures"))
                    MISSING_TEXTURES.add(name.getPath().replace("block/", ""));
                else simpleBlock(block);
            }

            if (!MISSING_TEXTURES.isEmpty())
                Wyrmroost.LOG.error("Blocks are missing Textures! Models will not be registered: {}", MISSING_TEXTURES.toString());
        }
    }

    private static class Items extends ItemModelProvider
    {
        private final List<Item> REGISTERED = new ArrayList<>();

        public Items(DataGenerator generator)
        {
            super(generator, Wyrmroost.MOD_ID, theGOODExistingFileHelper);
        }

        private static ResourceLocation resource(String path)
        {
            return Wyrmroost.rl("item/" + path);
        }

        public ItemModelBuilder item(Item item)
        {
            ItemModelBuilder builder = itemBare(item);

            // model
            String parent = (item instanceof TieredItem)? "item/handheld" : "item/generated";
            builder.parent(new ModelFile.UncheckedModelFile(parent));

            // texture
            ResourceLocation texture = resource(item.getRegistryName().getPath());
            if (theGOODExistingFileHelper.exists(texture, ResourcePackType.CLIENT_RESOURCES, ".png", "textures"))
                builder.texture("layer0", texture);
            else
                Wyrmroost.LOG.warn("Missing Texture for Item: {} , model will not be registered.", texture.getPath().replace("item/", ""));

            return builder;
        }

        public ItemModelBuilder itemBare(Item item)
        {
            REGISTERED.add(item);
            return getBuilder(item.getRegistryName().getPath());
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected void registerModels()
        {
            itemBare(WRItems.DRAGON_EGG.get())
                    .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                    .guiLight(BlockModel.GuiLight.FRONT)
                    .transforms()
                    .transform(ModelBuilder.Perspective.GUI).rotation(160, 8, 30).translation(21, 6, 0).scale(1.5f).end()
                    .transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT).rotation(180, 10, 4).translation(13, 10, -10).scale(1).end()
                    .transform(ModelBuilder.Perspective.FIRSTPERSON_LEFT).rotation(180, 10, 4).translation(-2, 11, -12).scale(1).end()
                    .transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT).rotation(253, 65, 0).translation(8, 2, 10).scale(0.75f).end()
                    .transform(ModelBuilder.Perspective.THIRDPERSON_LEFT).rotation(253, 65, 0).translation(3, 13, 7).scale(0.75f).end()
                    .transform(ModelBuilder.Perspective.GROUND).rotation(180, 0, 0).translation(4, 8, -5).scale(0.55f).end();

            getBuilder("desert_wyrm_alive")
                    .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
                    .texture("layer0", resource("desert_wyrm_alive"));
            item(WRItems.LDWYRM.get())
                    .override()
                    .predicate(Wyrmroost.rl("is_alive"), 1f)
                    .model(new ModelFile.UncheckedModelFile(resource("desert_wyrm_alive")));

            final ItemModelBuilder cdBuilder = item(WRItems.COIN_DRAGON.get());
            IntStream.range(1, 5).forEach(i ->
            {
                String path = "coin_dragon" + i;
                getBuilder(path)
                        .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
                        .texture("layer0", resource(path));
                cdBuilder.override()
                        .predicate(CoinDragonItem.VARIANT_OVERRIDE, i)
                        .model(new ModelFile.UncheckedModelFile(resource(path)));
            });

            item(WRItems.DRAGON_STAFF.get()).parent(new ModelFile.UncheckedModelFile("item/handheld"));
            for (LazySpawnEggItem i : LazySpawnEggItem.EGG_TYPES)
                itemBare(i).parent(new ModelFile.UncheckedModelFile(mcLoc("item/template_spawn_egg")));

            for (Block block : ModUtils.getRegistryEntries(WRBlocks.REGISTRY)) // All Standard ItemBlocks
            {
                if (REGISTERED.contains(block.asItem())) continue;
                if (block instanceof FlowingFluidBlock) // Buckets
                {
                    itemBare(((FlowingFluidBlock) block).getFluid().getFilledBucket()).parent(new ModelFile.UncheckedModelFile("forge:item/bucket"));
                    continue;
                }

                ResourceLocation path = block.getRegistryName();
                itemBare(block.asItem()).parent(new ModelFile.UncheckedModelFile(path.getNamespace() + ":block/" + path.getPath()));
            }

            // All items that do not require custom attention
            for (Item item : ModUtils.getRegistryEntries(WRItems.REGISTRY)) if (!REGISTERED.contains(item)) item(item);
        }

        @Override
        public String getName() { return "Wyrmroost Item Models"; }
    }
}
