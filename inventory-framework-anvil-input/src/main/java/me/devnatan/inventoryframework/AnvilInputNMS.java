package me.devnatan.inventoryframework;

import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.createTitleComponent;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.getContainerOrName;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.packetPlayOutOpenWindow;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.useContainers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

    //    // CONSTRUCTORS
    //    private static final MethodHandle ANVIL_CONSTRUCTOR;
    //    private static final Class<?> ANVIL;
    //
    //    // METHODS
    //    private static final MethodHandle GET_PLAYER_NEXT_CONTAINER_COUNTER;
    //    private static final MethodHandle GET_PLAYER_INVENTORY;
    //    private static final MethodHandle SET_PLAYER_ACTIVE_CONTAINER;
    //    private static final MethodHandle ADD_CONTAINER_SLOT_LISTENER;
    //    private static final MethodHandle INIT_MENU;
    //
    //    // FIELDS
    //    private static final MethodHandle CONTAINER_CHECK_REACHABLE;
    //    private static final MethodHandle PLAYER_DEFAULT_CONTAINER;
    //    private static final MethodHandle CONTAINER_WINDOW_ID;

    //    static {
    //        try {
    //            ANVIL = Objects.requireNonNull(
    //                    getNMSClass("world.inventory", "ContainerAnvil"), "ContainerAnvil NMS class not found");
    //
    //            final Class<?> playerInventoryClass = getNMSClass("world.entity.player", "PlayerInventory");
    //
    //            ANVIL_CONSTRUCTOR = getConstructor(ANVIL, int.class, playerInventoryClass);
    //            CONTAINER_CHECK_REACHABLE = setFieldHandle(CONTAINER, boolean.class, "checkReachable");
    //
    //            final Class<?> containerPlayer = getNMSClass("world.inventory", "ContainerPlayer");
    //            PLAYER_DEFAULT_CONTAINER = getField(ENTITY_PLAYER, containerPlayer, "inventoryMenu", "bQ", "bR");
    //
    //            final String activeContainerObfuscatedName = ReflectionUtils.supportsMC1202() ? "bS" : "bR";
    //            SET_PLAYER_ACTIVE_CONTAINER = setField(
    //                    ENTITY_PLAYER, containerPlayer, "activeContainer", "containerMenu",
    // activeContainerObfuscatedName);
    //
    //            GET_PLAYER_NEXT_CONTAINER_COUNTER =
    //                    getMethod(ENTITY_PLAYER, "nextContainerCounter", MethodType.methodType(int.class));
    //
    //            GET_PLAYER_INVENTORY =
    //                    getMethod(ENTITY_PLAYER, "fN", MethodType.methodType(playerInventoryClass), false, "fR");
    //
    //            CONTAINER_WINDOW_ID = setField(CONTAINER, int.class, "windowId", "containerId", "j");
    //            ADD_CONTAINER_SLOT_LISTENER = getMethod(
    //                    CONTAINER, "a", MethodType.methodType(void.class, getNMSClass("world.inventory.ICrafting")));
    //            INIT_MENU = getMethod(ENTITY_PLAYER, "a", MethodType.methodType(void.class, CONTAINER));
    //        } catch (Exception exception) {
    //            throw new RuntimeException(
    //                    "Unsupported version for Anvil Input feature: " + ReflectionUtils.getVersionInformation(),
    //                    exception);
    //        }
    //    }

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
            // SET_PLAYER_ACTIVE_CONTAINER.invoke(entityPlayer, anvilContainer);
            entityPlayer.containerMenu = anvilContainer;
            setContainerId(anvilContainer, windowId);
            // CONTAINER_WINDOW_ID.invoke(anvilContainer, windowId);

            entityPlayer.initMenu(anvilContainer);
            // INIT_MENU.invoke(entityPlayer, anvilContainer);

            return inventory;
        } catch (Throwable throwable) {
            throw new RuntimeException("Something went wrong while opening Anvil Input NMS inventory.", throwable);
        }
    }

    private static final Field containerIdField;

    static {
        Field tmp = null;
        try {
            tmp = AbstractContainerMenu.class.getDeclaredField("containerId");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        containerIdField = tmp;
        containerIdField.setAccessible(true);

        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(containerIdField, containerIdField.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setContainerId(AbstractContainerMenu container, int id) {
        try {
            containerIdField.set(container, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
