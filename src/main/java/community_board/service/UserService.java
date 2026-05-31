package community_board.service;

import community_board.domain.User;
import community_board.dto.UserSignupRequest;
import community_board.dto.UserSignupResponse;
import community_board.global.exception.BusinessException;
import community_board.global.exception.ErrorCode;
import community_board.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    //생성자 주입
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //회원가입 비즈니스 로직
    public UserSignupResponse signup(UserSignupRequest userSignupRequest) {
        if (userRepository.existsByEmail(userSignupRequest.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }
        if (userRepository.existsByNickname(userSignupRequest.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
        }
        //DTO -> domain
        User user = new User(
                null, userSignupRequest.getEmail(), userSignupRequest.getPassword(), userSignupRequest.getNickname(), userSignupRequest.getProfileImage()
        );

        //DB에 user 저장
        User savedUser = userRepository.save(user);

        return new UserSignupResponse(savedUser.getUserId(), savedUser.getEmail(), savedUser.getNickname(), savedUser.getProfileImage());


    }

}
