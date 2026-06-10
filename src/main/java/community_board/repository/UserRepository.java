package community_board.repository;

import community_board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//JpaRepository 상속 - CRUD 메서드를 별도 구현 없이 바로 사용
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    //이메일로 유저 찾기
    Optional<User> findByEmail(String email);

    //이메일 중복 확인
    boolean existsByEmail(String email);

    //닉네임 중복 확인
    boolean existsByNickname(String nickname);

}
