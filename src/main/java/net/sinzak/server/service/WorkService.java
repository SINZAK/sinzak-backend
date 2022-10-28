package net.sinzak.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.dto.WorkPost;
import net.sinzak.server.repository.UserRepository;
import net.sinzak.server.repository.WorkRepository;
import org.json.simple.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkService {
    private final UserRepository userRepository;
    private final WorkRepository workRepository;

    @Transactional
    public JSONObject makeWork(WorkPost workPost){   // 좋아요
        return PropertyUtil.response(true);
    }


}

