package project.board.dto.response;

import project.board.dto.ArticleCommentDto;
import project.board.dto.CommentReplyDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

public record ArticleCommentResponse(
    Long id,
    String content,
    Set<CommentReplyDto> replies,
    LocalDateTime createdAt,
    String userId,
    String email,
    String nickname
) implements Serializable {

    public static ArticleCommentResponse of(Long id, String content, Set<CommentReplyDto> replies, String userId, LocalDateTime createdAt, String email, String nickname) {
        return new ArticleCommentResponse(id, content,  replies, createdAt, userId, email, nickname);
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleCommentResponse(
            dto.id(),
            dto.content(),
            dto.commentReplyDtos(),
            dto.createdAt(),
            dto.userAccountDto().userId(),
            dto.userAccountDto().email(),
            nickname
        );
    }

}
