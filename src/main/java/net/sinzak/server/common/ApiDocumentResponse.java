package net.sinzak.server.common;


import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "API 호출 성공",response = ApiResponseSuccess.class),
        @ApiResponse(code = 404, message = "파일 찾지 못 함",response = ApiResponseFail.class)
})
public @interface ApiDocumentResponse {
}
