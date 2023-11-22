package lk.example.jersey.app.config;

import jakarta.inject.Singleton;
import lk.example.jersey.app.service.UserService;
import lk.example.jersey.app.util.JwtTokenUtil;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class DependencyBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(JwtTokenUtil.class).to(JwtTokenUtil.class).in(Singleton.class);
        bind(UserService.class).to(UserService.class).in(Singleton.class);
    }

}
