package community_board.service;

import community_board.domain.User;
import community_board.dto.user.GetUserResponse;
import community_board.dto.user.UserSignupRequest;
import community_board.dto.user.UserSignupResponse;
import community_board.global.exception.CommonErrorCode;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 내부 로직에서 User 객체가 필요할 때 사용
    @Transactional(readOnly = true)
    public User findById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RestApiException(
                                UserErrorCode.USER_NOT_FOUND
                        )
                );
    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public GetUserResponse getUserInfo(Integer userId, Integer loginUserId) {
        validateUserAccess(userId, loginUserId);

        User user = findById(userId);
        return GetUserResponse.from(user);
    }

    private void validateUserAccess(Integer userId, Integer loginUserId) {
        if (!userId.equals(loginUserId)) {
            throw new RestApiException(CommonErrorCode.FORBIDDEN_ACCESS);
        }
    }

    //회원가입 비즈니스 로직
    @Transactional
    public UserSignupResponse signup(UserSignupRequest userSignupRequest) {
        if (userRepository.existsByEmail(userSignupRequest.getEmail())) {
            throw new RestApiException(UserErrorCode.EMAIL_DUPLICATED);
        }
        if (userRepository.existsByNickname(userSignupRequest.getNickname())) {
            throw new RestApiException(UserErrorCode.NICKNAME_DUPLICATED);
        }
        //DTO -> domain
        User user = new User(
                userSignupRequest.getEmail(),
                passwordEncoder.encode(userSignupRequest.getPassword()),
                userSignupRequest.getNickname()
        );

        //DB에 user 저장
        User savedUser = userRepository.save(user);

        return new UserSignupResponse(savedUser.getUserId(), savedUser.getEmail(), savedUser.getNickname());


    }

}
