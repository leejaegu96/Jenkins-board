package com.example.jen.dto;

import com.example.jen.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;
    private Long boardId;
    private String content;
    private String writer;
    private LocalDateTime createdAt;

    public static CommentDto fromEntity(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .boardId(comment.getBoard().getId())
                .content(comment.getContent())
                .writer(comment.getWriter() != null ? comment.getWriter().getUsername() : null)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
