package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public abstract class BasicCapabilityTrait<T extends ConduitTrait<T>, CAP> extends BasicTrait<T, CapabilityConnection<CAP>> implements CapabilityConduitTrait<T, CAP> {

    protected final BlockCapability<? extends CAP, @Nullable Direction> token;

    protected BasicCapabilityTrait(
            ConduitTraitType<T> type,
            NetworkNode holder,
            Direction direction,
            BlockCapability<? extends CAP, @Nullable Direction> token
    ) {
        super(type, holder, direction);
        this.token = token;
    }

    @Override
    public BlockCapability<? extends CAP, @Nullable Direction> getToken() {
        return token;
    }

    @Override
    public boolean handle(HandleType handleType) {
        if (connection != null){
//            return handle(connection.getCapability());
        }
        return false;
    }

}
