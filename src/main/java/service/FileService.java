package service;

import entity.File;
import entity.User;
import exception.FileNotFoundException;
import exception.InputDataException;
import exception.SessionException;
import model.FileModel;
import model.UserModel;
import repository.FileRepository;
import repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service

@AllArgsConstructor
@Slf4j
public class FileService {

    private FileRepository fileRepository;
    private UserRepository userRepository;
    private UserService userService;


    public boolean addFile(String authToken, String fileName, byte[] bytes, String contentType, long sizeFile) {
        Long userId = checkUser(authToken);
        File addFile;
        boolean flag = true;
        if (fileRepository.findFileByUserIdAndFileName(userId, fileName).isPresent()) {
            flag = false;
            log.error("There's such file " + fileName);
        }
        User user = userRepository.getReferenceById(userId);
        addFile = File.builder()
                .fileName(fileName)
                .type(contentType)
                .fileContent(bytes)
                .size(sizeFile)
                .user(user)
                .build();
        fileRepository.save(addFile);
        log.info("User " + userId + "added " + fileName);
        return flag;

    }

    public String deleteFile(String authToken, String fileName) {
        Long userId = checkUser(authToken);
        File deleteFile = checkFile(userId, fileName);
        fileRepository.deleteById(deleteFile.getId());
        log.info("User " + userId + "deleted " + fileName);
        return fileName + " is deleted";
    }

    public List<FileModel> getAllFiles(String authToken, int limit) {
        Long userId = checkUser(authToken);
        if (limit < 0) {
            log.warn("Limit is incorrect");
            throw new InputDataException("Limit is incorrect");
        }
        List<File> allFiles = fileRepository.findFilesByUserId(userId);
        List<FileModel> listFiles = allFiles.stream()
                .map(file -> FileModel.builder()
                        .fileName(file.getFileName())
                        .size(file.getSize())
                        .build()).collect(Collectors.toList());
        log.info("There's a list of " + userId);
        return listFiles;
    }

    public Long checkUser(String authToken) {
        UserModel userModelResult = userService.getSession(authToken);
        if (userModelResult == null) {
            log.error("There's no such user");
            throw new SessionException("There's no such user");
        }
        return Objects.requireNonNull(userModelResult).getUserID();
    }

    public File checkFile(Long userId, String fileName) {
        var checkFile = fileRepository.findFileByUserIdAndFileName(userId, fileName);
        if (checkFile.isEmpty()) {
            log.error("There's no such file " + fileName);
            throw new FileNotFoundException("There's no such file " + fileName);
        }
        return checkFile.get();

    }

}

