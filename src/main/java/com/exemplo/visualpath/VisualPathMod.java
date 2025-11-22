package com.exemplo.visualpath;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class VisualPathMod implements ModInitializer {
    public static List<BlockPos> currentPath = new ArrayList<>();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("path")
                .then(CommandManager.argument("target", BlockPosArgumentType.blockPos())
                    .executes(context -> {
                        BlockPos target = BlockPosArgumentType.getBlockPos(context, "target");
                        BlockPos start = context.getSource().getPlayer().getBlockPos();
                        
                        context.getSource().sendMessage(Text.literal("Calculando rota para " + target.toShortString() + "..."));
                        
                        new Thread(() -> {
                            List<BlockPos> path = Pathfinder.findPath(start, target, context.getSource().getWorld());
                            if (path != null && !path.isEmpty()) {
                                currentPath = path;
                                context.getSource().sendMessage(Text.literal("Caminho encontrado! " + path.size() + " blocos."));
                            } else {
                                context.getSource().sendMessage(Text.literal("Caminho não encontrado."));
                            }
                        }).start();
                        
                        return 1;
                    })
                ));
            
            dispatcher.register(CommandManager.literal("clearpath")
                .executes(context -> {
                    currentPath.clear();
                    context.getSource().sendMessage(Text.literal("Caminho limpo."));
                    return 1;
                }));
        });
    }
}
