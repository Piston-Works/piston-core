package org.pistonworks.core.api.service;

import org.pistonworks.core.api.model.Plugin;

public interface LifecycleService
{
    void onEnable(Plugin plugin);

    void onDisable(Plugin plugin);
}
