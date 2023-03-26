package highsquare.hirecoder.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import highsquare.hirecoder.dto.StudyAuthorities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

@Slf4j
public class CustomSecurityExpression extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Authentication authentication;

    private ObjectMapper objectMapper = new ObjectMapper();

    public CustomSecurityExpression(Authentication authentication) {
        super(authentication);
        this.authentication = authentication;
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    public boolean isMemberManager(Long studyId) throws JsonProcessingException {

        log.info("pathVariable 검증 : {}", studyId);

        /**
         * 들어온 studyId를 포함하고 있는지 확인작업
         * authentication에 있는 JSON으로 받은 authority를 studyAuthorities로 치환하여 작업 진행
         */
        if (studyId != null) {
            String authority = authentication.getAuthorities().stream().findFirst().get().getAuthority();
            StudyAuthorities studyAuthorities = objectMapper.readValue(authority, StudyAuthorities.class);
            log.debug("studyAuthorities.toString() {}",studyAuthorities.toString());
            log.debug("studyAuthorities.getManagers() {}",studyAuthorities.getManagers().toString());
            log.debug("studyAuthorities.getManagers().contains() {}",studyAuthorities.getManagers().contains(studyId));

            return studyAuthorities.getManagers().contains(studyId);
        }

        return false;
    }


}
