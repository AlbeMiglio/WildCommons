package net.cubespace.yamler;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.plugin.Plugin;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class YamlerConfig extends MapConfigMapper implements IConfig {
	
	public YamlerConfig() {
		 
	}

    public YamlerConfig(Plugin plugin, String filename, String ... header) {
        configFile = new File(plugin.getDataFolder(), filename);
        configHeader = header;
    }

    @Override
    public void save() throws YamlerConfigurationException {
        if (configFile == null) {
            throw new IllegalArgumentException("Saving a config without given File");
        }

        if (root == null) {
            root = new ConfigSection();
        }

        clearComments();

        internalSave(getClass());
        saveToYaml();
    }

    private void internalSave(Class<?> clazz) throws YamlerConfigurationException {
        if (!clazz.getSuperclass().equals(YamlerConfig.class)) {
            internalSave(clazz.getSuperclass());
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (doSkip(field)) continue;

            String path = field.getName().replace("_", ".");

            ArrayList<String> comments = new ArrayList<>();
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation instanceof Comment) {
                    Comment comment = (Comment) annotation;
                    comments.add(comment.value());

                }

                if (annotation instanceof Comments) {
                    Comments comment = (Comments) annotation;
                    comments.addAll(Arrays.asList(comment.value()));
                }
            }

            if (field.isAnnotationPresent(Path.class)) {
                Path path1 = field.getAnnotation(Path.class);
                path = path1.value();
            }

            if (comments.size() > 0) {
                for (String comment : comments) {
                    addComment(path, comment);
                }
            }

            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }

            try {
                converter.toConfig(this, field, root, path);
                converter.fromConfig(this, field, root, path);
            } catch (Exception e) {
                if (!skipFailedObjects) {
                    throw new YamlerConfigurationException("Could not save the Field", e);
                }
            }
        }
    }

    @Override
    public void save(File file) throws YamlerConfigurationException {
        if (file == null) {
            throw new IllegalArgumentException("File argument can not be null");
        }

        configFile = file;
        save();
    }

    @Override
    public void init() throws YamlerConfigurationException {
        if (!configFile.exists()) {
            if (configFile.getParentFile() != null)
                configFile.getParentFile().mkdirs();

            try {
                configFile.createNewFile();
                save();
            } catch (IOException e) {
                throw new YamlerConfigurationException("Could not create new empty Config", e);
            }
        } else {
            load();
        }
    }

    @Override
    public void init(File file) throws YamlerConfigurationException {
        if (file == null) {
            throw new IllegalArgumentException("File argument can not be null");
        }

        configFile = file;
        init();
    }

    @Override
    public void reload() throws YamlerConfigurationException {
        loadFromYaml();
        internalLoad(getClass());
    }

    @Override
    public void load() throws YamlerConfigurationException {
        if (configFile == null) {
            throw new IllegalArgumentException("Loading a config without given File");
        }

        loadFromYaml();
        update(root);
        internalLoad(getClass());
    }

    private void internalLoad(Class<?> clazz) throws YamlerConfigurationException {
        if (!clazz.getSuperclass().equals(YamlerConfig.class)) {
            internalLoad(clazz.getSuperclass());
        }

        boolean save = false;
        for (Field field : clazz.getDeclaredFields()) {
            if (doSkip(field)) continue;

            String path = field.getName().replace("_", ".");

            if (field.isAnnotationPresent(Path.class)) {
                Path path1 = field.getAnnotation(Path.class);
                path = path1.value();
            }

            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
            }

            if (root.has(path)) {
                try {
                    converter.fromConfig(this, field, root, path);
                } catch (Exception e) {
                    throw new YamlerConfigurationException("Could not set field " + field.getName(), e);
                }
            } else {
                try {
                    converter.toConfig(this, field, root, path);
                    converter.fromConfig(this, field, root, path);

                    save = true;
                } catch (Exception e) {
                    if (!skipFailedObjects) {
                        throw new YamlerConfigurationException("Could not get field " + field.getName(), e);
                    }
                }
            }
        }

        if (save) {
            saveToYaml();
        }
    }

    @Override
    public void load(File file) throws YamlerConfigurationException {
        if (file == null) {
            throw new IllegalArgumentException("File argument can not be null");
        }

        configFile = file;
        load();
    }
}
