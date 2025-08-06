package org.pistonworks.core.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents plugin metadata loaded from piston-core.yml
 */
public class PluginMetadata
{
    private String name;
    private String version;
    private String description;
    private String mainClass;
    private List<String> authors;
    private List<String> dependencies;
    private String apiVersion;

    public PluginMetadata() {}

    public PluginMetadata(String name, String version, String description, String mainClass,
                         List<String> authors, List<String> dependencies, String apiVersion)
    {
        this.name = name;
        this.version = version;
        this.description = description;
        this.mainClass = mainClass;
        this.authors = authors;
        this.dependencies = dependencies;
        this.apiVersion = apiVersion;
    }

    public PluginMetadata(String name, String version, String description, String mainClass)
    {
        this.name = name;
        this.version = version;
        this.description = description;
        this.mainClass = mainClass;
        this.authors = new ArrayList<>();
        this.dependencies = new ArrayList<>();
        this.apiVersion = "0.3.0";
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getMainClass()
    {
        return mainClass;
    }

    public void setMainClass(String mainClass)
    {
        this.mainClass = mainClass;
    }

    public List<String> getAuthors()
    {
        return authors;
    }

    public void setAuthors(List<String> authors)
    {
        this.authors = authors;
    }

    public List<String> getDependencies()
    {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies)
    {
        this.dependencies = dependencies;
    }

    public String getApiVersion()
    {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion)
    {
        this.apiVersion = apiVersion;
    }
}
