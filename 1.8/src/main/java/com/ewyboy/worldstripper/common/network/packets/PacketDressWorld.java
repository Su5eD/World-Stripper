package com.ewyboy.worldstripper.common.network.packets;

import com.ewyboy.worldstripper.common.network.PacketHandler;
import com.ewyboy.worldstripper.common.util.Config;
import com.mojang.realmsclient.gui.ChatFormatting;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by EwyBoy
 **/
public class PacketDressWorld implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketDressWorld, IMessage> {

        @Override
        public IMessage onMessage(final PacketDressWorld message, final MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(ctx));
            return null;
        }

        private void handle(MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            World world = player.worldObj;

            double chunkClearSize = ((16 * Config.chuckRadius) / 2);

            if (player.capabilities.isCreativeMode) {
                if (PacketHandler.hashedBlockCache != null) {
                    player.addChatComponentMessage(new ChatComponentText(ChatFormatting.BOLD + "" + ChatFormatting.RED + "WARNING! " + ChatFormatting.WHITE + "World Dressing Initialized! Lag May Occur.."));
                    for (int x = (int)(player.getPosition().getX() - chunkClearSize); (double)x <= player.getPosition().getX() + chunkClearSize; x++) {
                        for (int y = 0; (double)y <= player.getPosition().getY() + chunkClearSize; ++y) {
                            for (int z = (int)(player.getPosition().getZ() - chunkClearSize); (double)z <= player.getPosition().getZ() + chunkClearSize; z++) {
                                BlockPos targetBlockPos = new BlockPos(x,y,z);
                                if (PacketHandler.hashedBlockCache.get(targetBlockPos) != null) {
                                    world.setBlockState(targetBlockPos, PacketHandler.hashedBlockCache.get(targetBlockPos), 3);
                                }
                            }
                        }
                    }
                    player.addChatMessage(new ChatComponentText("World Dressing Successfully Done!"));
                } else {
                    player.addChatMessage(new ChatComponentText("Non Cords have been cashed"));
                }
            } else {
                player.addChatMessage(new ChatComponentText(ChatFormatting.RED + "Error: You have to be in creative mode to use this feature!"));
            }
        }
    }
}