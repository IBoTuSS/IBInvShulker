package ibotus.ibinvshulker.event;

import ibotus.ibinvshulker.configurations.Config;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.List;

public class ShulkerInventoryEvent implements Listener {

    @EventHandler
    public void onShulkerPickup(PlayerAttemptPickupItemEvent event) {
        if (!Config.getConfig().getBoolean("shulker-inventory.enabled")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack pickupItem = event.getItem().getItemStack();
        Inventory playerInventory = player.getInventory();

        List<Material> blockedItems = Config.getBlockedItems();
        if (blockedItems.contains(pickupItem.getType())) {
            return;
        }

        if (playerInventory.firstEmpty() != -1) {
            return;
        }

        for (ItemStack item : playerInventory.getContents()) {
            if (item != null && Config.getWhitelistedShulkers().contains(item.getType())) {
                BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                if (meta != null && meta.getBlockState() instanceof ShulkerBox) {
                    ShulkerBox shulkerBox = (ShulkerBox) meta.getBlockState();
                    Inventory shulkerInventory = shulkerBox.getInventory();

                    int spaceLeftInShulker = calculateSpaceLeftInShulker(shulkerInventory, pickupItem);
                    if (spaceLeftInShulker > 0) {
                        int amountToAdd = Math.min(spaceLeftInShulker, pickupItem.getAmount());

                        ItemStack toAdd = pickupItem.clone();
                        toAdd.setAmount(amountToAdd);

                        shulkerInventory.addItem(toAdd);

                        pickupItem.setAmount(pickupItem.getAmount() - amountToAdd);

                        meta.setBlockState(shulkerBox);
                        item.setItemMeta(meta);

                        if (pickupItem.getAmount() <= 0) {
                            event.setCancelled(true);
                        }

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


    private int calculateSpaceLeftInShulker(Inventory shulkerInventory, ItemStack item) {
        int spaceLeft = 0;
        for (ItemStack content : shulkerInventory.getContents()) {
            if (content == null) {
                spaceLeft += item.getMaxStackSize();
            } else if (content.isSimilar(item)) {
                spaceLeft += (item.getMaxStackSize() - content.getAmount());
            }
        }
        return spaceLeft;
    }
}