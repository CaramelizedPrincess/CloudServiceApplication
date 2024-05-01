package controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import model.FileModel;
import service.FileService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    @SneakyThrows
    @PostMapping("/file")
    public ResponseEntity<String> addFile(@RequestHeader("auth-token") @NotNull String authToken,
                                          @RequestParam("filename") @NotNull String fileName,
                                          @RequestBody @NotNull MultipartFile file) {
        boolean flag = fileService.addFile(authToken, fileName, file.getBytes(), file.getContentType(), file.getSize());
        if (!flag) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body("File is saved");
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") @NotNull String authToken,
                                             @RequestParam("filename") @NotNull String fileName) {
        String response = fileService.deleteFile(authToken, fileName);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/list")
    public ResponseEntity<List<FileModel>> getAllFiles(@RequestHeader("auth-token") @NotNull String authToken,
                                                       @RequestParam("limit") @NotNull int limit) {
        List<FileModel> listFiles = fileService.getAllFiles(authToken, limit);
        return ResponseEntity.ok().body(listFiles);
    }
}
