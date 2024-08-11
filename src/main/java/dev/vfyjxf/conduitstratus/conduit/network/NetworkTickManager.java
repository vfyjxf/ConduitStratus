package dev.vfyjxf.conduitstratus.conduit.network;

import net.neoforged.neoforge.event.tick.ServerTickEvent;

//TODO: Implement
public class NetworkTickManager {

    private long totalTicks;

    private void onServerTickStart(ServerTickEvent.Pre event) {
    }

    private void onServerTickEnd(ServerTickEvent.Post event) {
        totalTicks++;
    }

}
