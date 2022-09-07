package project.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.board.domain.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

}
