package project.board.dto.response;

import project.board.domain.Article;

import java.time.LocalDateTime;

public record PopularArticleResponse(
    Long id,
    String title,
    LocalDateTime createdAt
) {
    public static PopularArticleResponse from(Article article) {
        return new PopularArticleResponse(article.getId(), article.getTitle(), article.getCreatedAt());
    }
}
