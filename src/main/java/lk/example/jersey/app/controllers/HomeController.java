package lk.example.jersey.app.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class HomeController {
    @GET
    public String index(){
        return "<h1>home<h1>";
    }
}
