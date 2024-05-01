package controller;

import dto.AuthentificationDto;
import dto.UserDto;
import exception.SessionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.UserService;


@RestController
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthentificationDto userAutorization(@RequestBody() UserDto UserDto) {
        return userService.userLogin(UserDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader("auth-token") String authToken) {
        boolean flag = userService.logout(authToken);
        if (!flag) {
            throw new SessionException("Пользователь с таким логином не найден");
        }
        return ResponseEntity.ok().body(null);

    }
}
