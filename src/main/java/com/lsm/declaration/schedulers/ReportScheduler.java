package com.lsm.declaration.schedulers;

import com.lsm.declaration.entities.ReportEntity;
import com.lsm.declaration.entities.UserEntity;
import com.lsm.declaration.reportrepository.ReportRepository;
import com.lsm.declaration.reportrepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ReportScheduler {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    // 이미 처리된 이메일을 추적하는 HashSet
    private final Set<String> processedEmails = new HashSet<>();

    @Scheduled(cron = "0 */1 * * * *") // 1분마다 실행
    public void countUserWarning() {
        System.out.println("countUserWarning 실행함");

// '신고 처리 완료' 상태인 신고들 가져오기
        List<ReportEntity> reports = reportRepository.findByCurrentStatus("신고 처리 완료");

        // 신고 처리 로직
        for (ReportEntity report : reports) {
            String reportedUserEmail = report.getReportedUserEmail();

            // 이미 처리된 이메일인지 확인
            if (processedEmails.contains(reportedUserEmail)) {
                continue; // 중복 처리 방지
            }

            // 사용자 찾기
            UserEntity user = userRepository.findById(reportedUserEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + reportedUserEmail));

            // 경고 수 증가
            int currentWarning = user.getWarning();
            int newWarning = currentWarning + 1;

            // 경고 값 제한: 최대 3
            if (newWarning >= 3) {
                newWarning = 3;
                user.setSuspended(true); // 계정 정지
            }

            user.setWarning(newWarning);
            userRepository.save(user);

            // 처리된 이메일을 기록
            processedEmails.add(reportedUserEmail);
            System.out.println("경고 처리된 이메일: " + reportedUserEmail);
        }
    }
}
