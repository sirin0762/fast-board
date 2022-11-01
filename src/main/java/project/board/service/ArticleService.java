package project.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.board.domain.Article;
import project.board.domain.UserAccount;
import project.board.domain.type.SearchType;
import project.board.dto.ArticleDto;
import project.board.dto.ArticleWithCommentsDto;
import project.board.dto.response.PopularArticleResponse;
import project.board.repository.ArticleRepository;
import project.board.repository.UserAccountRepository;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleService {

    private final String VIEW_COOKIE_NAME = "alreadyViewCookie";
    private final int EXPIRE_VIEW_COOKIE_SECONDS = 60 * 60 * 24;

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtag(searchKeyword, pageable).map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
            .map(ArticleDto::from)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId)
            .map(ArticleWithCommentsDto::from)
            .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if (hashtag == null || hashtag.isBlank()) {
            return Page.empty();
        }

        return articleRepository.findByHashtag(hashtag, pageable).map(ArticleDto::from);
    }

    @Transactional(readOnly = true)
    public List<String> getHashtags() {
        return articleRepository.findAllDistinctHashtags();
    }

    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        articleRepository.save(dto.toEntity(userAccount));
    }

    public void updateArticle(Long articleId, ArticleDto dto) {
        try {
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            if (!article.getUserAccount().equals(userAccount)) {
                throw new SecurityException(MessageFormat.format("게시글 업데이트 실패, 해당 게시글 수정 권한이 없는 유저입니다. dto: {}", dto));
            }
            if (dto.title() != null) article.setTitle(dto.title());
            if (dto.content() != null) article.setContent(dto.content());
            article.setHashtag(dto.hashtag());
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패, 게시글을 찾을 수 없습니다. - dto: {}", dto);
        }
    }

    public Set<PopularArticleResponse> getPopularArticles(String hashtag) {
        return articleRepository.findTop2ByHashtagOrderByViewCountDesc(hashtag)
            .stream().map(PopularArticleResponse::from)
            .collect(Collectors.toSet());
    }

    public void deleteArticle(long articleId, String userId) {
        articleRepository.deleteByIdAndUserAccount_UserId(articleId, userId);
    }

    public void addViewCount(Long articleId, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || !isRequestContainViewCookie(request, articleId)) {
            Cookie cookie = createViewCookie(articleId);
            response.addCookie(cookie);
            articleRepository.findById(articleId).orElseThrow(
                () -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId)
            ).addViewCount();
        }
    }

    private boolean isRequestContainViewCookie(HttpServletRequest request, Long articleId) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(VIEW_COOKIE_NAME + articleId)) return true;
        }
        return false;
    }

    private Cookie createViewCookie(Long articleId) {
        Cookie cookie = new Cookie(VIEW_COOKIE_NAME + articleId, String.valueOf(articleId));
        cookie.setComment("조회수 중복 증가 방지 쿠키");
        cookie.setMaxAge(EXPIRE_VIEW_COOKIE_SECONDS);
        cookie.setHttpOnly(true);
        return cookie;
    }

}
