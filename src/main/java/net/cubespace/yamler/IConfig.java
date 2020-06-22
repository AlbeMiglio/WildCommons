package net.cubespace.yamler;

import java.io.File;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public interface IConfig {
    public void save() throws YamlerConfigurationException;
    public void save(File file) throws YamlerConfigurationException;

    public void init() throws YamlerConfigurationException;
    public void init(File file) throws YamlerConfigurationException;

    public void reload() throws YamlerConfigurationException;

    public void load() throws YamlerConfigurationException;
    public void load(File file) throws YamlerConfigurationException;
}
