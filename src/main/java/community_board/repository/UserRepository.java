package community_board.repository;

import community_board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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

    // @SQLRestriction으로 인해 JPQL로는 삭제된 데이터 조회 불가
    // nativeQuery=true로 직접 SQL 실행하여 @SQLRestriction 우회
    @Query(value = "SELECT * FROM user WHERE deleted_at IS NOT NULL AND deleted_at < :cutoff", nativeQuery = true)
    List<User> findDeletedBefore(@Param("cutoff") LocalDateTime cutoff);

}
