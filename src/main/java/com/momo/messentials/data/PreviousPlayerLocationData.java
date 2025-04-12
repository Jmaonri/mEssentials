package com.momo.messentials.data;

public class PreviousPlayerLocationData {
    public int x, y, z;
    public String dimension;
    public float yaw,pitch;

    public PreviousPlayerLocationData(int x, int y, int z, String dimension, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}