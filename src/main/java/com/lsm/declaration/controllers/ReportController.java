package com.lsm.declaration.controllers;

import com.lsm.declaration.detail.CustomUserDetails;
import com.lsm.declaration.entities.*;
import com.lsm.declaration.reportrepository.BoardCommentRepository;
import com.lsm.declaration.reportrepository.BoardPostRepository;
import com.lsm.declaration.reportrepository.ReportRepository;
import com.lsm.declaration.reportrepository.UserRepository;
import com.lsm.declaration.results.Result;
import com.lsm.declaration.services.ArticleService;
import com.lsm.declaration.services.ReportService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "/report")
public class ReportController {
    private final ArticleService articleService;
    private final ReportService reportService;


    @Autowired
    public ReportController(ReportService reportService, ArticleService articleService) {

        this.reportService = reportService;

        this.articleService = articleService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView CommentButton() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/main");
        return modelAndView;
    }

    @RequestMapping(value = "/page", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView CommentReport(@AuthenticationPrincipal UserDetails userDetails,
                                      @RequestParam(value = "index" ,required = false)int index) {
        ArticleEntity article = articleService.getArticleByIndex(index);
        ModelAndView modelAndView = new ModelAndView();
        if (userDetails instanceof CustomUserDetails user) {
            modelAndView.addObject("email", user.getEmail());
        }
        modelAndView.addObject("article", article);
        modelAndView.setViewName("report/report");
        return modelAndView;
    }

    @RequestMapping(value = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> createReport(ReportEntity report) {
        try {
            boolean suspended = reportService.checkIfSuspended();
            Result result = this.reportService.EmailDuplicate(report);
            if (suspended) {
                throw new IllegalStateException("계정이 정지된 사용자입니다.");
            }
            if ("신고 처리 완료".equals(report.getCurrentStatus())) {
                this.reportService.increaseWarningForReportedUser(report.getReportedUserEmail());
            }
            JSONObject response = new JSONObject();
            response.put("result", result.name().toLowerCase());
            return ResponseEntity.ok(response.toString());
        } catch (IllegalStateException e) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("result", "fail");
            errorResponse.put("message", e.getMessage()); // 상세 오류 메시지 포함
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.toString());
        }
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView ReportResult() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("report/result");
        return modelAndView;
    }
}
