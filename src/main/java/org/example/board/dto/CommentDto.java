package org.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import org.example.board.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

public final class CommentDto {
    private CommentDto() {
    }

    public record CreateRequest(@NotBlank String content, Long parentCommentId) {
    }

    public record UpdateRequest(@NotBlank String content) {
    }

    public record Response(Long id,
                           String content,
                           Long authorId,
                           String authorNickname,
                           Long postId,
                           List<Response> reComments,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        public static Response from(Comment comment) {
            return new Response(comment.getId(),
                    comment.getContent(),
                    comment.getAuthor().getId(),
                    comment.getAuthor().getNickname(),
                    comment.getPost().getId(),
                    comment.getChildComment().stream()
                            .map(CommentDto.Response::from)
                            .toList(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt());
        }
    }
}
