package com.lsm.declaration.services;

import com.lsm.declaration.detail.CustomUserDetails;
import com.lsm.declaration.entities.CommentEntity;
import com.lsm.declaration.entities.UserEntity;
import com.lsm.declaration.mappers.CommentMapper;
import com.lsm.declaration.results.article.ArticleResult;
import com.lsm.declaration.results.comment.DeleteCommentResult;
import com.lsm.declaration.results.comment.ModifyCommentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
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

    // 댓글 삭제 기능
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
        return this.commentMapper.updateComment(comment) > 0
                ? DeleteCommentResult.SUCCESS // 성공적으로 업데이트됨
                : DeleteCommentResult.FAILURE; // 업데이트 실패
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

    // 댓글 작성 메서드 예시
    public ArticleResult writeComment(CommentEntity comment, @AuthenticationPrincipal UserDetails userDetails) {
        if (comment == null || comment.getComment() == null || comment.getComment().isEmpty() || comment.getComment().length() > 1000) {
            return ArticleResult.FAILURE;
        }

        // 사용자 정보 가져오기
        String userEmail = userDetails.getUsername();
        String userNickname = String.valueOf(userDetails.getClass());

        if (userDetails instanceof CustomUserDetails) {
//            userNickname = ((CustomUserDetails) userDetails).getNickname();
            userNickname="이잉";
        }

//        if (userNickname == null) {
//            userNickname = "익명"; // 사용자 닉네임이 제공되지 않은 경우 기본값 설정
//        }
//        System.out.println(userNickname);
        System.out.println(userEmail);
        // 댓글 정보 설정
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdateAt(null);
        comment.setIsDeleted(null);
        comment.setUserEmail(userEmail);
        comment.setUserNickname(userNickname);

        return commentMapper.insertComment(comment) > 0 ? ArticleResult.SUCCESS : ArticleResult.FAILURE;
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
        CommentEntity replyComment = new CommentEntity();
        replyComment.setPostId(parentComment.getPostId());
        replyComment.setCommentId(parentCommentId);
        replyComment.setComment(content);
        replyComment.setCreatedAt(LocalDateTime.now());
        replyComment.setUpdateAt(null);
        replyComment.setIsDeleted(null);
        return this.commentMapper.insertComment(replyComment) > 0 ? ArticleResult.SUCCESS : ArticleResult.FAILURE;
    }


    public CommentEntity[] getRepliesByParentId(int parentCommentId) {
        if (parentCommentId < 1) {
            return new CommentEntity[0];
        }
        return this.commentMapper.selectRepliesByParentId(parentCommentId);
    }
}