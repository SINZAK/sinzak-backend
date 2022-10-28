package net.sinzak.server.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.dto.WorkPost;
import net.sinzak.server.service.WorkService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkController {

    private final WorkService workService;

    @ApiOperation(value = "외주 모집 글 생성")
    @PostMapping("/works/build")
    public JSONObject makeWorkPost(@RequestBody WorkPost postDto) {
        return workService.makeWork(postDto);
    }
}
