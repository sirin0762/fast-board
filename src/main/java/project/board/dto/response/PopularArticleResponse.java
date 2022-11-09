package project.board.dto.response;

import project.board.domain.Article;

import java.time.LocalDateTime;

public record PopularArticleResponse(
    Long id,
    String title,
    String imagePath,
    LocalDateTime createdAt
) {
    public static PopularArticleResponse from(Article article) {
        return new PopularArticleResponse(article.getId(), article.getTitle(), article.getImagePath(), article.getCreatedAt());
    }
}
