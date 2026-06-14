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
@Table(name = "post_image")
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postImageId;

    @Column(name = "jpg_path", nullable = false, length = 500)
    private String jpgPath;

    @Column(name = "webp_path",length = 500)
    private String webpPath;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public static PostImage create(Post post, String jpgPath) {
        PostImage postImage = new PostImage();
        postImage.post = post;
        postImage.jpgPath = jpgPath;

        return postImage;
    }

    public void updateWebp(String webpPath) {
        this.webpPath = webpPath;
    }

    public void deactivate() {
        this.isActive = false;
    }

}
