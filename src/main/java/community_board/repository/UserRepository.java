package community_board.repository;

import community_board.domain.User;

import java.util.Optional;

public interface UserRepository {
    //회원가입 - 이메일 중복 확인, 닉네임 중복 확인,유저 저장,저장된 userId 확인
    //로그인 - 이메일로 유저 찾기,비밀번호 비교
    //회원정보수정 - userId로 유저 존재 확인,닉네임 중복 확인,nickname/profileImage 수정
    //회원정보조회 - userId로 유저 존재 확인
    //비밀번호수정 - 비밀번호 업데이트
    //탈퇴 - 유저 삭제

    //유저 저장
    User save(User user);

    //회원 존재 여부 확인
    //Optional<User> findById(Integer userId);

    //이메일로 유저 찾기
    Optional<User> findByEmail(String email);

    //이메일 중복 확인
    boolean existsByEmail(String email);

    //닉네임 중복 확인
    boolean existsByNickname(String nickname);

    //회원 정보 수정
    //void updateProfile(Integer userId, String nickname, String profileImage);

    //비밀번호 업데이트
    //void updatePassword(Integer userId, String password);

    //유저 삭제
    //void deleteById(Integer userId);

}
