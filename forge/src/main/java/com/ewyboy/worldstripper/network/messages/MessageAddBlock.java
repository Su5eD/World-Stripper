package com.ewyboy.worldstripper.network.messages;

import com.ewyboy.worldstripper.json.StripListHandler;
import com.ewyboy.worldstripper.stripclub.StripperAccessories;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

public class MessageAddBlock {

    public static void encode(MessageAddBlock pkt, FriendlyByteBuf buf) {}

    public static MessageAddBlock decode(FriendlyByteBuf buf) {
        return new MessageAddBlock();
    }

    public static void handle(MessageAddBlock message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                if(player.isSpectator() || player.isCreative()) {
                    BlockState state = StripperAccessories.getStateFromRaytrace();
                    if (state != null) {
                        String entry = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(state.getBlock())).toString();
                        if (StripListHandler.addEntry(entry)) {
                            player.sendSystemMessage(Component.literal(ChatFormatting.GREEN + entry + ChatFormatting.WHITE + " added to strip list"));
                        } else {
                            player.sendSystemMessage(Component.literal(ChatFormatting.DARK_RED + "ERROR: " + ChatFormatting.RED + entry + ChatFormatting.WHITE + " is already found in strip list"));
                        }
                    }
                } else {
                    player.sendSystemMessage(Component.literal(ChatFormatting.DARK_RED + "Error: " + ChatFormatting.WHITE + "You have to be in creative mode to use this feature!"));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
