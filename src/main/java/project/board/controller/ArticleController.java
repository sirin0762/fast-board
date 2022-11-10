package project.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import project.board.common.S3Uploader;
import project.board.domain.type.FormStatus;
import project.board.domain.type.SearchType;
import project.board.dto.request.ArticleRequest;
import project.board.dto.response.ArticleResponse;
import project.board.dto.response.ArticleWithCommentsResponse;
import project.board.dto.response.PopularArticleResponse;
import project.board.dto.security.UserPrincipal;
import project.board.service.ArticleService;
import project.board.service.PaginationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final PaginationService paginationService;
    private final S3Uploader s3Uploader;

    public final String BOARD_DIRNAME = "board";

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
    public String article(
        @PathVariable Long articleId,
        ModelMap map,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        ArticleWithCommentsResponse article = ArticleWithCommentsResponse.from(articleService.getArticleWithComments(articleId));
        Set<PopularArticleResponse> popularArticles = articleService.getPopularArticles(article.hashtag());

        articleService.addViewCount(articleId, request, response);
        map.addAttribute("article", article);
        map.addAttribute("articleComments", article.articleCommentsResponses());
        map.addAttribute("popularArticles", popularArticles);

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

        return "articles/form";
    }

    @GetMapping("/{articleId}/form")
    public String updateArticleForm(@PathVariable Long articleId, ModelMap map) {
        ArticleResponse article = ArticleResponse.from(articleService.getArticle(articleId));

        map.addAttribute("article", article);
        map.addAttribute("formStatus", FormStatus.UPDATE);

        return "articles/form";
    }

    @PostMapping("/form")
    public String postNewArticle(
        ArticleRequest articleRequest,
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(name = "file", required = false) MultipartFile file
    ) throws IOException {
        Long articleId = articleService.saveArticle(articleRequest.toDto(principal.toDto()));

        if (!Objects.isNull(file) && Objects.requireNonNull(file.getContentType()).toLowerCase().startsWith("image")) {
            String imagePath = s3Uploader.upload(file, BOARD_DIRNAME);
            articleService.saveBoardImage(articleId, imagePath);
        }

        return "redirect:/articles";
    }

    @PostMapping("/{articleId}/form")
    public String updateArticle(
        @PathVariable Long articleId,
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(name = "file", required = false) MultipartFile file,
        ArticleRequest articleRequest
    ) throws IOException {
        articleService.updateArticle(articleId, articleRequest.toDto(principal.toDto()));

        if (!Objects.isNull(file) && Objects.requireNonNull(file.getContentType()).toLowerCase().startsWith("image")) {
            String imagePath = s3Uploader.upload(file, BOARD_DIRNAME);
            articleService.saveBoardImage(articleId, imagePath);
        }

        return "redirect:/articles/" + articleId;
    }

    @PostMapping("/{articleId}/delete")
    public String deleteArticle(
        @PathVariable Long articleId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        articleService.deleteArticle(articleId, principal.username());

        return "redirect:/articles";
    }

}

