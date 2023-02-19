package net.sinzak.server.cert;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
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
    private final UserRepository userRepository;
    private final CertRepository certRepository;
    private final S3Service s3Service;

    @Transactional
    public JSONObject certifyUniv(User User, UnivDto dto){
        User user = userRepository.findByEmail(User.getEmail()).orElseThrow(UserNotFoundException::new);
        Optional<Cert> savedCert = certRepository.findCertByUnivEmail(dto.getUniv_email());
        Long certId;
        if(savedCert.isEmpty())
            certId = certRepository.save(new Cert(dto.getUniv_email(), dto.getUniv(), "temp", false)).getId();
        else
        {
            Cert cert = savedCert.get();
            if(!cert.isVerified())
                certId = cert.getId();
            else
                return PropertyUtil.responseMessage("이미 인증 처리된 이메일입니다.");
        }
        user.updateCertifiedUniv(dto.getUniv(),dto.getUniv_email());
        return PropertyUtil.response(certId);
    }

    @Transactional
    public JSONObject uploadUnivCard(Long id, MultipartFile file){
        Cert cert = certRepository.findById(id).orElseThrow(InstanceNotFoundException::new);
        String url = s3Service.uploadImage(file);
        cert.updateImageUrl(url);
        return PropertyUtil.response(true);
    }




}
