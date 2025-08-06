package org.pistonworks.core.api.model;

public record Orientation(double yaw, double pitch)
{
    @Override
    public String toString()
    {
        return "Orientation{" +
                "yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
