package community_board.repository;

import community_board.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcUserRepository(NamedParameterJdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public User save(User user) {
        //user 테이블에 새 회원 저장
        String sql = """
                INSERT INTO `user` (`email`, `password`, `nickname`, `profile_image`) VALUES (:email, :password, :nickname, :profileImage)""";

        //파라미터 객체 생성
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(user);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, param, keyHolder);

        int userId = keyHolder.getKey().intValue();
        user.setUserId(userId);
        return user;
    }


    @Override
    public Optional<User> findByEmail(String email) {
        String sql = """
                SELECT * FROM `user` WHERE `email` = :email """;

        try { User user = jdbcTemplate.queryForObject(
                sql, Map.of("email", email), userRowMapper()
        );
            return Optional.of(user); }
        catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    private RowMapper<User> userRowMapper() {
        return BeanPropertyRowMapper.newInstance(User.class);
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = """
                SELECT COUNT(*) FROM `user` WHERE `email` = :email""";
        Integer count = jdbcTemplate.queryForObject(sql, Map.of("email", email), Integer.class);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByNickname(String nickname) {
        String sql = """
                SELECT COUNT(*) FROM `user` WHERE `nickname` = :nickname""";
        Integer count = jdbcTemplate.queryForObject(sql, Map.of("nickname", nickname), Integer.class);
        return count != null && count > 0;
    }
}
