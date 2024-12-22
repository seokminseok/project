package com.lsm.declaration.services;

import com.lsm.declaration.entities.BoardPostEntity;
import com.lsm.declaration.entities.ReportEntity;
import com.lsm.declaration.entities.UserEntity;
import com.lsm.declaration.reportrepository.BoardCommentRepository;
import com.lsm.declaration.reportrepository.BoardPostRepository;
import com.lsm.declaration.reportrepository.ReportRepository;
import com.lsm.declaration.reportrepository.UserRepository;
import com.lsm.declaration.results.CommonResult;
import com.lsm.declaration.results.Result;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReportService {
    private final BoardCommentRepository boardCommentRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final BoardPostRepository boardPostRepository;

    @Autowired
    public ReportService(BoardCommentRepository boardCommentRepository, ReportRepository reportRepository, UserRepository userRepository, BoardPostRepository boardPostRepository) {
        this.boardCommentRepository = boardCommentRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;

        this.boardPostRepository = boardPostRepository;
    }

    // 중복신고 / 본인 글 신고 방지
    @Transactional
    public Result EmailDuplicate(ReportEntity report) {
        Optional<ReportEntity> existingReport = reportRepository.findByUserEmailAndReportedUserEmail(
                report.getUserEmail(), report.getReportedUserEmail());
        if (existingReport.isPresent()) {
            throw new IllegalStateException("이미 신고한 사용자입니다.");
        }
        if (report.getUserEmail().equals(report.getReportedUserEmail())) {
            throw new IllegalStateException("본인의 글을 신고 할 수 없습니다");
        }

        System.out.println(report.getReportedUserEmail());
        report.setReportedAt(LocalDateTime.now());
        reportRepository.save(report);
        return CommonResult.SUCCESS;
    }

    //warning카운트
    @Transactional
    public void increaseWarningForReportedUser(String reportedUserEmail) {
        // 1. 사용자가 존재하는지 확인
        UserEntity user = userRepository.findById(reportedUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + reportedUserEmail));

        // 2. 경고 카운터 증가
        user.setWarning(user.getWarning() + 1);

        // 3. 저장
        userRepository.save(user);
    }

    //삭제 사용자 확인
    @Transactional
    public boolean checkIfSuspended() {
        boolean isSuspended = userRepository.existsByIsSuspended();

        if (isSuspended) {
            throw new IllegalStateException("이미 삭제된 사용자입니다.");
        }
        return isSuspended;
    }

//    @Transactional
//    public String findByIndex(Integer index) {
//        Optional<BoardPostEntity> post = boardPostRepository.findById(index);
//        return post.isPresent() ? post.get().getUserEmail() : null;  // 게시글이 있으면 이메일 반환, 없으면 null 반환
//    }


//    @Transactional
//    public Result submitReport(ReportEntity report, String reportedEmail) {
//        List<Integer> reportedPostIds = boardCommentRepository.findIndexesByReportedEmail(reportedEmail);
//
//        if (!reportedPostIds.isEmpty()) {
//            for (Integer index : reportedPostIds) {
//                report.setReportedPostId(index); // 각 신고받은 댓글의 index 값 설정
//                report.setReportedAt(LocalDateTime.now());
//                reportRepository.save(report);
//            }
//            return CommonResult.SUCCESS;
//        } else {
//            throw new IllegalStateException("해당 댓글을 찾을 수 없습니다.");
//        }
//    }
}
