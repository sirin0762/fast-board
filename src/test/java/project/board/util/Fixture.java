package project.board.util;

import org.springframework.test.util.ReflectionTestUtils;
import project.board.domain.Article;
import project.board.domain.ArticleComment;
import project.board.domain.UserAccount;
import project.board.dto.ArticleCommentDto;
import project.board.dto.ArticleDto;
import project.board.dto.ArticleWithCommentsDto;
import project.board.dto.UserAccountDto;

import java.time.LocalDateTime;
import java.util.Set;

public class Fixture {

    public static ArticleCommentDto createArticleCommentDto(String content) {
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

    public static UserAccountDto createUserAccountDto() {
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

    public static ArticleComment createArticleComment(String content) {
        return ArticleComment.of(
            createUserAccount(),
            createArticle(),
            content
        );
    }

    public static UserAccount createUserAccount() {
        return UserAccount.of(
            "sirin",
            "password",
            "sirin@email.com",
            "sirin",
            "memo"
        );
    }

    public static Article createArticle() {
        Article article = Article.of(
            createUserAccount(),
            "title",
            "content",
            "hashtag"
        );
        ReflectionTestUtils.setField(article, "id", 1L);

        return article;
    }


    public static ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "hashtag");
    }

    public static ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L,
            createUserAccountDto(),
            title,
            content,
            hashtag,
            LocalDateTime.now(),
            "createdBy",
            LocalDateTime.now(),
            "modifiedBy"
        );
    }

    public static ArticleWithCommentsDto createArticleWithCommentDto() {
        return ArticleWithCommentsDto.of(
            1L,
            createUserAccountDto(),
            Set.of(),
            "title",
            "content",
            "hashtag",
            "",
            LocalDateTime.now(),
            "sirin",
            LocalDateTime.now(),
            "sirin"
        );
    }

}
