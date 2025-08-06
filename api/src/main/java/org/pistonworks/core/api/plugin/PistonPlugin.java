package org.pistonworks.core.api.plugin;

import org.pistonworks.core.api.PistonCore;
import org.pistonworks.core.api.logging.Logger;

public abstract class PistonPlugin {

    private static boolean coreInitialized = false;

    public PistonPlugin() {
        // Only initialize Piston Core once, and do it lazily when services are accessed
        if (!coreInitialized) {
            initializeCore();
        }

        // Register this plugin instance
        PistonCore.getPluginDiscoveryService().discoverPlugin(this);

        // Call the plugin's enable method
        onEnable();
    }

    private static synchronized void initializeCore() {
        if (!coreInitialized) {
            // Platform detection and initialization happens in PistonCore.getServices()
            coreInitialized = true;
        }
    }

    /**
     * Called when the plugin is enabled.
     */
    public abstract void onEnable();

    /**
     * Called when the plugin is disabled.
     */
    public void onDisable() {
        // Default implementation is empty
    }

    /**
     * Gets the logger for this plugin.
     *
     * @return the logger
     */
    public Logger getLogger() {
        return PistonCore.getLoggingService().getLogger(this.getClass());
    }
}
