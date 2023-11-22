package lk.example.jersey.app.service;

import lk.example.jersey.app.model.UserDetails;

public class UserService {
    public UserDetails getUserByEmail(String email){
        return new UserDetails("abc@gmail.com","1234");
    }
}
