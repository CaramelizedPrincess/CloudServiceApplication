package service;

import dto.AuthentificationDto;
import dto.UserDto;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;
import repository.FileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@Nested
@ContextConfiguration(classes = {FileService.class})
@WebAppConfiguration
public class FileServiceTest {
    @MockBean
    private final String fileName = "file.txt";
    @MockBean
    private FileRepository fileRepositoryTest;
    @MockBean
    private service.FileService fileService;
    @MockBean
    private FileRepository fileRepository;
    @MockBean
    private UserService userService;

    @SneakyThrows
    public MultipartFile multipartFileGet(String fileNameTest) {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        URL resource = getClass().getClassLoader().getResource(fileNameTest);

        URLConnection urlConnection = Objects.requireNonNull(resource).openConnection();
        byte[] content = ((InputStream) urlConnection.getContent()).readAllBytes();
        String contentMimeType = urlConnection.getContentType();

        when(multipartFile.getContentType()).thenReturn(contentMimeType);
        when(multipartFile.getBytes()).thenReturn(content);
        when(multipartFile.getSize()).thenReturn((long) content.length);

        return multipartFile;
    }

    @Test
    public void deleteFileTest() throws IOException {
        AuthentificationDto response = UserService.userLogin(new UserDto("user", "password"));
        String authToken = Objects.requireNonNull(response).getAuthToken();
        String fileNameTest2 = "deletedFile.txt";
        MultipartFile multipartFile = multipartFileGet(fileNameTest2);
        String contentType = multipartFile.getContentType();
        byte[] bytes = multipartFile.getBytes();
        long sizeFile = multipartFile.getSize();
        fileService.addFile(authToken, fileNameTest2, bytes, contentType, sizeFile);
        String actual = fileService.deleteFile(authToken, fileNameTest2);
        String expected = "File " + fileNameTest2 + " is deleted";
        assertEquals(expected, actual);
    }

    @SneakyThrows
    @Test
    public void addFileTest() {
        AuthentificationDto response = UserService.userLogin(new UserDto("user", "password"));
        String authToken = Objects.requireNonNull(response).getAuthToken();
        MultipartFile multipartFile = multipartFileGet(fileName);
        Long userId = userService.getSession(authToken).getUserID();
        if (fileRepository.findFileByUserIdAndFileName(userId, fileName).isPresent()) {
            fileService.deleteFile(authToken, fileName);
        }
        String contentType = multipartFile.getContentType();
        byte[] bytes = multipartFile.getBytes();
        long sizeFile = multipartFile.getSize();
        boolean result = fileService.addFile(authToken, fileName, bytes, contentType, sizeFile);
        assertTrue(result);
    }
}