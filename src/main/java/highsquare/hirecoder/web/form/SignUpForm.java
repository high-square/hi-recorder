package highsquare.hirecoder.web.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpForm {

    @NotNull(message = "아이디가 비었습니다.")
    @Size(min=3, max=50, message = "아이디의 길이는 3~50 사이입니다.")
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "비밀번호가 비었습니다.")
    @Size(min=3, max=100, message = "비밀번호의 길이는 3~100 사이입니다.")
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "비밀번호를 다시 한번 입력해주세요")
    @Size(min=3, max=100, message = "비밀번호의 길이는 3~100 사이입니다.")
    private String rePassword;

    @NotNull(message = "이메일이 비었습니다.")
    @Size(min=3, max=50, message = "이메일의 길이는 3~50 사이입니다.")
    private String email;
}
