package dev.vfyjxf.conduitstratus.conduit;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.Collection;
import java.util.EnumSet;

public class ConnectionState {

    private final EnumSet<Direction> conduitConnections = EnumSet.noneOf(Direction.class);
    private final EnumSet<Direction> traitConnections = EnumSet.noneOf(Direction.class);

    public boolean isEmpty() {
        return conduitConnections.isEmpty() && traitConnections.isEmpty();
    }

    public void clear() {
        conduitConnections.clear();
        traitConnections.clear();
    }

    public void addConnection(Direction dir) {
        conduitConnections.add(dir);
    }

    public void addTrait(Direction dir) {
        //todo:disconnect conduit connection when trait was added.
        if (conduitConnections.contains(dir)) {
            throw new IllegalStateException("Cannot set trait connection when conduit connection is present.");
        }
        traitConnections.add(dir);
    }

    public EnumSet<Direction> connectionSides() {
        return conduitConnections;
    }

    public EnumSet<Direction> traitSides() {
        return traitConnections;
    }

    public void setTraitConnections(Collection<Direction> traitConnections) {
        this.traitConnections.clear();
        this.traitConnections.addAll(traitConnections);
    }

    public boolean hasTrait() {
        return !traitConnections.isEmpty();
    }

    public boolean hasTrait(Direction dir) {
        return traitConnections.contains(dir);
    }

    public void setConnections(Collection<Direction> connections) {
        conduitConnections.clear();
        conduitConnections.addAll(connections);
    }

    public boolean hasConnections() {
        return !conduitConnections.isEmpty();
    }

    public boolean hasConnection(Direction dir) {
        return conduitConnections.contains(dir);
    }

    public boolean isStraight() {
        if (conduitConnections.size() != 2 || !traitConnections.isEmpty()) {
            return false;
        }
        var it = conduitConnections.iterator();
        Direction any = it.next();
        return it.next().getOpposite() == any;
    }

    public Direction getStraightDirection() {
        return conduitConnections.iterator().next();
    }

    public CompoundTag writeToTag(CompoundTag tag, boolean saveConduitConnections) {
        if (saveConduitConnections) {
            tag.putIntArray("conduitConnections", conduitConnections.stream().mapToInt(Direction::get3DDataValue).toArray());
        }
        tag.putIntArray("traitConnections", traitConnections.stream().mapToInt(Direction::get3DDataValue).toArray());
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        if (tag.contains("conduitConnections")) {
            conduitConnections.clear();
            for (int i : tag.getIntArray("conduitConnections")) {
                conduitConnections.add(Direction.from3DDataValue(i));
            }
        }
        traitConnections.clear();
        for (int i : tag.getIntArray("traitConnections")) {
            traitConnections.add(Direction.from3DDataValue(i));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionState that = (ConnectionState) o;
        return conduitConnections.equals(that.conduitConnections)
                && traitConnections.equals(that.traitConnections);
    }

    @Override
    public int hashCode() {
        int result = conduitConnections.hashCode();
        result = 31 * result + traitConnections.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ConduitConnections{" +
                "conduitConnections=" + conduitConnections +
                ", traitConnections=" + traitConnections +
                '}';
    }
}
