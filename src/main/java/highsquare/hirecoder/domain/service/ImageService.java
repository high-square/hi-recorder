package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardImageRepository;
import highsquare.hirecoder.dto.RegisterImageResponse;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.BoardImage;
import highsquare.hirecoder.entity.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ResourceLoader resourceLoader;
    private final BoardImageRepository boardImageRepository;

    public RegisterImageResponse saveImage(MultipartFile image) throws IOException {

        UUID uuid = UUID.randomUUID();

        String originalFilename = image.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.indexOf('.'), originalFilename.length());

        String imageRoot = resourceLoader.getResource("classpath:")
                .getFile()
                .getParentFile()
                .getParentFile()
                .getParentFile().toString() + "/resources/main/static/images/";

        String imageUri = "/images/" + uuid + extName;

        image.transferTo(new File(imageRoot + uuid + extName));

        BoardImage boardImage = new BoardImage(uuid, new Image(uuid.toString(), extName, imageUri), null);

        boardImageRepository.save(boardImage);

        return new RegisterImageResponse(uuid, imageUri);
    }

    @Transactional
    public void connectBoardAndImage(Board board, List<String> imageIds) {

        for (String imageId : imageIds) {

            boardImageRepository.findById(imageId).ifPresent(
                    (boardImage) -> {
                        boardImage.updateBoard(board);
                    }
            );
        }
    }

}
