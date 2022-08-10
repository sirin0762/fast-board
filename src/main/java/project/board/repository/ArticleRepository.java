package project.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import project.board.domain.Article;

@RepositoryRestResource
public interface ArticleRepository extends JpaRepository<Article, Long> {

}
