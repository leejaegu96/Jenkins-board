package com.example.jen.repository;

import com.example.jen.entity.Board;
import com.example.jen.entity.BoardLike;
import com.example.jen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByUserAndBoard(User user, Board board);

    boolean existsByUserAndBoard(User user, Board board);

    long countByBoard(Board board);
}
