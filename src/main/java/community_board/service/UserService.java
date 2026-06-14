package community_board.service;

import community_board.domain.Post;
import community_board.domain.User;
import community_board.dto.user.*;
import community_board.global.exception.CommonErrorCode;
import community_board.global.exception.RestApiException;
import community_board.global.exception.UserErrorCode;
import community_board.repository.CommentRepository;
import community_board.repository.PostRepository;
import community_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileImageService profileImageService;
    private final PostImageService postImageService;

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

    //회원 정보 수정
    @Transactional
    public UpdateUserResponse updateUserInfo(Integer userId, Integer loginUserId, UpdateUserRequest request) {

        validateUserAccess(userId, loginUserId);

        User user = findById(userId);
        String newNickname = request.getNickname();

        if (!user.getNickname().equals(newNickname) && userRepository.existsByNickname(newNickname)) {
            throw new RestApiException(UserErrorCode.NICKNAME_DUPLICATED);
        }

        user.update(newNickname);

        return UpdateUserResponse.from(user);
    }

    //회원 비밀번호 수정
    @Transactional
    public void updatePassword(Integer userId, Integer loginUserId, UpdatePasswordRequest request) {

        //본인만 수정 가능
        validateUserAccess(userId, loginUserId);

        // 비밀번호 불일치 검증
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RestApiException(UserErrorCode.PASSWORD_MISMATCH);
        }

        //유저 조회
        User user = findById(userId);

        //비밀번호 암호화 -> 비밀번호 업데이트
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        //TODO: 비밀번호 변경 후 토큰 재발급 미구현
        user.updatePassword(encodedPassword);
    }

    //유저 탈퇴
    @Transactional
    public void deleteById(Integer userId, Integer loginUserId) {

        //본인만 탈퇴 가능
        validateUserAccess(userId, loginUserId);

        //탈퇴할 유저 조회
        User user = findById(userId);

        //유저가 작성한 게시글과 게시글 이미지 소프트 딜리트
        List<Post> posts = postRepository.findByUser(user);
        posts.forEach(post -> {
            postImageService.deactivatePostImages(post);
            post.delete();
        });

        //유저가 작성한 댓글 소프트 딜리트
        commentRepository.findByUser(user)
                .forEach(comment -> comment.delete());

        //프로필 이미지 소프트 딜리트
        profileImageService.deactivateProfileImage(user);

        //유저 소프트 딜리트
        user.delete();
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

        // 이미지 경로가 있으면 프로필 이미지 DB 저장
        if (userSignupRequest.getJpgPath() != null) {
            profileImageService.saveProfileImage(
                    savedUser.getUserId(),
                    userSignupRequest.getJpgPath(),
                    userSignupRequest.getWebpPath(),
                    userSignupRequest.getThumbnailPath()
            );
        }

        return new UserSignupResponse(savedUser.getUserId(), savedUser.getEmail(), savedUser.getNickname());


    }

}
