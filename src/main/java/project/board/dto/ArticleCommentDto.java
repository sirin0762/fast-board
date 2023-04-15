package project.board.dto;

import project.board.domain.Article;
import project.board.domain.ArticleComment;
import project.board.domain.CommentReply;
import project.board.domain.UserAccount;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleCommentDto(
    Long id,
    Long articleId,
    UserAccountDto userAccountDto,
    String content,
    Set<CommentReplyDto> commentReplyDtos,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime modifiedAt,
    String modifiedBy
) {

    public ArticleCommentDto(Long id, Long articleId, UserAccountDto userAccountDto, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        this(id, articleId, userAccountDto, content, null, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDto of(Long articleId, UserAccountDto userAccountDto, String content) {
        return new ArticleCommentDto(null, articleId, userAccountDto, content, null, null, null, null);
    }

    public static ArticleCommentDto of(Long articleId, UserAccountDto userAccountDto, String content, Set<CommentReplyDto> replyDtos) {
        return new ArticleCommentDto(null, articleId, userAccountDto, content, replyDtos, null, null, null, null);
    }
    public static ArticleCommentDto of(Long id, Long articleId, UserAccountDto userAccountDto, String content, Set<CommentReplyDto> replyDtos,  LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleCommentDto(id, articleId, userAccountDto, content, replyDtos, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDto from(ArticleComment entity) {
        return new ArticleCommentDto(
            entity.getId(),
            entity.getArticle().getId(),
            UserAccountDto.from(entity.getUserAccount()),
            entity.getContent(),
            entity.getCommentReplies().stream()
                .map(CommentReplyDto::from)
                .collect(Collectors.toSet()),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getUpdatedAt(),
            entity.getUpdatedBy()
        );
    }

    public ArticleComment toEntity(Article article, UserAccount userAccount) {
        return ArticleComment.of(userAccount, article, content);
    }
}
