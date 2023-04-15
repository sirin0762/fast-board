package project.board.dto;

import project.board.domain.ArticleComment;
import project.board.domain.CommentReply;
import project.board.domain.UserAccount;

import java.time.LocalDateTime;

public record CommentReplyDto(
    Long id,
    Long commentId,
    UserAccountDto userAccountDto,
    String content,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {

    public static CommentReplyDto of(Long commentId, UserAccountDto userAccountDto, String content) {
        return new CommentReplyDto(null, commentId, userAccountDto, content,null, null, null, null);
    }

    public static CommentReplyDto of(Long id, Long commentId, UserAccountDto userAccountDto, String content) {
        return new CommentReplyDto(id, commentId,  userAccountDto, content,  null, null, null, null);
    }

    public static CommentReplyDto from(CommentReply entity) {
        return new CommentReplyDto(
            entity.getId(),
            entity.getArticleComment().getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getUpdatedAt(),
            entity.getUpdatedBy()
        );
    }

    public CommentReply toEntity(ArticleComment articleComment, UserAccount userAccount) {
        return CommentReply.of(articleComment, userAccount, content);
    }

}
