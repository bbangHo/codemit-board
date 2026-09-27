package org.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.board.domain.Post;

import java.time.LocalDateTime;

public final class PostDto {
    private PostDto() {}

    public record CreateRequest(@NotBlank @Size(max = 200) String title,
                                @NotBlank String content) {}

    public record UpdateRequest(@NotBlank @Size(max = 200) String title,
                                @NotBlank String content) {}

    public record Response(Long id,
                           String title,
                           String content,
                           Long authorId,
                           String authorNickname,
                           long commentCount,
                           LocalDateTime createdAt,
                           LocalDateTime updatedAt
    ) {

        public static Response from(Post post) {
            return new Response(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getAuthor().getId(),
                    post.getAuthor().getNickname(),
                    post.getCommentCount(),
                    post.getCreatedAt(),
                    post.getUpdatedAt());
        }
    }
}
