package me.devnatan.inventoryframework;

import java.util.function.BiConsumer;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.component.PaginationElementFactory;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("rawtypes")
public final class PaginationStateBuilder<
        Context extends IFContext,
        SlotContext extends IFSlotContext,
        Builder extends ItemComponentBuilder<Builder, Context> & ComponentFactory,
        V> {

    private final PlatformView root;
    private final Object sourceProvider;
    private char layoutTarget = LayoutSlot.FILLED_RESERVED_CHAR;
    private PaginationElementFactory<Context, V> elementFactory;
    private BiConsumer<Context, Pagination> pageSwitchHandler;

    PaginationStateBuilder(PlatformView root, Object sourceProvider) {
        this.root = root;
        this.sourceProvider = sourceProvider;
    }

    /**
     * Sets the element factory for pagination.
     * <p>
     * It consists of a function whose first parameter is a derivation of the
     * {@link ItemComponentBuilder} that must be used to configure the item, and the second
     * parameter is the current element being paginated.
     * <p>
     * This function is called for every single paginated element.
     *
     * @param elementFactory The element factory.
     * @return This pagination state builder.
     */
    public PaginationStateBuilder<Context, SlotContext, Builder, V> elementFactory(
            @NotNull PaginationElementFactory<Context, V> elementFactory) {
        this.elementFactory = elementFactory;
        return this;
    }

    /**
     * Sets the item factory for pagination.
     * <p>
     * It consists of a function whose first parameter is a derivation of the
     * {@link ItemComponentBuilder} that must be used to configure the item, and the second
     * parameter is the current element being paginated.
     * <p>
     * This function is called for every single paginated element.
     *
     * @param itemFactory The item factory.
     * @return This pagination state builder.
     */
    public PaginationStateBuilder<Context, SlotContext, Builder, V> itemFactory(
            @NotNull BiConsumer<Builder, V> itemFactory) {
        return elementFactory((context, index, slot, value) -> {
            @SuppressWarnings("unchecked")
            Builder builder = (Builder) root.getElementFactory().createComponentBuilder(context);
            builder.withSlot(slot).withExternallyManaged(true);
            itemFactory.accept(builder, value);
            return builder;
        });
    }

    /**
     * Defines a target character in the layout whose pagination will be rendered.
     * <p>
     * By default, if there is a layout available and a target character has not
     * been explicitly  defined in the layout, the layout's renderization target
     * character will be the {@link LayoutSlot#FILLED_RESERVED_CHAR reserved layout character}.
     * <p>
     * If there is no layout configured, pagination will be rendered throughout the view container.
     *
     * @param layoutTarget The target layout character.
     * @return This pagination state builder.
     */
    public PaginationStateBuilder<Context, SlotContext, Builder, V> layoutTarget(char layoutTarget) {
        this.layoutTarget = layoutTarget;
        return this;
    }

    /**
     * Handles the page switching action.
     * <p>
     * The first parameter is the previous page and the current page can be
     * obtained through {@link Pagination#currentPage()}.
     *
     * @param pageSwitchHandler The page switch handler.
     * @return This pagination state builder.
     */
    public PaginationStateBuilder<Context, SlotContext, Builder, V> onPageSwitch(
            @NotNull BiConsumer<Context, Pagination> pageSwitchHandler) {
        this.pageSwitchHandler = pageSwitchHandler;
        return this;
    }

    /**
     * Builds a pagination state based on this builder values.
     *
     * @return A new {@link Pagination} state.
     * @throws IllegalStateException If the element factory wasn't set.
     */
    public State<Pagination> build() {
        if (elementFactory == null)
            throw new IllegalStateException(String.format(
                    "Element factory from #buildPaginationState(...) cannot be null. Set it using %s or %s.",
                    "#elementFactory(PaginationElementFactory)", "#itemFactory(BiConsumer)"));

        return root.buildPaginationState(this);
    }

    PlatformView getRoot() {
        return root;
    }

    Object getSourceProvider() {
        return sourceProvider;
    }

    char getLayoutTarget() {
        return layoutTarget;
    }

    PaginationElementFactory<Context, V> getElementFactory() {
        return elementFactory;
    }

    BiConsumer<Context, Pagination> getPageSwitchHandler() {
        return pageSwitchHandler;
    }
}