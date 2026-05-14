package com.mysite.sbb.inquiry;

import com.mysite.sbb.notification.NotificationService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public InquiryResponse createInquiry(String email, String category, String content) {
        SiteUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        Inquiry inquiry = new Inquiry();
        inquiry.setUserEmail(email);
        inquiry.setUserName(user.getUsername());
        inquiry.setCategory(category);
        inquiry.setContent(content);
        inquiry.setStatus("답변대기");

        return InquiryResponse.from(inquiryRepository.save(inquiry));
    }

    public List<InquiryResponse> getMyInquiries(String email) {
        return inquiryRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream().map(InquiryResponse::from).collect(Collectors.toList());
    }

    public List<InquiryResponse> getAllInquiries() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(InquiryResponse::from).collect(Collectors.toList());
    }

    public void deleteInquiry(Long id, String email) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다."));
        if (!inquiry.getUserEmail().equals(email)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        inquiryRepository.delete(inquiry);
    }

    public void adminDeleteInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다."));
        inquiryRepository.delete(inquiry);
    }

    public InquiryResponse answerInquiry(Long id, String answer) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의를 찾을 수 없습니다."));

        inquiry.setAnswer(answer);
        inquiry.setStatus("답변완료");
        inquiry.setAnsweredAt(LocalDateTime.now());

        InquiryResponse result = InquiryResponse.from(inquiryRepository.save(inquiry));
        notificationService.send(inquiry.getUserEmail(), "문의 답변이 도착했어요", "'" + inquiry.getContent() + "'에 대한 답변을 확인해보세요.");
        return result;
    }
}
