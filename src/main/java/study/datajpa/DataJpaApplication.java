package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.Column;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository")
//스프링 부트 자동 처리
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}


	@Bean
	public AuditorAware<String> auditorProvider(){
		return new AuditorAware<String>() {
			//인터페이스에서 메서드가 하나면 람다로 변환 가능.
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of(UUID.randomUUID().toString());
				//예를들어 스프링시큐리티를 쓰게 되지게 되면 정보를 꺼내서 id값을 넣어주거나? 하면된다.
//				//이렇게 id값을 넘겨주게 되면
//				@CreatedBy
//				@Column(updatable = false)
//				private String createdBy;
//
//				@LastModifiedBy
//				private String lastModifiedBy;

				//이공간에 이렇게 넘겨준 uuid값이 string 형태로 넘어가서 채워지게 된다.
			}
		};
	}
	//return () -> Optional.of(UUID.randomUUID().toString());
}
