package project.board.repository.querydsl;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import project.board.domain.Article;
import project.board.domain.QArticle;

import java.util.List;

public class ArticleRepositoryCustomImpl extends QuerydslRepositorySupport implements ArticleRepositoryCustom {

    public ArticleRepositoryCustomImpl() {
        super(Article.class);
    }

    @Override
    public List<String> findAllDistinctHashtags() {
        QArticle article = QArticle.article;

        return from(article)
            .distinct()
            .select(article.hashtag)
            .where(article.isNotNull())
            .fetch();
    }

}
