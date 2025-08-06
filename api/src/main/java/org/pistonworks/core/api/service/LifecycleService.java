package org.pistonworks.core.api.service;

import org.pistonworks.core.api.plugin.PistonPlugin;

public interface LifecycleService
{
    void onEnable(PistonPlugin plugin);

    void onDisable(PistonPlugin plugin);
}
