package community_board.service;

import community_board.domain.User;
import community_board.dto.UserLoginRequest;
import community_board.dto.UserLoginResponse;
import community_board.global.exception.BusinessException;
import community_board.global.exception.ErrorCode;
import community_board.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    /*로그인 비즈니스 로직
    1. 이메일로 User 조회
    2. 없으면 LOGIN_FAILED
    3. 있으면 password 비교
    4. 다르면 LOGIN_FAILED
    5. 맞으면 UserLoginResponse 반환*/
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(userLoginRequest.getEmail());

        if (optionalUser.isEmpty()) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        User user = optionalUser.get();

        if (!user.getPassword().equals(userLoginRequest.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        return new UserLoginResponse(user.getUserId(),user.getNickname(),user.getProfileImage());
    }

}
