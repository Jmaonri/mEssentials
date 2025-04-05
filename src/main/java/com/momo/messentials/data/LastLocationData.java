package com.momo.messentials.data;

public class LastLocationData {
    public int x, y, z;
    public String dimension;
    public float yaw,pitch;

    public LastLocationData(int x, int y, int z, String dimension, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}