package community_board.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profile_image")
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer profileImageId;

    @Column(name = "jpg_path", nullable = false, length = 500)
    private String jpgPath;

    @Column(name = "webp_path",length = 500)
    private String webpPath;

    @Column(name = "thumbnail_path", length = 500)
    private String thumbnailPath;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static ProfileImage create(User user, String jpgPath) {
        ProfileImage profileImage = new ProfileImage();
        profileImage.user = user;
        profileImage.jpgPath = jpgPath;

        return profileImage;
    }

    public void updateWebpAndThumbnail(String webpPath, String thumbnailPath) {
        this.webpPath = webpPath;
        this.thumbnailPath = thumbnailPath;
    }

    public void deactivate() {
        this.isActive = false;
    }

}
