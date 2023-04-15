package project.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import project.board.dto.CommentReplyDto;
import project.board.dto.CommentReplyRequest;
import project.board.dto.UserAccountDto;
import project.board.dto.request.ArticleCommentRequest;
import project.board.dto.security.UserPrincipal;
import project.board.service.ArticleCommentService;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(
        ArticleCommentRequest articleCommentRequest,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(principal.toDto()));

        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(
        @PathVariable Long commentId,
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        Long articleId
    ) {
        articleCommentService.deleteArticleComment(commentId, userPrincipal.username());

        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{commentId}/reply")
    public String postNewCommentReply(
        CommentReplyRequest commentReplyRequest,
        @PathVariable Long commentId,
        @AuthenticationPrincipal UserPrincipal principal,
        Long articleId
    ) {
        articleCommentService.saveArticleCommentRely(commentReplyRequest.toDto(commentId, principal.toDto()));
        return "redirect:/articles/" + articleId;
    }

}
