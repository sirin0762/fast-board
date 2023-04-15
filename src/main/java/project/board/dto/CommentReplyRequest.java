package project.board.dto;

public record CommentReplyRequest(String content) {

    public static CommentReplyRequest of(String content) {
        return new CommentReplyRequest(content);
    }

    public CommentReplyDto toDto(Long articleCommentId, UserAccountDto dto) {
        return CommentReplyDto.of(articleCommentId, dto, content);
    }

}
