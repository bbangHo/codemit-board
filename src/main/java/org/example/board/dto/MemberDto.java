package org.example.board.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.board.domain.Member;

import java.time.LocalDateTime;

public final class MemberDto {
    private MemberDto() {}

    public record SignupRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.") String password,
            @NotBlank @Size(max = 30, message = "닉네임은 30자리 이하여야 합니다.") String nickname) {}

    public record LoginRequest(@NotBlank @Email String email,
                               @NotBlank String password) {}

    public record Response(Long id,
                           String email,
                           String nickname,
                           LocalDateTime createdAt) {

        public static Response from(Member member) {
            return new Response(
                    member.getId(),
                    member.getEmail(),
                    member.getNickname(),
                    member.getCreatedAt());
        }
    }
}
