package de.tudbut.optiplot.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import tudbut.parsing.TCN;
import tudbut.parsing.TCNArray;

import java.util.List;
import java.util.stream.Collectors;

public class Area {

    TCNArray data;

    List<AABB> bounds;

    public Area(Claim forClaim) {
        data = forClaim.data.getArray("Area");
        bounds = data.stream().map(x -> (TCN) x).map(x -> new AABB(
                new BlockPos(x.getInteger("X1"), x.getInteger("Y1"), x.getInteger("Z1")),
                new BlockPos(x.getInteger("X2"), x.getInteger("Y2"), x.getInteger("Z2"))
        )).collect(Collectors.toList());
    }

    public List<AABB> getBounds() {
        return bounds;
    }

    public void addBound(AABB bound) {
        TCN tcn = new TCN();
        tcn.set("X1", bound.minX);
        tcn.set("Y1", bound.minY);
        tcn.set("Z1", bound.minZ);
        tcn.set("X2", bound.maxX);
        tcn.set("Y2", bound.maxY);
        tcn.set("Z2", bound.maxZ);
        data.add(tcn);
        bounds.add(bound);
    }
}
