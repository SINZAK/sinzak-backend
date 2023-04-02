package net.sinzak.server.cert;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.cert.author.Celeb;
import net.sinzak.server.cert.author.CelebRepository;
import net.sinzak.server.cert.dto.CertDto;
import net.sinzak.server.cert.dto.MailDto;
import net.sinzak.server.cert.univ.UnivCard;
import net.sinzak.server.cert.univ.UnivCardRepository;
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
@Transactional
public class CertService {
    private final UserUtils userUtils;
    private final UserRepository userRepository;
    private final UnivCardRepository univCardRepository;
    private final CelebRepository celebRepository;
    private final S3Service s3Service;


    public JSONObject certifyUniv(UnivDto dto){
        Optional<UnivCard> existUnivCard = univCardRepository.findCertByUserId(userUtils.getCurrentUserId());
        if(existUnivCard.isPresent())
            return PropertyUtil.responseMessage("처리중이거나, 이미 인증된 요청입니다.");
        Long certId = univCardRepository.save(new UnivCard(dto.getUniv(), "temp", userUtils.getCurrentUserId())).getId();
        return PropertyUtil.response(certId);
    }


    public JSONObject uploadUnivCard(Long id, MultipartFile file){
        UnivCard univCard = univCardRepository.findById(id).orElseThrow(InstanceNotFoundException::new);
        userUtils.getCurrentUser().setUniv(univCard.getUnivName());
        String url = s3Service.uploadImage(file);
        univCard.updateImageUrl(url);
        univCard.setStatus(Status.PROCESS);
        return PropertyUtil.response(true);
    }

    public JSONObject completeUnivCard(Long id){
        UnivCard univCard = univCardRepository.findById(id).orElseThrow(InstanceNotFoundException::new);
        univCard.setStatus(Status.COMPLETE);
        User user = userRepository.findByIdNotDeleted(univCard.getUserId()).orElseThrow(UserNotFoundException::new);
        user.setCertifiedUniv();
        return PropertyUtil.response(true);
    }


    public JSONObject applyCertifiedAuthor(String link){
        User user = userUtils.getCurrentUser();
        Optional<Celeb> existCeleb = celebRepository.findCertByUserId(user.getId());
        if(!user.isCert_uni())
            return PropertyUtil.responseMessage("아직 대학 인증이 완료되지 않았습니다.");

        if(existCeleb.isPresent() || user.isCert_author())
            return PropertyUtil.responseMessage("처리중이거나, 이미 인증된 요청입니다.");
        Celeb celeb = new Celeb(link, user.getId());
        celeb.setStatus(Status.PROCESS);
        celebRepository.save(celeb);
        return PropertyUtil.response(true);
    }
    public JSONObject completeCeleb(Long id){
        Celeb celeb = celebRepository.findById(id).orElseThrow(InstanceNotFoundException::new);
        celeb.setStatus(Status.COMPLETE);
        User user = userRepository.findByIdNotDeleted(celeb.getUserId()).orElseThrow(UserNotFoundException::new);
        user.setCertifiedCeleb();
        return PropertyUtil.response(true);
    }

    public void updateCertifiedUniv(MailDto dto){
        User user = userUtils.getCurrentUser();
        user.setUniv(dto.getUnivName());
        user.setCertifiedUniv();
    }

    @Transactional(readOnly = true)
    public JSONObject getStatus(){
        User user = userUtils.getCurrentUser();
        CertDto certDto = CertDto.builder()
                .userId(user.getId())
                .cert_uni(user.isCert_uni())
                .cert_author(user.isCert_author())
                .build();
        univCardRepository.findCertByUserId(user.getId())
                .ifPresent(card -> certDto.setUnivcardStatus(card.getStatus()));
        celebRepository.findCertByUserId(user.getId())
                .ifPresent(celeb -> certDto.setCelebStatus(celeb.getStatus()));

        return PropertyUtil.response(certDto);
    }



}
