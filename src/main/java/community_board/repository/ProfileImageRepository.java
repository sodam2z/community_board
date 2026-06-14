package community_board.repository;

import community_board.domain.ProfileImage;
import community_board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Integer> {
    Optional<ProfileImage> findByUser(User user);
    List<ProfileImage> findByIsActiveFalse();
}
