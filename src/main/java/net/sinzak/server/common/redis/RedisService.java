package net.sinzak.server.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Resource(name= "autoCompleteRedisTemplate")
    private final RedisTemplate<String,Object> redisTemplate;
    private static final String REDIS_AUTO_COMPLETE ="autoComplete";
    private static final Integer SUB_WORD_SCORE =0;
    private static final Integer NEW_SEARCH_WORD_SCORE =1;
    public void addWordToRedis(String newWord){
        SearchNode parentNode = SearchNode.builder()
                .children(null)
                .score(0)
                .value(newWord.substring(0,1))
                .build();
        SearchNode firstNode = parentNode;
        for(int i=1;i<newWord.length();i++){
            SearchNode childNode = SearchNode.builder()
                    .children(null)
                    .score(0)
                    .value(newWord.substring(i,i+1))
                    .build();
            parentNode.setChildren(childNode);
            parentNode = childNode;

        }
        redisTemplate.opsForValue().set(REDIS_AUTO_COMPLETE,firstNode);
//        for(int i=0;i<newWord.length();i++){
//            Double score = redisTemplate.opsForZSet().score(REDIS_AUTO_COMPLETE,newWord.substring(i,i+1));
//            if(i==newWord.length()-1){
//                if(score==null||score==0){
//                    redisTemplate.opsForZSet().add(REDIS_AUTO_COMPLETE,newWord.substring(i,i+1),NEW_SEARCH_WORD_SCORE);
//                    break;
//                }
//                redisTemplate.opsForZSet().add(REDIS_AUTO_COMPLETE,newWord.substring(i,i+1),score+1);
//                break;
//            }
//            if(score==null||score==0){
//                redisTemplate.opsForZSet().add(REDIS_AUTO_COMPLETE,newWord.substring(i,i+1),SUB_WORD_SCORE);
//            }
//        }
    }

//    public List<String> getAutoCompleteWords(String prefix){
//        Set<String> allWords = redisTemplate
//                .opsForZSet()
//                .rangeByLex()
//                .
//
//    }


}
