package org.pistonworks.core.api.model;

import java.io.File;

/**
 * A platform-agnostic plugin implementation that uses metadata from piston-core.yml
 */
public class PistonPlugin implements Plugin
{
    private final PluginMetadata metadata;
    private final Class<?> mainClass;
    private final File dataFolder;
    private boolean enabled;
    private Object instance;

    public PistonPlugin(PluginMetadata metadata, Class<?> mainClass, File dataFolder)
    {
        this.metadata = metadata;
        this.mainClass = mainClass;
        this.dataFolder = dataFolder;
        this.enabled = false;
    }

    @Override
    public String getName()
    {
        return metadata.getName();
    }

    @Override
    public String getVersion()
    {
        return metadata.getVersion();
    }

    @Override
    public String getDescription()
    {
        return metadata.getDescription();
    }

    @Override
    public Class<?> getMainClass()
    {
        return mainClass;
    }

    @Override
    public File getDataFolder()
    {
        return dataFolder;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public String[] getAuthors()
    {
        return metadata.getAuthors() != null ?
            metadata.getAuthors().toArray(new String[0]) : new String[0];
    }

    public PluginMetadata getMetadata()
    {
        return metadata;
    }

    public Object getInstance()
    {
        return instance;
    }

    public void setInstance(Object instance)
    {
        this.instance = instance;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
