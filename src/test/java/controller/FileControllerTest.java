package controller;

import dto.AuthentificationDto;
import dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import repository.FileRepository;
import service.FileService;
import service.UserService;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.test.web.servlet.MockMvc;

@Nested
@ContextConfiguration(classes = {FileController.class})
public class FileControllerTest {
    @MockBean
    private MockMvc mvc;
    @MockBean
    private FileRepository fileRepository;
    @MockBean
    private FileService fileService;
    @MockBean
    private UserService userService;

    private static final String header = "auth-token";
    private static final String query = "filename";



        @SneakyThrows
        public String getAuthToken() {
            MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(new UserDto("user", "password")));

            MvcResult result = mvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andReturn();

            String actualJson = result.getResponse().getContentAsString();
            ObjectMapper mapper = new ObjectMapper();
            AuthentificationDto response = mapper.readValue(actualJson, AuthentificationDto.class);
            return response.getAuthToken();
        }


    @SneakyThrows
    @Test
    public void deleteFileTest() {
        String fileName = "text.txt";
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                fileName,
                MediaType.TEXT_PLAIN_VALUE,
                "Here's the text".getBytes()
        );
        String authToken = getAuthToken();
        Long userId = userService.getSession(authToken).getUserID();
        if (fileRepository.findFileByUserIdAndFileName(userId, fileName).isEmpty()) {
            String contentType = file.getContentType();
            byte[]bytes = file.getBytes();
            long sizeFile = file.getSize();
            fileService.addFile(authToken, fileName, bytes,contentType,sizeFile);
        }
        var request = MockMvcRequestBuilders.delete("/file")
                .header(header, authToken)
                .queryParam(query, fileName);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
        assertTrue(fileRepository.findFileByUserIdAndFileName(userId, fileName).isEmpty());
    }


    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}