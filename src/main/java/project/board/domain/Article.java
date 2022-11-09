package project.board.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
    @Index(columnList = "title"),
    @Index(columnList = "hashtag"),
    @Index(columnList = "createdAt"),
    @Index(columnList = "createdBy"),
})
@Entity(name = "article")
public class Article extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @Column(nullable = false, length = 255)
    private String title;

    @Setter @Column(nullable = false, length = 10_000)
    private String content;

    @Column(length = 255)
    private String hashtag;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    @Column @Setter
    private String imagePath;

    @ToString.Exclude
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @OrderBy("id")
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @Setter @ManyToOne(optional = true)
    @JoinColumn(name = "user_id")
    private UserAccount userAccount;

    protected Article() {}

    private Article(UserAccount userAccount, String title, String content, String hashtag) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Java 14 pattern matching
        if (!(o instanceof Article article)) return false;
        return id != null && id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addViewCount() {
        this.viewCount += 1;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag.startsWith("#") ? hashtag : "#" + hashtag;
    }
}
