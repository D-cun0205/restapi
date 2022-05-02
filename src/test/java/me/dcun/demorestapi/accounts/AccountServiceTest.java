package me.dcun.demorestapi.accounts;

import me.dcun.demorestapi.common.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {
    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppProperties appProperties;

    @Test
    void findByUsername() {
        //When
        UserDetailsService userDetailsService = this.accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(appProperties.getAdminUsername());

        //Then
        assertThat(passwordEncoder.matches(appProperties.getAdminPassword(), userDetails.getPassword())).isTrue();
    }

    @Test
    void expectedExceptionFindByUsername() {
        UsernameNotFoundException exceptionWasExpected = assertThrows(UsernameNotFoundException.class, () -> {
            this.accountService.loadUserByUsername("notJoinId@email.com");
        }, "UsernameNotFoundException was expected");
    }
}