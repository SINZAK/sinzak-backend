package net.sinzak.server.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ImageController {
    private final S3Service s3Service;

    @PostMapping("/upload")
    @ResponseBody
    public List<String> upload(@RequestPart List<MultipartFile> multipartFile) throws IOException {
        return s3Service.uploadImage(multipartFile);
    }

//    @GetMapping("/get")
//    @ResponseBody
//    public List<String> upload(@RequestPart List<MultipartFile> multipartFile) throws IOException {
//        return s3Service.getImage(multipartFile);
//    }
}
