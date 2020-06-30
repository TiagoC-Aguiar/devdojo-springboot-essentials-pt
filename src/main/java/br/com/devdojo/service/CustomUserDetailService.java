package br.com.devdojo.service;

import br.com.devdojo.model.UserEntity;
import br.com.devdojo.repository.UserRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomUserDetailService implements UserDetailsService {

    private final UserRespository userRespository;

    @Autowired
    public CustomUserDetailService(UserRespository userRespository) {
        this.userRespository = userRespository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = Optional.ofNullable(userRespository.findByUsername(username)).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<GrantedAuthority> authorityAdmin = AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN");
        List<GrantedAuthority> authorityUser = AuthorityUtils.createAuthorityList("ROLE_USER");
        return new User(user.getUsername(), user.getPassword(), user.isAdmin() ? authorityAdmin : authorityUser);
    }
}
