package biggestxuan.randomenchant;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @Author Biggest_Xuan
 * 2025/3/24
 */

@Mod.EventBusSubscriber(modid = RandomEnchant.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Event {
    @SubscribeEvent
    public static void onAttackOrHurt(LivingHurtEvent event){
        LivingEntity living = event.getEntity();
        DamageSource source = event.getSource();
        if(source.getDirectEntity() instanceof Player player){
            RandomEnchant.additionCost(player,player.getMainHandItem());
            applyMainHandEnchantment(player);
        }
        if(living instanceof Player player){
            player.getArmorSlots().forEach(e -> {
                RandomEnchant.additionCost(player, e,0.25F);
                RandomEnchant.applyEnchantment(e);
            });
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event){
        Player player = event.getPlayer();
        if(player != null){
            RandomEnchant.additionCost(player,player.getMainHandItem());
            applyMainHandEnchantment(player);
        }
    }

    public static void applyMainHandEnchantment(Player player){
        ItemStack stack = player.getMainHandItem();
        RandomEnchant.applyEnchantment(stack);
    }
}
