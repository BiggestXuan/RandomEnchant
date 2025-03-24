package biggestxuan.randomenchant;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;


@Mod(RandomEnchant.MODID)
public class RandomEnchant
{
    public static final String MODID = "random_enchant";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    //public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    //public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public RandomEnchant(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        //context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }

    @Nullable
    public static ResourceLocation rl(String s){
        return ResourceLocation.tryParse(s);
    }

    public static boolean applyEnchantment(ItemStack stack){
        Enchantment enchantment = getRightEnchantment(stack);
        if(stack.getItem().getMaxStackSize() > 1 || enchantment == null) return false;
        if(stack.getAllEnchantments().get(enchantment) == null){
            stack.enchant(enchantment,1);
            return true;
        }
        int level = stack.getAllEnchantments().get(enchantment);
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        enchantments.put(enchantment, level+1);
        EnchantmentHelper.setEnchantments(enchantments, stack);
        return true;
    }

    public static boolean stackCanUseEnchantment(ItemStack stack, Enchantment enchantment){
        if(stack.getAllEnchantments().get(enchantment) != null) return true;
        for(var e : stack.getAllEnchantments().keySet()){
            if(!e.isCompatibleWith(enchantment)) return false;
        }
        return true;
    }

    public static Enchantment getRightEnchantment(ItemStack stack){
        for (int i = 0; i < 150; i++) {
            Enchantment enchantment = getRandomEnchant();
            if(enchantment == null){
                continue;
            }
            if(enchantment.category.canEnchant(stack.getItem())){
                if(stackCanUseEnchantment(stack,enchantment)){
                    return enchantment;
                }
            }
        }
        return null;
    }

    public static Enchantment getRandomEnchant(){
        return getRandomValue(ForgeRegistries.ENCHANTMENTS.getValues());
    }

    public static void additionCost(Player player,ItemStack stack){
        additionCost(player,stack,1);
    }

    public static void additionCost(Player player,ItemStack stack,float rate){
        if(stack.isEnchanted()){
            var enchantments = stack.getAllEnchantments();
            enchantments.forEach((enchantment,level) -> {
                float i;
                Enchantment.Rarity rarity = enchantment.getRarity();
                i = switch (rarity) {
                    case COMMON -> 0.4F;
                    case UNCOMMON -> 1F;
                    case RARE -> 2.25F;
                    case VERY_RARE -> 4F;
                };
                i *= level;
                i *= rate;
                stack.hurtAndBreak(Math.round(i),player,(p) -> {});
            });
        }
    }

    public static <T> T getRandomValue(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        List<T> list = new ArrayList<>(collection);
        Random random = new Random();
        int randomIndex = random.nextInt(list.size());
        return list.get(randomIndex);
    }
}
