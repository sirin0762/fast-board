package project.board.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.board.domain.Article;
import project.board.domain.ArticleComment;
import project.board.domain.UserAccount;
import project.board.dto.ArticleCommentDto;
import project.board.dto.UserAccountDto;
import project.board.repository.ArticleCommentRepository;
import project.board.repository.ArticleRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks private ArticleCommentService sut;

    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleCommentRepository articleCommentRepository;

    @DisplayName("게시글 ID로 조회하면, 해당하는 댓글 리스트를 반환한다.")
    @Test
    void givenArticleId_whenSearchingComments_thenReturnsArticleComments() {
        // given
        Long articleId = 1L;
        ArticleComment expected = createArticleComment("content");

        given(articleCommentRepository.findByArticleId(articleId)).willReturn(List.of(expected));

        // when
        List<ArticleCommentDto> actual = sut.searchArticleComments(articleId);

        // then
        assertThat(actual).hasSize(1)
                .first().hasFieldOrPropertyWithValue("content", expected.getContent());
        then(articleCommentRepository).should().findByArticleId(articleId);
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 저장한다.")
    @Test
    void givenArticleCommentDto_whenSavingArticleComment_thenSavesArticleComments() {
        // given
        ArticleCommentDto dto = createArticleCommentDto("comment");
        given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);

        // when
        sut.saveArticleComment(dto);

        // then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @DisplayName("댓글 저장을 시도했는데 맞는 게시글이 없으면, 경고 로그를 찍고 아무것도 안한다.")
    @Test
    void givenNonexistenceArticle_whenSavingArticleComment_thenLogsSituationANdDoesNothing() {
        // given
        ArticleCommentDto dto = createArticleCommentDto("comment");
        given(articleRepository.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);

        // when
        sut.saveArticleComment(dto);

        // then
        then(articleRepository).should().getReferenceById(dto.id());
        then(articleCommentRepository).shouldHaveNoInteractions();
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 수정한다")
    @Test
    void givenArticleCommentInfo_whenUpdatingArticleComment_thenUpdatesArticleComment() {
        // given
        String oldContent = "content";
        String updatedContent = "new content";
        ArticleComment articleComment = createArticleComment(oldContent);
        ArticleCommentDto dto = createArticleCommentDto(updatedContent);
        given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);

        // when
        sut.updateArticleComment(dto);

        // then
        assertThat(articleComment.getContent())
            .isNotEqualTo(oldContent)
            .isEqualTo(updatedContent);
        then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 댓글 정보를 수정하려고 하면, 경고 로그를 찍고 아무 것도 안 한다.")
    @Test
    void givenNonexistentArticleComment_whenUpdatingArticleComment_thenLogsWarningAndDoesNothing() {
        // Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleCommentRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticleComment(dto);

        // Then
        then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("댓글 ID를 입력하면, 댓글을 삭제한다.")
    @Test
    void givenArticleCommentId_whenDeletingArticleComment_thenDeletesArticleComment() {
        // Given
        Long articleCommentId = 1L;
        willDoNothing().given(articleCommentRepository).deleteById(articleCommentId);

        // When
        sut.deleteArticleComment(articleCommentId);

        // Then
        then(articleCommentRepository).should().deleteById(articleCommentId);
    }

    private ArticleCommentDto createArticleCommentDto(String content) {
        return ArticleCommentDto.of(
            1L,
            1L,
            createUserAccountDto(),
            content,
            LocalDateTime.now(),
            "sirin",
            LocalDateTime.now(),
            "sirin"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
            "sirin",
            "password",
            "sirin@email.com",
            "sirin",
            "memo",
            LocalDateTime.now(),
            "sirin",
            LocalDateTime.now(),
            "sirin"
        );
    }

    private ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
            createUserAccount(),
            createArticle(),
            content
        );
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
            "sirin",
            "password",
            "sirin@email.com",
            "sirin",
            "memo"
        );
    }

    private Article createArticle() {
        return Article.of(createUserAccount(), "title", "content", "hashtag");
    }


}
