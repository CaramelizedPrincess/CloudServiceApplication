package service;

import static org.junit.jupiter.api.Assertions.*;

import dto.AuthentificationDto;
import dto.UserDto;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.junit.Test;

import java.util.Objects;



@Nested
@ContextConfiguration(classes = {UserService.class})
@WebAppConfiguration
    public class UserServiceTest {

    @MockBean
        private UserService userService;



        @Test
        public void UserLoginTest() {
            AuthentificationDto response = userService.userLogin(new UserDto("user", "password"));
            assertNotNull(response);
            assertNotNull(Objects.requireNonNull(response).getAuthToken());
            assertNotEquals(0, response.getAuthToken().length());
        }


        @Test
        public void logoutTest() {
            AuthentificationDto response = userService.userLogin(new UserDto("user", "password"));
            String authToken = Objects.requireNonNull(response).getAuthToken();

            boolean actual = userService.logout(authToken);
            boolean expected = true;
            assertEquals(expected, actual);
        }
    }