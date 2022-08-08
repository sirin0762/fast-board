package project.board.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import project.board.configuration.JpaConfiguration;
import project.board.domain.Article;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Jpa 연결 테스트")
@Import(JpaConfiguration.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public JpaRepositoryTest(
        @Autowired ArticleRepository articleRepository,
        @Autowired ArticleCommentRepository articleCommentRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @Test
    @DisplayName("select test")
    void JpaSelectTest() {
        // given

        // when
        List<Article> articles = articleRepository.findAll();

        // then
        assertThat(articles)
            .isNotNull()
            .hasSize(123);
    }

    @Test
    @DisplayName("insert 테스트")
    void JpaInsertTest() {
        // given
        long previousCount = articleRepository.count();

        // when
        Article savedArticle = articleRepository.save(Article.of("new Title", "new Content", "new Hashtag"));

        // then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @Test
    @DisplayName("update 테스트")
    void JpaUpdateTest() {
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#newHashtag";
        article.setHashtag(updatedHashtag);

        // when
        Article savedArticle = articleRepository.saveAndFlush(article);

        // then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @Test
    @DisplayName("delete 테스트")
    void JpaDeleteTest() {
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        // when
        articleRepository.delete(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }
}
