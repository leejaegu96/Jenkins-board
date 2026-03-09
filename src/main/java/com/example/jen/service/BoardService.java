package com.example.jen.service;

import com.example.jen.dto.BoardDto;
import com.example.jen.entity.Board;
import com.example.jen.entity.User;
import com.example.jen.entity.BoardLike;
import com.example.jen.repository.BoardLikeRepository;
import com.example.jen.repository.BoardRepository;
import com.example.jen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;

    @Transactional
    public Long create(BoardDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(user)
                .build();

        return boardRepository.save(board).getId();
    }

    public List<BoardDto> getList() {
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(BoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<BoardDto> getListByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다."));

        // NOTE: 실제 운영에선 boardRepository.findByWriter(user) 로 쿼리 메서드를 파는 것이 성능상 좋으나
        // 간소화를 위해 전체 리스트 필터링 방식으로 구현
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .filter(b -> b.getWriter() != null && b.getWriter().getUsername().equals(username))
                .map(BoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    public BoardDto getDetail(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        BoardDto dto = BoardDto.fromEntity(board);
        dto.setLikeCount(boardLikeRepository.countByBoard(board));
        return dto;
    }

    public BoardDto getDetailWithLike(Long id, String username) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다."));

        BoardDto dto = BoardDto.fromEntity(board);
        dto.setLikeCount(boardLikeRepository.countByBoard(board));
        dto.setLikedByCurrentUser(boardLikeRepository.existsByUserAndBoard(user, board));
        return dto;
    }

    @Transactional
    public void toggleLike(Long boardId, String username) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자가 존재하지 않습니다."));

        boardLikeRepository.findByUserAndBoard(user, board)
                .ifPresentOrElse(
                        boardLikeRepository::delete,
                        () -> boardLikeRepository.save(BoardLike.builder().user(user).board(board).build()));
    }

    @Transactional
    public void increaseViewCount(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
        board.setViewCount(board.getViewCount() + 1);
    }

    @Transactional
    public void update(Long id, BoardDto dto, String username) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        if (!board.getWriter().getUsername().equals(username)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
    }

    @Transactional
    public void delete(Long id, String username) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));

        if (!board.getWriter().getUsername().equals(username)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }
}
