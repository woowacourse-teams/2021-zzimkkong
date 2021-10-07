package com.woowacourse.s3proxy;

import io.restassured.specification.RequestSpecification;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

public final class DocumentUtils {
    private static RequestSpecification preConfiguredRequestSpecification;

    private DocumentUtils() {
    }

    public static RequestSpecification getRequestSpecification() {
        return preConfiguredRequestSpecification;
    }

    public static void setRequestSpecification(RequestSpecification preConfiguredRequestSpecification) {
        DocumentUtils.preConfiguredRequestSpecification = preConfiguredRequestSpecification;
    }

    public static OperationRequestPreprocessor getRequestPreprocessor() {
        return preprocessRequest(prettyPrint());
    }

    public static OperationResponsePreprocessor getResponsePreprocessor() {
        return preprocessResponse(prettyPrint());
    }
}
