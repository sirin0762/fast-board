package project.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.board.domain.CommentReply;

public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {}
