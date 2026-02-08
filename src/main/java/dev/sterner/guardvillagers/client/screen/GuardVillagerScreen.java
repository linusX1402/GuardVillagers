package dev.sterner.guardvillagers.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.sterner.guardvillagers.GuardVillagers;
import dev.sterner.guardvillagers.GuardVillagersConfig;
import dev.sterner.guardvillagers.common.entity.GuardEntity;
import dev.sterner.guardvillagers.common.network.GuardFollowPacket;
import dev.sterner.guardvillagers.common.network.GuardPatrolPacket;
import dev.sterner.guardvillagers.common.screenhandler.GuardVillagerScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class GuardVillagerScreen extends HandledScreen<GuardVillagerScreenHandler> {

    private static final Identifier GUARD_GUI_TEXTURES = new Identifier(GuardVillagers.MODID, "textures/gui/inventory.png");
    private static final Identifier GUARD_FOLLOWING_ICON = new Identifier(GuardVillagers.MODID, "textures/gui/following_icons.png");
    private static final Identifier GUARD_NOT_FOLLOWING_ICON = new Identifier(GuardVillagers.MODID, "textures/gui/not_following_icons.png");
    private static final Identifier PATROL_ICON = new Identifier(GuardVillagers.MODID, "textures/gui/patrollingui.png");
    private static final Identifier NOT_PATROLLING_ICON = new Identifier(GuardVillagers.MODID, "textures/gui/notpatrollingui.png");

    private static final Identifier ICONS = new Identifier("textures/gui/icons.png");
    private final PlayerEntity player;
    private final GuardEntity guardEntity;
    private float mousePosX;
    private float mousePosY;
    private boolean buttonPressed;

    public GuardVillagerScreen(GuardVillagerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, handler.guardEntity.getDisplayName());
        this.titleX = 80;
        this.playerInventoryTitleX = 100;
        this.player = inventory.player;
        guardEntity = handler.guardEntity;
    }

    @Override
    protected void init() {
        super.init();
        if (!GuardVillagersConfig.followHero || player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            this.addDrawableChild(new GuardGuiButton(this.x + 100, this.height / 2 - 40, 20, 18, 0, 0, 19, GUARD_FOLLOWING_ICON, GUARD_NOT_FOLLOWING_ICON, true,
                    (button) -> {
                        ClientPlayNetworking.send(new GuardFollowPacket(guardEntity.getId()));
                    })
            );
        }
        if (!GuardVillagersConfig.setGuardPatrolHotv || player.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE)) {
            this.addDrawableChild(new GuardGuiButton(this.x + 120, this.height / 2 - 40, 20, 18, 0, 0, 19, PATROL_ICON, NOT_PATROLLING_ICON, false,
                    (button) -> {
                        buttonPressed = !buttonPressed;
                        ClientPlayNetworking.send(new GuardPatrolPacket(guardEntity.getId(), buttonPressed));
                    })
            );
        }
    }

    @Override
    protected void drawBackground(DrawContext ctx, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        ctx.drawTexture(GUARD_GUI_TEXTURES, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        
        // Define the rendering area for the entity
        int entityX = i + 51;
        int entityY = j + 75;
        int entityAreaWidth = 60;  // Width of the rendering area
        int entityAreaHeight = 80; // Height of the rendering area
        
        InventoryScreen.drawEntity(ctx, 
            entityX - entityAreaWidth / 2, entityY - entityAreaHeight, // x1, y1 = top-left
            entityX + entityAreaWidth / 2, entityY, // x2, y2 = bottom-right
            30, // size
            0.0F, // f = vertical offset
            (float) entityX - this.mousePosX,
            (float) (entityY - 50) - this.mousePosY,
            this.guardEntity);
    }

    @Override
    protected void drawForeground(DrawContext ctx, int x, int y) {
        super.drawForeground(ctx, x, y);
        int health = MathHelper.ceil(guardEntity.getHealth());
        int armor = guardEntity.getArmor();
        int statusU = guardEntity.hasStatusEffect(StatusEffects.POISON) ? 4 : 0;


        // Use the proper sprite identifiers for hearts and armor
        Identifier heartContainer = new Identifier("hud/heart/container");
        Identifier heartFull = new Identifier("hud/heart/full");
        Identifier heartHalf = new Identifier("hud/heart/half");
        Identifier armorEmpty = new Identifier("hud/armor_empty");
        Identifier armorFull = new Identifier("hud/armor_full");
        Identifier armorHalf = new Identifier("hud/armor_half");

        // Render hearts
        for (int i = 0; i < 10; i++) {
            ctx.drawGuiTexture(heartContainer, (i * 8) + 80, 20, 9, 9);
        }
        for (int i = 0; i < health / 2; i++) {
            if (health % 2 != 0 && health / 2 == i) {
                ctx.drawGuiTexture(heartHalf, (i * 8) + 80, 20, 9, 9);
            } else {
                ctx.drawGuiTexture(heartFull, (i * 8) + 80, 20, 9, 9);
            }
        }

        // Render armor
        for (int i = 0; i < 10; i++) {
            ctx.drawGuiTexture(armorEmpty, (i * 8) + 80, 30, 9, 9);
        }
        for (int i = 0; i < armor / 2; i++) {
            if (armor % 2 != 0 && armor / 2 == i) {
                ctx.drawGuiTexture(armorHalf, (i * 8) + 80, 30, 9, 9);
            } else {
                ctx.drawGuiTexture(armorFull, (i * 8) + 80, 30, 9, 9);
            }
        }



//        //Health
//        for (int i = 0; i < 10; i++) {
//            ctx.drawTexture(ICONS, (i * 8) + 80, 20, 16, 0, 9, 9);
//        }
//        for (int i = 0; i < health / 2; i++) {
//            if (health % 2 != 0 && health / 2 == i + 1) {
//                ctx.drawTexture(ICONS, (i * 8) + 80, 20, 16 + 9 * (4 + statusU), 0, 9, 9);
//                ctx.drawTexture(ICONS, ((i + 1) * 8) + 80, 20, 16 + 9 * (5 + statusU), 0, 9, 9);
//            } else {
//                ctx.drawTexture(ICONS, (i * 8) + 80, 20, 16 + 9 * (4 + statusU), 0, 9, 9);
//            }
//        }
//        //Armor
//        for (int i = 0; i < 10; i++) {
//            ctx.drawTexture(ICONS, (i * 8) + 80, 30, 16, 9, 9, 9);
//        }
//        for (int i = 0; i < armor / 2; i++) {
//            if (armor % 2 != 0 && armor / 2 == i + 1) {
//                ctx.drawTexture(ICONS, (i * 8) + 80, 30, 16 + 9 * 2, 9, 9, 9);
//                ctx.drawTexture(ICONS, ((i + 1) * 8) + 80, 30, 16 + 9, 9, 9, 9);
//            } else {
//                ctx.drawTexture(ICONS, (i * 8) + 80, 30, 16 + 9 * 2, 9, 9, 9);
//            }
//        }

    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ctx, mouseX, mouseY, partialTicks);
        this.mousePosX = (float) mouseX;
        this.mousePosY = (float) mouseY;
        super.render(ctx, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(ctx, mouseX, mouseY);
    }


    class GuardGuiButton extends ButtonWidget {
        private final Identifier texture;
        private final Identifier newTexture;
        private final boolean isFollowButton;
        private final int u;
        private final int v;
        private final int hoveredVOffset;
        private final int textureWidth;
        private final int textureHeight;

        public GuardGuiButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn, int yDiffTextIn, Identifier resourceLocationIn, Identifier newTexture, boolean isFollowButton, PressAction pressAction) {
            super(xIn, yIn, widthIn, heightIn, Text.empty(), pressAction, DEFAULT_NARRATION_SUPPLIER);
            this.texture = resourceLocationIn;
            this.newTexture = newTexture;
            this.isFollowButton = isFollowButton;
            this.u = xTexStartIn;
            this.v = yTexStartIn;
            this.hoveredVOffset = yDiffTextIn;
            this.textureWidth = 256;
            this.textureHeight = 256;
        }

        public boolean requirementsForTexture() {
            boolean following = guardEntity.isFollowing();
            boolean patrol = guardEntity.isPatrolling();
            return this.isFollowButton ? following : patrol;
        }

        @Override
        public void renderButton(DrawContext ctx, int mouseX, int mouseY, float partialTicks) {
            Identifier icon = this.requirementsForTexture() ? texture : newTexture;
            int vOffset = this.v;
            if (this.isHovered()) {
                vOffset += this.hoveredVOffset;
            }

            RenderSystem.enableDepthTest();
            ctx.drawTexture(icon, this.getX(), this.getY(), this.u, vOffset, this.width, this.height, this.textureWidth, this.textureHeight);
        }
    }

}
