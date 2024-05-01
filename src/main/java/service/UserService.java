package service;

import dto.UserDto;
import dto.AuthentificationDto;
import entity.User;
import model.UserModel;
import repository.UserRepository;
import utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service

@Slf4j
public class UserService {
    private static ConcurrentMap<String, UserModel> sessions;
    private static UserRepository userRepository;
    private UserDto userDto;

    public UserService(UserRepository userRepository) {
        this.sessions = new ConcurrentHashMap<>();
        this.userRepository = userRepository;
    }

    public static AuthentificationDto userLogin(UserDto userDto) {AuthentificationDto response;

        Optional<User> userFromDataBase = userRepository.findUserByLoginAndPassword(userDto.getLogin(),
                userDto.getPassword());
        if (userFromDataBase.isPresent()) {
            UserModel userModel = new UserModel(CommonUtils.createID(), userFromDataBase.get().getId());
            sessions.put(userModel.getId(), userModel);
            response = new AuthentificationDto(userModel.getId());
            log.info("User " + userDto.getLogin() + " is authorized");
        } else {
            log.error("Authorization error");
            response = null;
        }
        return response;
    }
    public boolean logout(String authToken) {
        UserModel sessionResult = sessions.getOrDefault(authToken, null);
        boolean flag;
        if (sessionResult != null) {
            sessions.remove(sessionResult.getId(), sessionResult);
            flag = true;
            log.info("User " + authToken + " is logout");
        } else {
            log.warn("User is logout");
            flag = false;
        }
        return flag;

    }

    public UserModel getSession(String authToken) {
        return sessions.get(authToken);
    }
}
