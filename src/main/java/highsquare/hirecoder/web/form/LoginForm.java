package highsquare.hirecoder.web.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {

    @NotNull(message = "아이디를 입력하세요.")
    @Size(min = 3, max = 50, message = "아이디의 길이는 3~50 사이입니다.")
    private String username;

    @NotNull(message = "비밀번호를 입력하세요.")
    @Size(min = 3, max = 100, message = "비밀번호의 길이는 3~100 사이입니다.")
    private String password;
}
