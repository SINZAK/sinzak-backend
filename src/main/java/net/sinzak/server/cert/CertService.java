package net.sinzak.server.cert;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CertService {

    private final JavaMailSender emailSender;
    private final CertRepository certRepository;
    private final UserRepository userRepository;

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

            if(UnivMail.needCheck(mailDto.getUniv())){   /** 인증이 필요한 대학만 진행. **/
                boolean result = UnivMail.certUniv(mailDto.getUniv(),mailDto.getAddress());
                System.out.println("result = "+ result);
                if(!result)
                    return PropertyUtil.response(false); /** false가 와도 끊지말고 확인이 안되니 후에 마이페이지에서 학생증 인증하라고 고고 **/
            }
            user.updateUniv(mailDto.getUniv(), mailDto.getAddress());

            return PropertyUtil.response(true);
        }
        return PropertyUtil.response(false);

    }

}
