package org.example.board.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.board.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MemberPrincipal implements UserDetails {
    private final Long id;
    private final String email;
    private final String password;
    private final String nickname;

    public static MemberPrincipal from(Member member) {
        return new MemberPrincipal(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getNickname());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_USER")

        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
