package project.board.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import project.board.configuration.JpaConfiguration;
import project.board.domain.Article;
import project.board.domain.ArticleComment;
import project.board.domain.UserAccount;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Jpa 연결 테스트")
@Import(JpaRepositoryTest.TestJpaConfiguration.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest(
        @Autowired ArticleRepository articleRepository,
        @Autowired ArticleCommentRepository articleCommentRepository,
        @Autowired UserAccountRepository userAccountRepository
    ) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
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
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("userId", "password", null, null, null));
        // when
        Article savedArticle = articleRepository.save(
            Article.of(userAccount, "new Title", "new Content", "new Hashtag")
        );

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


    @EnableJpaAuditing
    @TestConfiguration
    public static class TestJpaConfiguration {

        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("sirin");
        }
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
