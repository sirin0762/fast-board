package project.board.dto;

import project.board.domain.UserAccount;

import java.time.LocalDateTime;

public record UserAccountDto(
    String userId,
    String userPassword,
    String email,
    String nickname,
    String memo,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime updatedAt,
    String updatedBy
) {

    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, String memo) {
        return new UserAccountDto(userId, userPassword, email, nickname, memo, null, null, null, null);
    }

    public static UserAccountDto of(String userId, String userPassword, String email, String nickname, String memo, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new UserAccountDto(userId, userPassword, email, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);
    }


    public static UserAccountDto from(UserAccount entity) {
        return new UserAccountDto(
            entity.getUserId(),
            entity.getUserPassword(),
            entity.getEmail(),
            entity.getNickname(),
            entity.getMemo(),
            entity.getCreatedAt(),
            entity.getCreatedBy(),
            entity.getUpdatedAt(),
            entity.getUpdatedBy()
        );
    }

    public UserAccount toEntity() {
        return UserAccount.of(
            this.userId,
            this.userPassword,
            this.email,
            this.nickname,
            this.memo
        );
    }

}
