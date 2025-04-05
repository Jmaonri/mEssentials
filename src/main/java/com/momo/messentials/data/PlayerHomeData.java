package com.momo.messentials.data;

public class PlayerHomeData {
    /**
     * The X-coordinate of the player's home location.
     */
    public int XCoordinate;

    /**
     * The Y-coordinate of the player's home location.
     */
    public int YCoordinate;

    /**
     * The Z-coordinate of the player's home location.
     */
    public int ZCoordinate;

    /**
     * The dimension where the player's home is located (e.g., Overworld, Nether).
     */
    public String Dimension;

    /**
     * The yaw (horizontal rotation) of the player at the home location.
     */
    public float PlayerYaw;

    /**
     * The pitch (vertical rotation) of the player at the home location.
     */
    public float PlayerPitch;

    /**
     * Constructs a new instance of `PlayerHomeData` with the specified coordinates, dimension, and player orientation.
     *
     * @param xCoordinate The X-coordinate of the home.
     * @param yCoordinate The Y-coordinate of the home.
     * @param zCoordinate The Z-coordinate of the home.
     * @param dimension The dimension of the home.
     * @param playerYaw The yaw (horizontal rotation) of the player at the home location.
     * @param playerPitch The pitch (vertical rotation) of the player at the home location.
     */
    public PlayerHomeData(int xCoordinate, int yCoordinate, int zCoordinate, String dimension, float playerYaw, float playerPitch) {
        XCoordinate = xCoordinate;
        YCoordinate = yCoordinate;
        ZCoordinate = zCoordinate;
        Dimension = dimension;
        PlayerYaw = playerYaw;
        PlayerPitch = playerPitch;
    }
}
