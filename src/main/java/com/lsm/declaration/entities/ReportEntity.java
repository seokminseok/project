package com.lsm.declaration.entities;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;



@Entity(name = "fave")
@Table(schema = "fave", name = "reports")
@Getter
@Setter
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`index`")
    private int index;

    @Column(nullable = false, name = "user_email", length = 50)
    private String userEmail;

    @Column(nullable = false, name = "reported_user_email", length = 50)
    private String reportedUserEmail;

    @Column(nullable = true, name = "reported_post_id")
    private Integer reportedPostId;

    @Column(nullable = true, name = "reported_comment_id")
    private Integer reportedCommentId;

    @Column(name = "status", length = 255)
    private String status; // ENUM 대신 String 사용

    @Column(name = "current_status")
    private String currentStatus = ReportStatus.PROCESSING.getDescription(); // 기본값 설정

    @Column(name = "reason" ,length = 50)
    private String reason;

    @Column(name = "reason_detail")
    private String reasonDetail;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;
}
enum ReportStatus {
    PROCESSING("신고 처리 중"),
    COMPLETED("신고 처리 완료");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}