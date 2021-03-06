package com.github.wolfshotz.wyrmroost.entities.dragon.helpers.ai.goals;

import com.github.wolfshotz.wyrmroost.WRConfig;
import com.github.wolfshotz.wyrmroost.entities.dragon.AbstractDragonEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class MoveToHomeGoal extends Goal
{
    private int time;
    private final AbstractDragonEntity dragon;

    public MoveToHomeGoal(AbstractDragonEntity creatureIn)
    {
        this.dragon = creatureIn;
        setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean shouldExecute()
    {
        return !dragon.isWithinHomeDistanceCurrentPosition();
    }

    @Override
    public void startExecuting()
    {
        dragon.clearAI();
    }

    @Override
    public void resetTask()
    {
        this.time = 0;
    }

    @Override
    public void tick()
    {
        int sq = WRConfig.homeRadius * WRConfig.homeRadius;
        Vector3d home = Vector3d.copy(dragon.getHomePosition());
        final int TIME_UNTIL_TELEPORT = 600; // 30 seconds

        time++;
        if (dragon.getDistanceSq(home) > sq + 35 || time >= TIME_UNTIL_TELEPORT)
            dragon.trySafeTeleport(dragon.getHomePosition().up());
        else
        {
            Vector3d movePos;
            if (dragon.getNavigator().noPath() && (movePos = RandomPositionGenerator.findRandomTargetBlockTowards(dragon, WRConfig.homeRadius, 10, home)) != null)
                dragon.getNavigator().tryMoveToXYZ(movePos.x, movePos.y, movePos.y, 1.1);
        }
    }
}
