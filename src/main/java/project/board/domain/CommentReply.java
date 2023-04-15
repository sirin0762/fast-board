package project.board.domain;

import io.micrometer.core.lang.Nullable;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Entity
@Table(name = "comment_reply")
@ToString(callSuper = true)
public class CommentReply extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_comment_id", nullable = false)
    private ArticleComment articleComment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(nullable = false, length = 500)
    private String content;

    private CommentReply(ArticleComment articleComment, UserAccount userAccount, String content) {
        this.articleComment = articleComment;
        this.userAccount = userAccount;
        this.content = content;
    }

    protected CommentReply() {
    }

    public static CommentReply of(ArticleComment articleComment, UserAccount userAccount, String content) {
        return new CommentReply(articleComment, userAccount, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentReply that = (CommentReply) o;
        return Objects.equals(id, that.id) && Objects.equals(articleComment, that.articleComment) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
