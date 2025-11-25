package com.exemplo.visualpath.mixin;

import com.exemplo.visualpath.VisualPathMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderWorldBorder(Lnet/minecraft/client/render/Camera;)V", shift = At.Shift.BEFORE))
    private void onRender(RenderTickCounter tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, CallbackInfo ci) {
        if (VisualPathMod.currentPath.isEmpty()) return;

        List<BlockPos> path = VisualPathMod.currentPath;
        Vec3d cameraPos = camera.getPos();

        // 1. Configurar RenderSystem
        // Se estes métodos falharem, é 100% certeza que o 'fabric-loom' no build.gradle precisa ser atualizado.
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        // 2. Configurar Shader
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        
        Tessellator tessellator = Tessellator.getInstance();
        
        // 3. Iniciar BufferBuilder (Correção para 1.21)
        // Usamos DrawMode.LINES e importamos VertexFormat explicitamente
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);

        for (int i = 0; i < path.size() - 1; i++) {
            BlockPos posA = path.get(i);
            BlockPos posB = path.get(i + 1);

            // Ajuste de coordenadas (centralizar no bloco)
            float x1 = (float) (posA.getX() + 0.5f - cameraPos.x);
            float y1 = (float) (posA.getY() + 0.5f - cameraPos.y);
            float z1 = (float) (posA.getZ() + 0.5f - cameraPos.z);

            float x2 = (float) (posB.getX() + 0.5f - cameraPos.x);
            float y2 = (float) (posB.getY() + 0.5f - cameraPos.y);
            float z2 = (float) (posB.getZ() + 0.5f - cameraPos.z);

            // Desenhar linha
            buffer.vertex(x1, y1, z1).color(0f, 1f, 0f, 1f);
            buffer.vertex(x2, y2, z2).color(0f, 1f, 0f, 1f);
        }

        // 4. Finalizar e Renderizar
        // BufferRenderer substitui BufferUploader em muitos contextos de shader direto
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        // 5. Restaurar estado
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
