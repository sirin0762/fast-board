package project.board.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import project.board.domain.Article;
import project.board.domain.type.SearchType;
import project.board.dto.ArticleDto;
import project.board.dto.ArticleUpdateDto;
import project.board.repository.ArticleRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks private ArticleService sut;

    @Mock private ArticleRepository articleRepository;

    /*

     */

    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticleList() {
        // given

        // when
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");

        // then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 검색하면, 게시글을 반환한다.")
    @Test
    void givenArticleId_whenSearchArticle_thenReturnsArticle() {
        // given

        // when
        ArticleDto articles = sut.searchArticle(1L);

        // then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // when
        sut.saveArticle(ArticleDto.of("title", "content", "hashtag", LocalDateTime.now(), "createdBy"));

        // then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 ID와 수정정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // when
        sut.updateArticle(1L, ArticleUpdateDto.of("title", "content", "hashtag"));

        // then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        // when
        sut.deleteArticle(1L);

        // then
        then(articleRepository).should().delete(any(Article.class));
    }

}
