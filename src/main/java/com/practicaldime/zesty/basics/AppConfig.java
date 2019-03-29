package com.practicaldime.zesty.basics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

    private static AppConfig instance;
    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);
    private Properties properties;

    private AppConfig() {
        initProperties();
    }

    public static AppConfig instance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                instance = new AppConfig();
            }
        }
        return instance;
    }
    
    private void initProperties() {
        File file = new File("app-config.properties");
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(file));
        } catch (IOException ex) {
            LOG.error("Problem loading app-config.properties", ex);
        }
        this.properties = props;
    }

    public Properties properties() {
        return this.properties;
    }
}
