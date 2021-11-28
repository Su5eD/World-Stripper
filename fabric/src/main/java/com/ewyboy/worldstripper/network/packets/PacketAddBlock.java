package com.ewyboy.worldstripper.network.packets;

import com.ewyboy.worldstripper.WorldStripper;
import com.ewyboy.worldstripper.json.JsonHandler;
import com.ewyboy.worldstripper.network.IPacket;
import com.ewyboy.worldstripper.stripclub.StripperAccessories;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.state.BlockState;

public class PacketAddBlock implements IPacket {

    public static final ResourceLocation ID = new ResourceLocation(WorldStripper.MOD_ID, "add_block_packet");

    public PacketAddBlock() {}

    @Override
    public void read(FriendlyByteBuf packetByteBuf) {}

    @Override
    public void write(FriendlyByteBuf packetByteBuf) {}

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    public static class Handler implements ServerPlayNetworking.PlayChannelHandler {

        @Override
        public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
            if(player.isSpectator() || player.isCreative()) {
                BlockState state = StripperAccessories.getStateFromRaytrace();
                if (state != null) {
                    String entry = Registry.BLOCK.getKey(state.getBlock()).toString();
                    if (JsonHandler.addEntry(entry)) {
                        player.sendMessage(new TextComponent(ChatFormatting.GREEN + entry + ChatFormatting.WHITE + " added to strip list"), ChatType.GAME_INFO, player.getUUID());
                    } else {
                        //TODO: fix color for text
                        player.sendMessage(new TextComponent(ChatFormatting.RED + "ERROR: " + entry + ChatFormatting.WHITE + " is already found in strip list"), ChatType.GAME_INFO, player.getUUID());
                    }
                }
            } else {
                player.sendMessage(new TextComponent(ChatFormatting.RED + "Error: You have to be in creative mode to use this feature!"), ChatType.GAME_INFO, player.getUUID());
            }
        }
    }

}