package me.devnatan.inventoryframework;

import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.createTitleComponent;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.getContainerOrName;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.packetPlayOutOpenWindow;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.useContainers;

import java.util.Objects;
import me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate;
import me.devnatan.inventoryframework.runtime.thirdparty.ReflectionUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class AnvilInputNMS {

    private AnvilInputNMS() {}

    public static Inventory open(Player player, Object title, String initialInput) {
        try {
            ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            AbstractContainerMenu container = entityPlayer.inventoryMenu;
            entityPlayer.containerMenu = container;

            final int windowId =
                    entityPlayer
                            .nextContainerCounter(); // (int) GET_PLAYER_NEXT_CONTAINER_COUNTER.invoke(entityPlayer);
            final AnvilMenu anvilContainer =
                    new AnvilMenu(windowId, entityPlayer.getInventory()); // ANVIL_CONSTRUCTOR.invoke(windowId,
            // GET_PLAYER_INVENTORY.invoke(entityPlayer));
            anvilContainer.checkReachable = false;
            // CONTAINER_CHECK_REACHABLE.invoke(anvilContainer, false);

            final AnvilInventory inventory = (AnvilInventory)
                    ((InventoryView) InventoryUpdate.getBukkitView.invoke(anvilContainer)).getTopInventory();

            inventory.setMaximumRepairCost(0);

            @SuppressWarnings("deprecation")
            final ItemStack item = new ItemStack(Material.PAPER, 1, (short) 0);
            final ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
            meta.setDisplayName(initialInput);
            item.setItemMeta(meta);
            inventory.setItem(0, item);

            Object nmsContainers = getContainerOrName(InventoryUpdate.Containers.ANVIL, InventoryType.ANVIL);
            Object updatedTitle = createTitleComponent(title == null ? "" : title);
            Object openWindowPacket = useContainers()
                    ? packetPlayOutOpenWindow.invoke(windowId, nmsContainers, updatedTitle)
                    : packetPlayOutOpenWindow.invoke(
                            windowId, nmsContainers, updatedTitle, InventoryType.ANVIL.getDefaultSize());

            ReflectionUtils.sendPacketSync(player, openWindowPacket);
            entityPlayer.containerMenu = anvilContainer;

            entityPlayer.initMenu(anvilContainer);

            return inventory;
        } catch (Throwable throwable) {
            throw new RuntimeException("Something went wrong while opening Anvil Input NMS inventory.", throwable);
        }
    }
}
