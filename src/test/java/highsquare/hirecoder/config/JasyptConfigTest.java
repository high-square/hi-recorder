package highsquare.hirecoder.config;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {JasyptConfig.class})
class JasyptConfigTest {
    @Autowired
    private StringEncryptor jasyptStringEncryptor;

    @Test
    public void generatePassword() {
        String encrypt1 = jasyptStringEncryptor.encrypt("sa");
        System.out.println(encrypt1);
        System.out.println(jasyptStringEncryptor.decrypt(encrypt1));
        String encrypt2 = jasyptStringEncryptor.encrypt("jdbc:h2:tcp://localhost/~/test");
        System.out.println(encrypt2);
        System.out.println(jasyptStringEncryptor.decrypt(encrypt2));
    }
}