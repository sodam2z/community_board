package community_board.repository;


import community_board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer>, PostRepositoryCustom {

}
