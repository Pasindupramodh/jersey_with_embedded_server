package lk.example.jersey.app.middleware;

import io.fusionauth.jwt.JWTExpiredException;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lk.example.jersey.app.model.UserDetails;
import lk.example.jersey.app.service.UserService;
import lk.example.jersey.app.util.JwtTokenUtil;

import java.io.IOException;
@Provider
@Priority(1)

public class JwtValidationFilter implements ContainerRequestFilter {

    @Inject
    private JwtTokenUtil tokenUtil;
    @Inject
    private UserService userService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if(path.equals("auth") || path.equals("refresh-token")){
            return;
        }
//        System.out.println(path);
        if(requestContext.getHeaders().getFirst("Authorization")==null){
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }else{
            String token = requestContext.getHeaders().getFirst("Authorization").split(" ")[1];
            try {
                UserDetails userDetails = userService.getUserByEmail(tokenUtil.getUsernameFromToken(token));
                if(!tokenUtil.validateToken(token,userDetails)){
                    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                }
            }catch (JWTExpiredException | NullPointerException jwtExpiredException){
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Token Expired").build());
            }catch (Exception ex){
                ex.printStackTrace();
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }
    }
}