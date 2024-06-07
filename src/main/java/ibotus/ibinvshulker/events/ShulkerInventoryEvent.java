package ibotus.ibinvshulker.events;


import ibotus.ibinvshulker.configurations.Config;

import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerInventoryEvent implements Listener {

    @EventHandler
    public void onShulkerPickup(PlayerAttemptPickupItemEvent event) {
        if (!Config.getConfig().getBoolean("shulker-inventory.enabled")) {
            return;
        }

        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {

            ItemStack pickupItem = event.getItem().getItemStack();
            for (ItemStack item : inventory.getContents()) {
                if (item != null && Config.getWhitelistedShulkers().contains(item.getType())) {

                    BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                    if (meta != null && meta.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
                        Inventory shulkerInventory = shulkerBox.getInventory();

                        if (shulkerInventory.firstEmpty() != -1) {
                            if (Config.getBlockedItems().contains(pickupItem.getType())) {
                                event.setCancelled(true);
                                return;
                            }
                            shulkerInventory.addItem(pickupItem);
                            meta.setBlockState(shulkerBox);
                            item.setItemMeta(meta);
                            event.getItem().remove();

                            String soundKey = "sound-pickup";
                            Sound sound = Sound.valueOf(Config.getConfig().getString(soundKey + ".sound"));
                            float volume = (float) Config.getConfig().getDouble(soundKey + ".volume");
                            float pitch = (float) Config.getConfig().getDouble(soundKey + ".pitch");
                            player.playSound(player.getLocation(), sound, volume, pitch);
                            return;
                        }
                    }
                }
            }
        }
    }
}
