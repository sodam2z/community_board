package community_board.event;

import community_board.service.PostViewCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventListener {

    private final PostViewCountService postViewCountService;

    //조회수 증가 담당 리스너
    @Async
    @EventListener
    public void handle(PostViewedEvent event) {
        postViewCountService.increase(event.postId());
    }
}
