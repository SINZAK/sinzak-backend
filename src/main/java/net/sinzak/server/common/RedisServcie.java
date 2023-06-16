package net.sinzak.server.common;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisServcie {

    @Resource(name="redisTemplate")
    private final RedisTemplate<String,String> redisTemplate;
    private static final String REDIS_AUTO_COMPLETE ="autoComplete";
    private static final Integer SUB_WORD_SCORE =0;
    private static final Integer NEW_SEARCH_WORD_SCORE =1;
    public void addWordToRedis(String newWord){
        for(int i=0;i<newWord.length();i++){
            Double score = redisTemplate.opsForZSet().score(REDIS_AUTO_COMPLETE,newWord.substring(i,i+1));
            if(i==newWord.length()-1){
                if(score==null||score==0){
                    redisTemplate.opsForZSet().add(REDIS_AUTO_COMPLETE,newWord.substring(i,i+1),NEW_SEARCH_WORD_SCORE);
                    break;
                }
                redisTemplate.opsForZSet().add(REDIS_AUTO_COMPLETE,newWord.substring(i,i+1),score+1);
                break;
            }
            if(score==null||score==0){
                redisTemplate.opsForZSet().add(REDIS_AUTO_COMPLETE,newWord.substring(i,i+1),SUB_WORD_SCORE);
            }
        }
    }

//    public List<String> getAutoCompleteWords(String prefix){
//        Set<String> allWords = redisTemplate
//                .opsForZSet()
//                .rangeByLex()
//                .
//
//    }


}
