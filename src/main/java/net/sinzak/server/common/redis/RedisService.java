package net.sinzak.server.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String,String> redisTemplate;

    private static final String REDIS_KEY_AUTO_COMPLETE ="autoComplete:";
    private static final String COMPLETE_WORD ="+";
    private static final Integer NUMBER_OF_WORD_TO_SHOW = 5;
    public void addWordToRedis(String newWord){
        String key = REDIS_KEY_AUTO_COMPLETE;
        for(int i=0;i<newWord.length();i++){
            String word = newWord.substring(i,i+1);
            redisTemplate.opsForZSet().incrementScore(key,word,1);
            key += word;
        }
        redisTemplate.opsForZSet().add(key,COMPLETE_WORD,0);
    }

    public List<String> getAutoCompleteWords(String prefix){
        List<String> wordsToShow = new ArrayList<>();
        findEndOfWord(prefix,wordsToShow);
        return wordsToShow;
    }
    public void findEndOfWord(String prefix,List<String> wordsToShow){
        List<String> temporaryWord = redisTemplate
                .opsForZSet()
                .reverseRangeWithScores(REDIS_KEY_AUTO_COMPLETE+prefix,0,NUMBER_OF_WORD_TO_SHOW)
                .stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .collect(Collectors.toList());
        for(int i=0;i<temporaryWord.size();i++){
            if(wordsToShow.size()>=NUMBER_OF_WORD_TO_SHOW) return;
            if(temporaryWord.get(i).equals(COMPLETE_WORD)) wordsToShow.add(prefix);
            if(!temporaryWord.get(i).equals(COMPLETE_WORD)) findEndOfWord(prefix+temporaryWord.get(i),wordsToShow);
        }
    }


}
