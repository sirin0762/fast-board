package project.board.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.board.domain.Article;
import project.board.domain.type.SearchType;
import project.board.dto.ArticleDto;
import project.board.dto.ArticleWithCommentsDto;
import project.board.repository.ArticleRepository;
import project.board.repository.UserAccountRepository;
import static project.board.Fixture.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;

    @Mock private ArticleRepository articleRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // when
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("검색어와 함꼐 게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // when
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @DisplayName("게시글 ID를 조회하면, 댓글 달린 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchArticleWithComments_thenReturnsArticleWithComments() {
        // given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        ArticleWithCommentsDto dto = sut.getArticleWithComments(articleId);

        // then
        assertThat(dto)
            .hasFieldOrPropertyWithValue("title", article.getTitle())
            .hasFieldOrPropertyWithValue("content", article.getContent())
            .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("댓글 달린 게시글이 없으면, 예외를 던진다")
    @Test
    void givenNonexistenceArticleId_whenSearchingArticleWithComments_thenThrowException() {
        // given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.getArticleWithComments(articleId));

        // then
        assertThat(t)
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);

    }
    @DisplayName("게시글을 조회하면, 게시글을 반환한다")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle() {
        // given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when
        ArticleDto dto = sut.getArticle(articleId);

        // then
        assertThat(dto)
            .hasFieldOrPropertyWithValue("title", article.getTitle())
            .hasFieldOrPropertyWithValue("content", article.getContent())
            .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다")
    @Test
    void givenNonexistenceArticleId_whenSearchingArticle_thenThrowsException() {
        // given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // then
        assertThat(t)
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }


    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // given
        ArticleDto dto = createArticleDto();
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        // when
        sut.saveArticle(dto);

        // then
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 수정정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("new Title", "new content", "new hashtag");
        given(articleRepository.getReferenceById(dto.id())).willReturn(article);

        // when
        sut.updateArticle(dto.id(), dto);

        // then
        assertThat(article)
            .hasFieldOrPropertyWithValue("title", dto.title())
            .hasFieldOrPropertyWithValue("content", dto.content())
            .hasFieldOrPropertyWithValue("hashtag", dto.hashtag());
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무것도 하지 않는다.")
    @Test
    void givenNonexistenceArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
        // given
        ArticleDto dto = createArticleDto("new title", "new content", "new hashtag");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // when
        sut.updateArticle(dto.id(), dto);

        // then
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);

        // when
        sut.deleteArticle(articleId);

        // then
        then(articleRepository).should().deleteById(articleId);
    }

    @DisplayName("검색어 없이 게시글을 해시테그 검색하면, 빈 페이지를 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchArticlesViaHashtag_thenReturnsEmptyPage() {
        // given
        Pageable pageable = Pageable.ofSize(20);
        // when
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable);

        // then
        assertThat(articles).isEqualTo(Page.empty());
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("게시글을 해시테그 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchArticlesViaHashtag_thenReturnsArticlesPage() {
        // given
        Pageable pageable = Pageable.ofSize(20);
        String hashtag = "#spring";
        given(articleRepository.findByHashtag(hashtag, pageable)).willReturn(Page.empty(pageable));

        // when
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtag, pageable);

        // then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtag(hashtag, pageable);
    }

    @DisplayName("해시태그를 조회하면, 유니크 해시태그 리스트를 반환한다.")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() {
        // given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        given(articleRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);

        // when
        List<String> hashtags = sut.getHashtags();

        // then
        assertThat(hashtags).isEqualTo(expectedHashtags);
        then(articleRepository).should().findAllDistinctHashtags();
    }

}
