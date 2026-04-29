package se.voizter.felparkering.api.testsupport;

import org.springframework.test.web.servlet.ResultMatcher;

import com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers;

public final class OpenApiValidation {

    private static final String DEFAULT_SPEC_PATH = "docs/api-spec.yaml";

    private OpenApiValidation() {
    }

    public static ResultMatcher matchesOpenApiSpec() {
        return OpenApiValidationMatchers.openApi().isValid(DEFAULT_SPEC_PATH);
    }

    public static ResultMatcher matchesOpenApiSpec(String specPath) {
        return OpenApiValidationMatchers.openApi().isValid(specPath);
    }
}
