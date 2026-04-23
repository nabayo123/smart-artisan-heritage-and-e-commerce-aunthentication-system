package com.korarwanda.kora.security;

import com.korarwanda.kora.entity.Admin;
import com.korarwanda.kora.entity.Artisan;
import com.korarwanda.kora.entity.Customer;
import com.korarwanda.kora.repository.AdminRepository;
import com.korarwanda.kora.repository.ArtisanRepository;
import com.korarwanda.kora.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ArtisanRepository artisanRepository;
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Check Admin FIRST — only ACTIVE admins can authenticate
        Optional<Admin> admin = adminRepository.findByEmailAndActiveTrue(email);
        if (admin.isPresent()) {
            Admin adm = admin.get();
            return new User(adm.getEmail(), adm.getPassword(),
                    List.of(new SimpleGrantedAuthority(adm.getRole().name())));
        }

        // 2. Check Artisan
        Optional<Artisan> artisan = artisanRepository.findByEmail(email);
        if (artisan.isPresent()) {
            Artisan a = artisan.get();
            return new User(a.getEmail(), a.getPassword(),
                    List.of(new SimpleGrantedAuthority(a.getRole().name())));
        }

        // 3. Check Customer
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            Customer c = customer.get();
            return new User(c.getEmail(), c.getPassword(),
                    List.of(new SimpleGrantedAuthority(c.getRole().name())));
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
    public UserDetails loadUserByEmailAndRole(String email, String role) throws UsernameNotFoundException {
        // 1. ALWAYS prioritize Admin privileges if an account exists
        Optional<Admin> admin = adminRepository.findByEmailAndActiveTrue(email);
        if (admin.isPresent()) {
            Admin adm = admin.get();
            return new User(adm.getEmail(), adm.getPassword(), List.of(new SimpleGrantedAuthority(adm.getRole().name())));
        }

        // 2. Fallback to the role specified in the token
        if ("ROLE_ARTISAN".equals(role)) {
            Artisan a = artisanRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Artisan not found"));
            return new User(a.getEmail(), a.getPassword(), List.of(new SimpleGrantedAuthority(a.getRole().name())));
        } else if ("ROLE_CUSTOMER".equals(role)) {
            Customer c = customerRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
            return new User(c.getEmail(), c.getPassword(), List.of(new SimpleGrantedAuthority(c.getRole().name())));
        }
        return loadUserByUsername(email);
    }
}
