package highsquare.hirecoder.web.api;

import highsquare.hirecoder.domain.service.ImageService;
import highsquare.hirecoder.dto.RegisterImageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegisterImageController {

    private final ImageService imageService;

    @PostMapping("/image/upload")
    public ResponseEntity<RegisterImageResponse> registerImage(
                        @RequestParam("image")MultipartFile image) {

        try {

            RegisterImageResponse imageResponse = imageService.saveImage(image);

            return new ResponseEntity<>(imageResponse, HttpStatus.FORBIDDEN);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
