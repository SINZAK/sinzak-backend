package net.sinzak.server.cert;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.UserUtils;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.UnivDto;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertService {
    private final UserUtils userUtils;
    private final UserRepository userRepository;
    private final CertRepository certRepository;
    private final S3Service s3Service;

    @Transactional
    public JSONObject certifyUniv(UnivDto dto){
        Optional<Cert> savedCert = certRepository.findCertByUnivEmail(dto.getUniv_email());
        Long certId;
        if(savedCert.isEmpty())
            certId = certRepository.save(new Cert(dto.getUniv_email(), dto.getUniv(), "temp", false)).getId();
        else
        {
            Cert cert = savedCert.get();
            if(!cert.isCeleb_verified())
                certId = cert.getId();
            else
                return PropertyUtil.responseMessage("이미 인증 처리된 이메일입니다.");
        }
        User user = userUtils.getCurrentUser();
        user.updateCertifiedUniv(dto.getUniv(),dto.getUniv_email());
        userRepository.save(user);
        return PropertyUtil.response(certId);
    }

    @Transactional
    public JSONObject uploadUnivCard(Long id, MultipartFile file){
        Cert cert = certRepository.findById(id).orElseThrow(InstanceNotFoundException::new);
        String url = s3Service.uploadImage(file);
        cert.updateImageUrl(url);
        return PropertyUtil.response(true);
    }


    @Transactional
    public JSONObject applyCertifiedAuthor(String link){
        User user = userUtils.getCurrentUser();
        if(!user.isCert_uni())
            return PropertyUtil.responseMessage("아직 대학 인증이 완료되지 않았습니다.");
        if(user.isCert_celeb())
            return PropertyUtil.responseMessage("이미 처리된 요청입니다.");
        user.setPortFolioUrl(link);
        userRepository.save(user);
        return PropertyUtil.response(true);
    }

    @Transactional
    public void updateCertifiedUniv(MailDto dto){
        User user = userUtils.getCurrentUser();
        user.updateCertifiedUniv(dto.getUnivName(), dto.getUniv_email());
    }

}
