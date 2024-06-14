package eu.pb4.serveruifix.ui;

import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.serveruifix.util.EnchantingPhrases;
import eu.pb4.serveruifix.util.GuiTextures;
import eu.pb4.serveruifix.util.GuiUtils;
import eu.pb4.serveruifix.util.UiResourceCreator;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.IndexedIterable;

import java.util.*;

import static eu.pb4.serveruifix.ModInit.id;

public class EnchantmentGui extends SimpleGui {
    private final EnchantmentScreenHandler wrapped;
    private final Text realTitle;
    private int[] lastEnchantmentId;
    private int[] lastEnchantmentLevel;
    private int[] lastEnchantmentPower;
    private int lastLapisCount;
    private int lastExperience;

    public EnchantmentGui(Text text, EnchantmentScreenHandler handler, ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
        this.wrapped = handler;
        this.realTitle = text;
        this.setSlotRedirect(9 * 2 + 0, this.wrapped.getSlot(0));
        this.setSlotRedirect(9 * 2 + 2, this.wrapped.getSlot(1));
        this.updateEnchantments();
    }

    @Override
    public boolean onAnyClick(int index, ClickType type, SlotActionType action) {
        if (action == SlotActionType.QUICK_MOVE && (index == 18 || index == 18 + 2 || index > 9 * 3)) {
            var i = index > 9 * 3 ? index - 9 * 3 + 2 : (index == 18 ? 0 : 1);

            Slot slot = this.wrapped.slots.get(i);
            if (!slot.canTakeItems(player)) {
                return true;
            }

            ItemStack itemStack = this.wrapped.quickMove(player, i);

            while(!itemStack.isEmpty() && ItemStack.areItemsEqual(slot.getStack(), itemStack)) {
                itemStack = this.wrapped.quickMove(player, i);
            }

            return false;
        }

        return super.onAnyClick(index, type, action);
    }

    private void updateEnchantments() {
        this.lastEnchantmentId = Arrays.copyOf(this.wrapped.enchantmentId, this.wrapped.enchantmentId.length);
        this.lastEnchantmentLevel = Arrays.copyOf(this.wrapped.enchantmentLevel, this.wrapped.enchantmentLevel.length);
        this.lastEnchantmentPower = Arrays.copyOf(this.wrapped.enchantmentPower, this.wrapped.enchantmentPower.length);
        this.lastLapisCount = this.wrapped.getLapisCount();
        this.lastExperience = this.player.experienceLevel;
        var hasEnchantment = false;
        var text = Text.empty();
        var builder = new StringBuilder();
        builder.append(GuiTextures.ENCHANTMENT_OFFSET);
        EnchantingPhrases.getInstance().setSeed((long)this.wrapped.getSeed());
        var indexedIterable = player.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getIndexedEntries();

        for (int i = 0; i < 3; i++) {
            if (this.lastEnchantmentId[i] == -1 || this.lastEnchantmentPower[i] == 0) {
                for (int x = 0; x < 6; x++) {
                    this.clearSlot(i * 9 + 3 + x);
                }
                continue;
            }
            hasEnchantment = true;
            int finalI = i;
            GuiElementInterface.ClickCallback action = (index, type, _e, _d) -> {
                if (type.isLeft) {
                    GuiUtils.playClickSound(this.player);
                    this.wrapped.onButtonClick(this.player, finalI);
                }
            };


            var enchantment = indexedIterable.get(this.lastEnchantmentId[i]);
            if (enchantment == null) {
                continue;
            }
            int power = this.lastEnchantmentPower[i];
            int level = this.lastEnchantmentLevel[i];

            var name = Text.translatable("container.enchant.clue", Enchantment.getName(enchantment, level)).formatted(Formatting.WHITE);

            var lore = new ArrayList<Text>();
            int place = i + 1;

            var canEnchant = (lastLapisCount >= place && this.player.experienceLevel >= power) || this.player.getAbilities().creativeMode;

            if (!this.getPlayer().getAbilities().creativeMode) {
                lore.add(ScreenTexts.EMPTY);
                if (this.getPlayer().experienceLevel < level) {
                    lore.add(Text.translatable("container.enchant.level.requirement", power).formatted(Formatting.RED));
                } else {
                    MutableText mutableText;
                    if (place == 1) {
                        mutableText = Text.translatable("container.enchant.lapis.one");
                    } else {
                        mutableText = Text.translatable("container.enchant.lapis.many", place);
                    }

                    lore.add(mutableText.formatted(this.wrapped.getLapisCount() >= place ? Formatting.GRAY : Formatting.RED));
                    MutableText mutableText2;
                    if (place == 1) {
                        mutableText2 = Text.translatable("container.enchant.level.one");
                    } else {
                        mutableText2 = Text.translatable("container.enchant.level.many", place);
                    }

                    lore.add(mutableText2.formatted(Formatting.GRAY));
                }
            }


            for (int x = 0; x < 6; x++) {
                var e = (x == 0 ? (canEnchant ? GuiTextures.ENCHANTMENT_LEVEL[i] : GuiTextures.ENCHANTMENT_LEVEL_DISABLED[i]).get()
                        : GuiElementBuilder.from(GuiTextures.EMPTY.getItemStack())).setName(name).setLore(lore).setCallback(action);

                if (x == 5) {
                    e.setCount(power);
                }
                this.setSlot(i * 9 + 3 + x, e);
            }

            if (canEnchant) {
                builder.append(GuiTextures.ENCHANTMENT_BUTTON[i]);
                builder.append(GuiTextures.ENCHANTMENT_NEGATIVE_BUTTON);
            }
            builder.append(GuiTextures.ENCHANTMENT_TEXT_START);
            text.append(Text.literal(builder.toString()).setStyle(UiResourceCreator.STYLE));
            var string = EnchantingPhrases.getInstance().generatePhrase();

            text.append(Text.literal(string).setStyle(Style.EMPTY.withFont(id("alt/" + (i + 1))).withColor(canEnchant ? 0x675d49 : 0x332e25)));

            // Text
            builder = new StringBuilder();
            var l = DefaultFonts.ALT.getTextWidth(string, 8) + 2;

            for (; l >= 10; l -= 10) {
                builder.append(GuiTextures.NEGATIVE_10);
            }
            for (; l >= 1; l -= 1) {
                builder.append(GuiTextures.NEGATIVE_1);
            }
            builder.append(GuiTextures.ENCHANTMENT_TEXT_START_NEGATIVE);
        }

        builder.append(GuiTextures.ENCHANTMENT_NEGATIVE);
        text.append(Text.literal(builder.toString()).setStyle(UiResourceCreator.STYLE));
        this.setSlot(9 + 1, (hasEnchantment ? GuiTextures.BOOK_OPEN : GuiTextures.BOOK_CLOSED).get().hideTooltip());
        this.setTitle(GuiTextures.ENCHANTMENT.apply(text.append(this.realTitle)));
    }

    public OptionalInt openWithNumber() {
        this.open();

        if (this.screenHandler != null) {
            return OptionalInt.of(this.screenHandler.syncId);
        } else {
            return OptionalInt.empty();
        }
    }

    @Override
    public void onTick() {
        if (!wrapped.canUse(player)) {
            this.close();
            return;
        }

        if (!Arrays.equals(this.wrapped.enchantmentId, this.lastEnchantmentId)
                || !Arrays.equals(this.wrapped.enchantmentLevel, this.lastEnchantmentLevel)
                || !Arrays.equals(this.wrapped.enchantmentPower, this.lastEnchantmentPower)
                || this.lastLapisCount != this.wrapped.getLapisCount()
                || this.lastExperience != this.player.experienceLevel
        ) {
            this.updateEnchantments();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        wrapped.onClosed(this.player);
    }
}
