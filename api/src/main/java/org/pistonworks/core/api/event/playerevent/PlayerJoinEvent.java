package org.pistonworks.core.api.event.playerevent;

/**
 * Called when a player joins the server.
 */
public interface PlayerJoinEvent extends PlayerEvent
{

    /**
     * Gets the join message that will be displayed.
     *
     * @return the join message, or null if no message
     */
    String getJoinMessage();

    /**
     * Sets the join message to be displayed.
     *
     * @param message the join message, or null for no message
     */
    void setJoinMessage(String message);
}