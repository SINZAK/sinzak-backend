package net.sinzak.server.cert;

import lombok.AllArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CertService {

    private JavaMailSender emailSender;
    private CertRepository certRepository;
    private UserRepository userRepository;

    @Transactional
    public JSONObject sendMail(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sinzakofficial@gmail.com");
        message.setTo(mailDto.getAddress());
        message.setSubject("신작 : 대학인증 메일 코드를 확인해주세요");
        String code = String.valueOf((int)((Math.random()*10 +1) * 1000));
        message.setText("인증번호 : "+ code); //1000~9999
        emailSender.send(message);
        Optional<Cert> existCert = certRepository.findCertByUnivEmail(mailDto.getAddress());
        if(existCert.isPresent()){
            Cert cert = existCert.get();
            cert.updateKey(code);
        }
        else
            certRepository.save(new Cert(mailDto.getAddress(), code));
        return PropertyUtil.response(true);
    }

    @Transactional
    public JSONObject receiveMail(SessionUser User, MailDto mailDto) {
        Cert savedCert = certRepository.findCertByUnivEmail(mailDto.getAddress()).orElseThrow();

        if(savedCert.getCode().equals(mailDto.getCode())){
            User user = userRepository.findByEmail(User.getEmail()).orElseThrow(() -> new UserNotFoundException());
            savedCert.setVerified();
//            check(String univMail);
            user.updateUniv(mailDto.getAddress());  /** univ_email 등록 및  대학 인증 true 변경 **/

            return PropertyUtil.response(true);
        }
        return PropertyUtil.response(false);

    }


//    private boolean check(String mail){
//        UnivMail.changeMailToUniv(mail)
//
//    }
}
