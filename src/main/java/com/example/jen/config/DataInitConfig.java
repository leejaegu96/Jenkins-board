package com.example.jen.config;

import com.example.jen.dto.UserJoinDto;
import com.example.jen.entity.Board;
import com.example.jen.entity.User;
import com.example.jen.repository.BoardRepository;
import com.example.jen.repository.UserRepository;
import com.example.jen.service.BoardService;
import com.example.jen.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitConfig {

    private final UserService userService;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        // 기존 데이터가 없을 때만 초기화 (간단한 예제용)
        if (!userRepository.existsByUsername("user")) {
            // 더미 유저 생성
            UserJoinDto userDto = new UserJoinDto();
            userDto.setUsername("user");
            userDto.setPassword("1234");
            userService.join(userDto);

            User user = userRepository.findByUsername("user").orElseThrow();

            // 더미 게시글 생성
            Board board1 = Board.builder()
                    .title("첫 번째 게시물입니다.")
                    .content("안녕하세요. 테스트 게시물입니다.")
                    .writer(user)
                    .build();

            Board board2 = Board.builder()
                    .title("두 번째 게시물입니다.")
                    .content("스프링 부트 프로젝트 시작합니다.")
                    .writer(user)
                    .build();

            boardRepository.save(board1);
            boardRepository.save(board2);
        }
    }
}
