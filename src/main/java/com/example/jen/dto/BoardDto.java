package com.example.jen.dto;

import com.example.jen.entity.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BoardDto {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private int viewCount;
    private long likeCount;
    private boolean isLikedByCurrentUser; // 현재 접속자가 좋아요를 눌렀는지 여부
    private LocalDateTime createdAt;

    public static BoardDto fromEntity(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter() != null ? board.getWriter().getUsername() : null)
                .viewCount(board.getViewCount())
                // likeCount와 isLikedByCurrentUser는 Service에서 별도로 세팅하도록 기본값만 줍니다
                .createdAt(board.getCreatedAt())
                .build();
    }
}
