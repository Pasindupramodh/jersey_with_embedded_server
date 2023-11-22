package lk.example.jersey.app.controllers;

import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.example.jersey.app.dto.AuthResponseDTO;
import lk.example.jersey.app.model.UserDetails;
import lk.example.jersey.app.service.UserService;
import lk.example.jersey.app.util.JwtTokenUtil;

import java.util.Date;

@Path("/")
public class AuthController {
    @Inject
    private JwtTokenUtil tokenUtil ;
    @Inject
    private UserService userService;
    @Path("/auth")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(@FormParam("email") String email , @FormParam("password") String password){

        if(email.equals("abc@gmail.com") && password.equals("1234")){
            UserDetails ud = userService.getUserByEmail(email);
            String token = tokenUtil.generateAccessToken(ud);
            String refreshToken = tokenUtil.generateRefreshToken(ud);
            Date expireDateFromToken = tokenUtil.getExpireDateFromToken(token);
            AuthResponseDTO authResponseDTO = new AuthResponseDTO();
            authResponseDTO.setRefreshToken(refreshToken);
            authResponseDTO.setAccessToken(token);
            authResponseDTO.setExpireIn(expireDateFromToken.toString());

            return Response.ok().entity(authResponseDTO).build();

        }else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid Emial or Password").build();
        }
    }
    @Path("/refresh-token")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken(@FormParam("refreshToken") String refreshToken){
        UserDetails userDetails = userService.getUserByEmail(tokenUtil.getUsernameFromToken(refreshToken));
        if(!tokenUtil.validateToken(refreshToken,userDetails)){
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid refresh token").build();
        }else{
            String accessToken = tokenUtil.generateAccessToken(userDetails);
            Date expireDateFromToken = tokenUtil.getExpireDateFromToken(accessToken);
            AuthResponseDTO authResponseDTO=new AuthResponseDTO();
            authResponseDTO.setExpireIn(expireDateFromToken.toString());
            authResponseDTO.setRefreshToken(refreshToken);
            authResponseDTO.setAccessToken(accessToken);
            return  Response.ok().entity(authResponseDTO).build();
        }

    }

}
