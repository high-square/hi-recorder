package highsquare.hirecoder.converter;

import highsquare.hirecoder.entity.Kind;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToKindConverter implements Converter<String, Kind> {
    @Override
    public Kind convert(String source) {
        log.info("convert source={}", source);

            return Kind.valueOf(source.toUpperCase());

    }
}
