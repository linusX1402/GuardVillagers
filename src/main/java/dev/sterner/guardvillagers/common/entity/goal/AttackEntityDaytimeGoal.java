package dev.sterner.guardvillagers.common.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.math.BlockPos;

public class AttackEntityDaytimeGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
    public AttackEntityDaytimeGoal(SpiderEntity spider, Class<T> classTarget) {
        super(spider, classTarget, true);
    }

    @Override
    public boolean canStart() {
        float f = this.getBrightnessAtEyes();
        return !(f >= 0.5F) && super.canStart();
    }

    public float getBrightnessAtEyes() {
        return this.mob.getWorld().isChunkLoaded(this.mob.getChunkPos().x, this.mob.getChunkPos().z)
                ? this.mob.getWorld().getBrightness(BlockPos.ofFloored(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()))
                : 0.0F;
    }
}