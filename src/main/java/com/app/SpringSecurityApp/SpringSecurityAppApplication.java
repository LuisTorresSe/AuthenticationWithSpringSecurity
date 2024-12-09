package com.app.SpringSecurityApp;

import com.app.SpringSecurityApp.persistence.entity.PermissionEntity;
import com.app.SpringSecurityApp.persistence.entity.RoleEntity;
import com.app.SpringSecurityApp.persistence.entity.RoleEnum;
import com.app.SpringSecurityApp.persistence.entity.UserEntity;
import com.app.SpringSecurityApp.persistence.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@SpringBootApplication
public class SpringSecurityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityAppApplication.class, args);
	}

@Bean
	CommandLineRunner init(UserRepository userRepository) {
		return args -> {
			PermissionEntity createPermision = PermissionEntity
					.builder().permission("create").build();

			PermissionEntity readPermission = PermissionEntity.builder().permission("read").build();

			RoleEntity roleAdmin = RoleEntity.builder().role(RoleEnum.ADMIN)
					.permissions(Set.of(createPermision, readPermission)).build();

			RoleEntity roleUser = RoleEntity.builder().role(RoleEnum.USER)
					.permissions(Set.of(readPermission)).build();

			UserEntity davidAdmin = UserEntity.builder()
					.email("davidAdmin")
					.password("password")
					.isAccountNonExpired(true)
					.isAccountNonLocked(true)
					.isCredentialsNonExpired(true)
					.isEnabled(true)
					.roles(Set.of(roleAdmin))
					.build();
			UserEntity claudiaUser = UserEntity.builder()
					.email("claudiaUser")
					.password("password")
					.isAccountNonExpired(true)
					.isAccountNonLocked(true)
					.isCredentialsNonExpired(true)
					.isEnabled(true)
					.roles(Set.of(roleUser))
					.build();

			userRepository.saveAll(List.of(davidAdmin, claudiaUser));
		};
	}
}
