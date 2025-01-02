package com.lsm.declaration.controllers;

import com.lsm.declaration.services.ArticleService;
import com.lsm.declaration.vos.ArticleVo;
import com.lsm.declaration.vos.PageVo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/board")
public class BoardController {

    private final ArticleService articleService;

    @Autowired
    public BoardController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "keyword", required = false) String keyword) {

        ModelAndView modelAndView = new ModelAndView();
        Pair<ArticleVo[], PageVo> articles;

        if (filter == null || filter.isEmpty() || keyword == null || keyword.isEmpty()) {
            articles = this.articleService.getArticlesByPaging(page);
        } else {
            articles = this.articleService.searchArticles(keyword, filter, page);
        }

        modelAndView.addObject("articles", articles.getLeft());
        modelAndView.addObject("pageVo", articles.getRight());
        modelAndView.addObject("filter", filter);
        modelAndView.addObject("keyword", keyword);
        modelAndView.setViewName("board/list");
        return modelAndView;
    }
}