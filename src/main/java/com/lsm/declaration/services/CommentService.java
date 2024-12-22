package com.lsm.declaration.services;

import com.lsm.declaration.detail.CustomUserDetails;
import com.lsm.declaration.entities.CommentEntity;
import com.lsm.declaration.mappers.CommentMapper;
import com.lsm.declaration.results.article.ArticleResult;
import com.lsm.declaration.results.comment.DeleteCommentResult;
import com.lsm.declaration.results.comment.ModifyCommentResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }


    /// ///////////////////////////////

    public ModifyCommentResult modifyComment(int index, String content) {
        if (index < 1 || content == null || content.isEmpty() || content.length() > 100) {
            return ModifyCommentResult.FAILURE;
        }
        CommentEntity comment = this.commentMapper.selectCommentByIndex(index);
        if (comment == null || comment.getIsDeleted() != null) {
            return ModifyCommentResult.FAILURE;
        }
        comment.setComment(content);
        comment.setUpdateAt(LocalDateTime.now());
        return this.commentMapper.updateComment(comment) > 0
                ? ModifyCommentResult.SUCCESS
                : ModifyCommentResult.FAILURE;
    }


    /// //////////////////////////////////////////////////////////////
    ///
    public DeleteCommentResult deleteComment(int index) {
        if (index < 1) {
            return DeleteCommentResult.FAILURE; // 유효하지 않은 index
        }
        CommentEntity comment = this.commentMapper.selectCommentByIndex(index);
        if (comment == null) {
            return DeleteCommentResult.FAILURE; // 댓글이 없음
        }
        if (comment.getIsDeleted() != null) {
            return DeleteCommentResult.FAILURE; // 이미 삭제된 댓글
        }
        comment.setIsDeleted(LocalDateTime.now()); // 삭제 시간 설정
        int updateCount = this.commentMapper.updateComment(comment);

        // 대댓글 가져와서 업데이트
        CommentEntity[] replies = this.commentMapper.selectRepliesByParentId(index);
        for (CommentEntity reply : replies) {
            reply.setIsDeleted(LocalDateTime.now());
            updateCount += this.commentMapper.updateComment(reply);
        }

        return updateCount > 0 ? DeleteCommentResult.SUCCESS : DeleteCommentResult.FAILURE;
    }

    // 댓글 불러오기 기능
    public CommentEntity[] getCommentsByPostId(int articleIndex) {
        if (articleIndex < 1) {
            return new CommentEntity[0];
        }
        CommentEntity[] commentEntities = this.commentMapper.selectCommentsByPostId(articleIndex);
        if (commentEntities == null || commentEntities.length == 0) {
            return new CommentEntity[0];
        }
        return commentEntities;
    }

    // 댓글 작성기능
    public ArticleResult writeComment(CommentEntity comment) {
        if (comment == null ||
                comment.getComment() == null || comment.getComment().isEmpty() || comment.getComment().length() > 1000) {
            System.out.println("Comment validation failed");
            return ArticleResult.FAILURE;
        }

        System.out.println("Starting authentication check...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;
        String userNickname = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                userEmail = userDetails.getEmail();
                userNickname = userDetails.getNickname();
            }
        }

        System.out.println("userEmail: " + userEmail);
        System.out.println("userNickname: " + userNickname);

        if (userEmail == null) {
            System.out.println("Authentication failed. Returning FAILURE.");
            return ArticleResult.FAILURE;
        }

        comment.setCreatedAt(LocalDateTime.now());
        comment.setUserEmail(userEmail);
        comment.setUserNickname(userNickname != null ? userNickname : "익명");

        int result = commentMapper.insertComment(comment);

        return result > 0 ? ArticleResult.SUCCESS : ArticleResult.FAILURE;
    }



    /// ////////////////////////////////////
    public ArticleResult saveReplyComment(int parentCommentId, String content) {
        if (parentCommentId < 1 || content == null || content.isEmpty() || content.length() > 100) {
            return ArticleResult.FAILURE;
        }
        CommentEntity parentComment = this.commentMapper.selectCommentByIndex(parentCommentId);
        if (parentComment == null || parentComment.getIsDeleted() != null) {
            return ArticleResult.FAILURE;
        }
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;
        String userNickname = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            //userDetails에서는 이메일 닉네임을 못가져와서 커스텀했음 근데 왜 못가져온거지
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                userEmail = userDetails.getEmail();       // 이메일 가져오기
                userNickname = userDetails.getNickname(); // 닉네임 가져오기
            }
        }
        System.out.println(userEmail);
        System.out.println(userNickname);

        if (userEmail == null) {
            return ArticleResult.FAILURE;
        }
        CommentEntity replyComment = new CommentEntity();
        replyComment.setPostId(parentComment.getPostId());
        replyComment.setCommentId(parentCommentId);
        replyComment.setComment(content);
        replyComment.setUserEmail(userEmail);
        replyComment.setUserNickname(userNickname != null ? userNickname : "익명");

        return this.commentMapper.insertComment(replyComment) > 0 ? ArticleResult.SUCCESS : ArticleResult.FAILURE;
    }

    public CommentEntity[] getRepliesByParentId(int parentCommentId) {
        if (parentCommentId < 1) {
            return new CommentEntity[0];
        }
        return this.commentMapper.selectRepliesByParentId(parentCommentId);
    }
}