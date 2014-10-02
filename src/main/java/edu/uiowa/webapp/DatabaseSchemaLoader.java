package edu.uiowa.webapp;

import java.util.Properties;

public abstract interface DatabaseSchemaLoader {
    
    public abstract void run(String descriptor) throws Exception;
    
    public abstract void run(Properties props) throws Exception;
    
    public abstract Database getDatabase();
    
}