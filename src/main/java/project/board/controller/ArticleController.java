package project.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.board.domain.type.FormStatus;
import project.board.domain.type.SearchType;
import project.board.dto.UserAccountDto;
import project.board.dto.request.ArticleRequest;
import project.board.dto.response.ArticleResponse;
import project.board.dto.response.ArticleWithCommentsResponse;
import project.board.service.ArticleService;
import project.board.service.PaginationService;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

    @GetMapping
    public String articles(
        @RequestParam(required = false) SearchType searchType,
        @RequestParam(required = false) String searchValue,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticles(searchType, searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumber(pageable.getPageNumber(), articles.getTotalPages());

        map.addAttribute("articles", articles);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchTypes", SearchType.values());

        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(@PathVariable Long articleId, ModelMap map) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentsResponses());
        return "articles/detail";
    }

    @GetMapping("/search-hashtag")
    public String searchHashtag(
        @RequestParam(required = false) SearchType searchType,
        @RequestParam(required = false) String searchValue,
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
        ModelMap map
    ) {
        Page<ArticleResponse> articles = articleService.searchArticlesViaHashtag(searchValue, pageable).map(ArticleResponse::from);
        List<Integer> barNumbers = paginationService.getPaginationBarNumber(pageable.getPageNumber(), articles.getTotalPages());
        List<String> hashtags = articleService.getHashtags();

        map.addAttribute("articles", articles);
        map.addAttribute("hashtags", hashtags);
        map.addAttribute("paginationBarNumbers", barNumbers);
        map.addAttribute("searchType", SearchType.HASHTAG);

        return "articles/search-hashtag";
    }

    @GetMapping("/form")
    public String articleForm(ModelMap map) {
        map.addAttribute("formStatus", FormStatus.CREATE);

        return "article/form";
    }

    @PostMapping("/form")
    public String postNewArticle(ArticleRequest articleRequest) {
        // TODO : 인증정보를 넣어줘야한다.
        articleService.saveArticle(Objects.requireNonNull(articleRequest.toDto(UserAccountDto.of(
            "uno", "asdf1234", "uno@mail.com", "Uno", "memo",
            null, null, null, null
        ))));

        return "redirect:/articles";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(@PathVariable Long articleId, ArticleRequest articleRequest) {
        // TODO : 인증 정보를 보내줘야한다.
        articleService.updateArticle(articleId, Objects.requireNonNull(articleRequest.toDto(UserAccountDto.of(
            "uno", "asdf1234", "uno@mail.com", "Uno", "memo",
            null, null, null, null
        ))));

        return "redirect:/articles/" + articleId;
    }

}

