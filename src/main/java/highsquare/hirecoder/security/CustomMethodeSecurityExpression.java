package highsquare.hirecoder.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

@Slf4j
public class CustomMethodeSecurityExpression extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Authentication authentication;

    public CustomMethodeSecurityExpression(Authentication authentication) {
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

    public boolean isMemberManager(Long studyId) {

        log.info("pathVariable 검증 : {}", studyId);

        return studyId != null &&
                authentication.getAuthorities().stream()
                .mapToLong((auth)->Long.parseLong(auth.getAuthority()))
                .anyMatch((id)->id == studyId);
    }
}
