package org.pistonworks.core.api.model.entity;

import org.pistonworks.core.api.model.Orientation;
import org.pistonworks.core.api.model.Position;

import java.util.UUID;

public interface Entity
{
    /**
     * Gets the unique identifier for this entity.
     *
     * @return the entity's UUID
     */
    UUID getUniqueId();

    /**
     * Gets the current position of the entity.
     *
     * @return the entity's position
     */
    Position getPosition();

    /**
     * Sets the position of the entity.
     *
     * @param position the new position
     */
    void setPosition(Position position);

    /**
     * Gets the current orientation of the entity.
     *
     * @return the entity's orientation
     */
    Orientation getOrientation();

    /**
     * Sets the orientation of the entity.
     *
     * @param orientation the new orientation
     */
    void setOrientation(Orientation orientation);

    /**
     * Gets the current health of the entity.
     *
     * @return the entity's health
     */
    double getHealth();

    /**
     * Sets the health of the entity.
     *
     * @param health the new health value
     */
    void setHealth(double health);

    /**
     * Gets the maximum health of the entity.
     *
     * @return the entity's maximum health
     */
    double getMaxHealth();

    /**
     * Sets the maximum health of the entity.
     *
     * @param maxHealth the new maximum health value
     */
    void setMaxHealth(double maxHealth);
}
