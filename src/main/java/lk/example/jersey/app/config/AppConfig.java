package lk.example.jersey.app.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

public class AppConfig extends ResourceConfig {
    public AppConfig(){
        packages("lk.example.jersey.app.controllers");
        packages("lk.example.jersey.app.middleware");
        register(DependencyBinder.class);

    }
}
