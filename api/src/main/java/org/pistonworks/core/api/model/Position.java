package org.pistonworks.core.api.model;

/**
 * Represents a position in a 3D world with coordinates (x, y, z).
 * This class is immutable and provides methods for basic vector arithmetic.
 */
public record Position(World world, double x, double y, double z)
{
    /**
     * Add the specified offsets to the current position.
     *
     * @param dx the offset in the x direction
     * @param dy the offset in the y direction
     * @param dz the offset in the z direction
     * @return a new Position object with the updated coordinates
     */
    Position add(double dx, double dy, double dz)
    {
        return new Position(world, x + dx, y + dy, z + dz);
    }

    /**
     * Add the coordinates of another Position to the current position.
     *
     * @param position the Position to add
     * @return a new Position object with the updated coordinates
     */
    Position add(Position position)
    {
        return add(position.x, position.y, position.z);
    }

    /**
     * Subtract the specified offsets from the current position.
     *
     * @param dx the offset in the x direction
     * @param dy the offset in the y direction
     * @param dz the offset in the z direction
     * @return a new Position object with the updated coordinates
     */
    Position subtract(double dx, double dy, double dz)
    {
        return new Position(world, x - dx, y - dy, z - dz);
    }

    /**
     * Subtract the coordinates of another Position from the current position.
     *
     * @param position the Position to subtract
     * @return a new Position object with the updated coordinates
     */
    Position subtract(Position position)
    {
        return subtract(position.x, position.y, position.z);
    }

    /**
     * Multiply the current position by a scalar value.
     *
     * @param d the scalar value to multiply by
     * @return a new Position object with the coordinates multiplied by the scalar
     */
    Position multiply(double d)
    {
        return new Position(world, x * d, y * d, z * d);
    }

    /**
     * Divide the current position by a scalar value.
     *
     * @param d the scalar value to divide by
     * @return a new Position object with the coordinates divided by the scalar
     * @throws ArithmeticException if division by zero is attempted
     */
    Position divide(double d)
    {
        if (d == 0)
        {
            throw new ArithmeticException("Division by zero is not allowed.");
        }
        return new Position(world, x / d, y / d, z / d);
    }

    /**
     * Gets the distance to another position.
     *
     * @param position the Position to measure distance to
     * @return the distance to the specified position
     */
    public double distanceTo(Position position)
    {
        return Math.sqrt(Math.pow(x - position.x, 2) + Math.pow(y - position.y, 2) + Math.pow(z - position.z, 2));
    }

    @Override
    public String toString()
    {
        return "Position{" +
                "world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
