package project.board.dto.response;

import project.board.dto.ArticleWithCommentsDto;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
    Long id,
    String title,
    String content,
    String hashtag,
    String imagePath,
    LocalDateTime createdAt,
    String userId,
    String email,
    String nickname,
    Set<ArticleCommentResponse> articleCommentsResponses
) {

    public static ArticleWithCommentsResponse of(Long id, String title, String content, String hashtag, String imagePath, LocalDateTime createdAt, String userId, String email, String nickname, Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtag, imagePath, createdAt, userId, email, nickname, articleCommentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponse(
            dto.id(),
            dto.title(),
            dto.content(),
            dto.hashtag(),
            dto.imagePath(),
            dto.createdAt(),
            dto.userAccountDto().userId(),
            dto.userAccountDto().email(),
            nickname,
            dto.articleCommentDtos().stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }

}
