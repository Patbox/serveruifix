package eu.pb4.serveruifix.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.serveruifix.ModInit;
import eu.pb4.serveruifix.ui.StonecutterGui;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.StonecutterScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "openHandledScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void replaceScreenHandled(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir, @Local ScreenHandler handler) {
        var player = (ServerPlayerEntity) (Object) this;
        if (handler instanceof StonecutterScreenHandler screenHandler && !(ModInit.DEV_MODE && player.getMainHandStack().isOf(Items.DEBUG_STICK)) ) {
            cir.setReturnValue(new StonecutterGui(factory.getDisplayName(), screenHandler, player).openWithNumber());
        }
    }
}
