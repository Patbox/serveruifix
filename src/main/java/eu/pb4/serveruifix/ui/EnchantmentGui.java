package eu.pb4.serveruifix.ui;

import eu.pb4.serveruifix.util.GuiTextures;
import eu.pb4.serveruifix.util.GuiUtils;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class EnchantmentGui extends SimpleGui {
    private final EnchantmentScreenHandler wrapped;
    private int[] lastEnchantmentId;
    private int[] lastEnchantmentLevel;
    private int[] lastEnchantmentPower;
    private int lastLapisCount;
    private int lastExperience;

    public EnchantmentGui(Text text, EnchantmentScreenHandler handler, ServerPlayerEntity player) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
        this.wrapped = handler;
        this.setTitle(GuiTextures.ENCHANTMENT.apply(text));
        this.setSlotRedirect(9 * 2 + 0, this.wrapped.getSlot(0));
        this.setSlotRedirect(9 * 2 + 2, this.wrapped.getSlot(1));
    }

    private void updateEnchantments() {
        for (int i = 0; i < 3; i++) {
            if (this.lastEnchantmentId[i] == -1) {
                for (int x = 0; x < 6; x++) {
                    this.clearSlot(i * 9 + 3 + x);
                }
                continue;
            }

            int finalI = i;
            GuiElementInterface.ClickCallback action = (index, type, _e, _d) -> {
                if (type.isLeft) {
                    GuiUtils.playClickSound(this.player);
                    this.wrapped.onButtonClick(this.player, finalI);
                }
            };
            var enchantment = Enchantment.byRawId(this.lastEnchantmentId[i]);
            int power = this.lastEnchantmentPower[i];
            int level = this.lastEnchantmentLevel[i];

            var name = Text.translatable("container.enchant.clue", enchantment.getName(level)).formatted(Formatting.WHITE);

            var lore = new ArrayList<Text>();
            int place = i + 1;

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
                var e = GuiElementBuilder.from(GuiTextures.EMPTY.getItemStack()).setName(name).setLore(lore).setCallback(action);

                if (x == 5) {
                    e.setCount(power);
                }

                this.setSlot(i * 9 + 3 + x, e);
            }
        }


        /*this.recipeEntries.add(new GuiElement(recipe.value().getResult(drm),
            );
        */
    }

    private void updateRecipeDisplay() {

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
        if (this.getSlot(3) != null && this.wrapped.getSlot(0).getStack().isEmpty()) {
            this.updateRecipeDisplay();
        }

        if (!Objects.equals(this.wrapped.enchantmentId, this.lastEnchantmentId)
                || !Objects.equals(this.wrapped.enchantmentLevel, this.lastEnchantmentLevel)
                || !Objects.equals(this.wrapped.enchantmentPower, this.lastEnchantmentPower)
                || this.lastLapisCount != this.wrapped.getLapisCount()
                || this.lastExperience != this.player.experienceLevel

        ) {
            this.lastEnchantmentId = Arrays.copyOf(this.wrapped.enchantmentId, this.wrapped.enchantmentId.length);
            this.lastEnchantmentLevel = Arrays.copyOf(this.wrapped.enchantmentLevel, this.wrapped.enchantmentLevel.length);
            this.lastEnchantmentPower = Arrays.copyOf(this.wrapped.enchantmentPower, this.wrapped.enchantmentPower.length);
            this.lastLapisCount = this.wrapped.getLapisCount();
            this.lastExperience = this.player.experienceLevel;
            this.updateEnchantments();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        wrapped.onClosed(this.player);
    }
}
