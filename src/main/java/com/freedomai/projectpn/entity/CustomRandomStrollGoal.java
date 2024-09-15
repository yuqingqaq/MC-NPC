package com.freedomai.projectpn.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class CustomRandomStrollGoal extends Goal {
    protected final Mob mob;
    protected double x;
    protected double y;
    protected double z;
    protected final double speed;
    private final int interval = 120;
    private final boolean force = false;

    public CustomRandomStrollGoal(Mob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isVehicle() || !this.mob.getNavigation().isDone()) {
            return false;
        }

        if (!this.force) {
            if (this.mob.getRandom().nextInt(this.interval) != 0) {
                return false;
            }
        }

        Vec3 vec3 = getPos(this.mob, 10, 7);
        if (vec3 == null) {
            return false;
        } else {
            this.x = vec3.x;
            this.y = vec3.y;
            this.z = vec3.z;
            return true;
        }
    }

    public static Vec3 getPos(Mob mob, int horizontalRange, int verticalRange) {
        Random random = mob.getRandom();
        BlockPos pos = mob.blockPosition();

        // Attempt to find a random position within the ranges
        int dx = random.nextInt(horizontalRange * 2) - horizontalRange;
        int dy = random.nextInt(verticalRange * 2) - verticalRange;
        int dz = random.nextInt(horizontalRange * 2) - horizontalRange;

        BlockPos targetPos = pos.offset(dx, dy, dz);
        if (mob.level.getBlockState(targetPos).isAir()) { // Simple check, can be improved
            return Vec3.atCenterOf(targetPos);
        }
        return null;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.x, this.y, this.z, this.speed);
    }
}