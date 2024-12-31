package com.lsm.declaration.services;

import com.lsm.declaration.detail.CustomUserDetails;
import com.lsm.declaration.entities.ArticleEntity;
import com.lsm.declaration.entities.ImageEntity;
import com.lsm.declaration.mappers.ArticleMapper;
import com.lsm.declaration.mappers.ImageMapper;
import com.lsm.declaration.results.article.ArticleResult;
import com.lsm.declaration.results.article.DeleteArticleResult;
import com.lsm.declaration.vos.ArticleVo;
import com.lsm.declaration.vos.PageVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ArticleService {
    private final ArticleMapper articleMapper;
    private final ImageMapper imageMapper;

    @Autowired
    public ArticleService(ArticleMapper articleMapper, ImageMapper imageMapper) {
        this.articleMapper = articleMapper;
        this.imageMapper = imageMapper;
    }

    /****************************************************************************/


    /******************************************************************************/
    // 삭제
    public DeleteArticleResult deleteArticle(int index) {
        if (index < 1) {
            return DeleteArticleResult.FAILURE;
        }

        ArticleEntity article = this.articleMapper.selectArticleByIndex(index);
        if (article == null) {
            return DeleteArticleResult.FAILURE;
        }

        if (article.getDeletedAt() != null) {
            return DeleteArticleResult.FAILURE;
        }

        article.setDeletedAt(LocalDateTime.now());
        return this.articleMapper.updateArticle(article) > 0
                ? DeleteArticleResult.SUCCESS
                : DeleteArticleResult.FAILURE;
    }



    public ImageEntity getImage(int index) {
        if (index < 1) {
            return null;
        }
        return this.imageMapper.selectImageByIndex(index);
    }

    public boolean modifyArticle(ArticleEntity article) {
        // 클라이언트가 준 데이터 유효성 검사
        if (article == null ||
                article.getIndex() < 1 ||
                article.getTitle() == null || article.getTitle().isEmpty() || article.getTitle().length() > 100 ||
                article.getContent() == null || article.getContent().isEmpty() || article.getContent().length() > 16_777_215) {
            return false;
        }

        ArticleEntity dbArticle = this.articleMapper.selectArticleByIndex(article.getIndex());
        // 데이터베이스에서 게시글 조회
        if (dbArticle == null || dbArticle.getDeletedAt() != null) {
            // 게시글이 존재하지 않거나 이미 삭제된 경우
            return false;
        }

        // 클라이언트가 전달한 데이터로 게시글 수정
        dbArticle.setTitle(article.getTitle());
        dbArticle.setContent(article.getContent());
        dbArticle.setUpdateAt(LocalDateTime.now()); // 수정 시간 갱신

        // 게시글 업데이트 결과 반환
        return this.articleMapper.updateArticle(dbArticle) > 0;
    }

//    public Pair<ArticleEntity[], PageVo> searchArticles(String keyword, String filter, int page) {
//        int totalCount = articleMapper.selectArticleCountBySearch(filter, keyword);
//        PageVo pageVo = new PageVo(page, totalCount);
//        ArticleEntity[] articles = articleMapper.selectArticleBySearch(filter, keyword, pageVo.countPerPage, pageVo.offsetCount);
//        return Pair.of(articles, pageVo);
//    }
//
//    public Pair<ArticleEntity[], PageVo> getArticlesByPaging(int page) {
//        int totalCount = this.articleMapper.getTotalArticlesCount();
//        PageVo pageVo = new PageVo(page, totalCount);
//        ArticleEntity[] articles = this.articleMapper.selectArticlesByPaging(pageVo.offsetCount, pageVo.countPerPage);
//
//        return Pair.of(articles, pageVo);
//    }

    public Pair<ArticleVo[], PageVo> searchArticles(String keyword, String filter, int page) {
        int totalCount = articleMapper.selectArticleCountBySearch(filter, keyword);
        PageVo pageVo = new PageVo(page, totalCount);

        // 댓글 수를 포함한 게시물 조회
        ArticleVo[] articles = articleMapper.selectArticleBySearch(filter, keyword, pageVo.countPerPage, pageVo.offsetCount);;
        return Pair.of(articles, pageVo);
    }

    public Pair<ArticleVo[], PageVo> getArticlesByPaging(int page) {
        int totalCount = this.articleMapper.getTotalArticlesCount();
        PageVo pageVo = new PageVo(page, totalCount);

        // 댓글 수를 포함한 페이징된 게시물 조회
        ArticleVo[] articles = this.articleMapper.selectArticlesByPaging(pageVo.offsetCount, pageVo.countPerPage);

        return Pair.of(articles, pageVo);
    }


    public boolean increaseArticleView(ArticleEntity article) {
        if (article == null) {
            return false;
        }
        article.setView(article.getView() + 1);
        return this.articleMapper.updateArticle(article) > 0;
    }

    public ArticleEntity getArticleByIndex(int index) {
        return articleMapper.selectArticleByIndex(index);
    }



    public boolean uploadImage(ImageEntity image) {
        if (image == null ||
                image.getData() == null ||
                image.getData().length == 0 ||
                image.getContentType() == null || image.getContentType().isEmpty() ||
                image.getName() == null || image.getName().isEmpty()) {
            return false;
        }
        image.setCreatedAt(LocalDateTime.now());
        return this.imageMapper.insertImage(image) > 0;
    }

    public ArticleResult write(ArticleEntity article) {
        if (article == null ||
                article.getTitle() == null || article.getTitle().isEmpty() || article.getTitle().length() > 100 ||
                article.getContent() == null || article.getContent().isEmpty()) {
            return ArticleResult.FAILURE;
        }
// 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;
        String userNickname = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                userEmail = userDetails.getEmail();       // 이메일 가져오기
                userNickname = userDetails.getNickname(); // 닉네임 가져오기
            }
        }
        System.out.println(userEmail);
        System.out.println(userNickname);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdateAt(null);
        article.setDeletedAt(null);
        article.setUserEmail(userEmail);
        article.setUserNickname(userNickname != null ? userNickname : "익명");


        return this.articleMapper.insertArticle(article) > 0
                ? ArticleResult.SUCCESS
                : ArticleResult.FAILURE;
    }
}
