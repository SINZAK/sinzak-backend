package net.sinzak.server.config;


import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {
//    @PostConstruct
//    public void init(){
//        try{
//            FileInputStream serviceAccount =
//                    new FileInputStream("src/main/resources/fireBaseAccountKey.json");
//
//            FirebaseOptions options = new FirebaseOptions.Builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
//
//            FirebaseApp.initializeApp(options);
//
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//    }

}
