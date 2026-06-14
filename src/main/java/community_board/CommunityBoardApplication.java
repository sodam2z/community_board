package community_board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CommunityBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityBoardApplication.class, args);
	}

}
