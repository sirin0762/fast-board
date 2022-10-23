package project.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.board.domain.Article;
import project.board.domain.ArticleComment;
import project.board.domain.UserAccount;
import project.board.dto.ArticleCommentDto;
import project.board.repository.ArticleCommentRepository;
import project.board.repository.ArticleRepository;
import project.board.repository.UserAccountRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final ArticleCommentRepository articleCommentRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(long articleId) {
        return articleCommentRepository.findByArticleId(articleId)
            .stream().map(ArticleCommentDto::from)
            .toList();
    }

    public void saveArticleComment(ArticleCommentDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.articleId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            articleCommentRepository.save(dto.toEntity(article, userAccount));
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패, 댓글 작성에 필요한 정보를 찾을 수 없습니다 - {}", e.getLocalizedMessage());
        }
    }

    public void updateArticleComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if (dto.content() != null) articleComment.setContent(dto.content());
            articleCommentRepository.save(articleComment);
        } catch (EntityNotFoundException e) {
            log.warn("댓글 업데이트 실패. 댓글을 찾을 수 없습니다 - dto: {}", dto);
        }
    }

    public void deleteArticleComment(Long articleCommentId, String userId) {
        articleCommentRepository.deleteByIdAndUserAccount_UserId(articleCommentId, userId);
    }

}
