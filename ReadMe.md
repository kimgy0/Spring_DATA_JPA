### Spring Data JPA

<HR>

스프링 DATA JPA는 우리가 구현하지 못하는 정말 무수히 많은 메서드들을 구현하고 있다.

`public interface MemberRepository extends JpaRepository<Member, Long>` 라는 JpaRepository로 제네릭에 내가 반환 받고자하는 엔티티 클래스 이름과, 그 엔티티가 PK로 매핑하고 있는 ID타입을 적어주게 되면 모든 것이 끝나게 된다.

```java
/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#findAll()
	 */
	@Override
	List<T> findAll();
```

JpaRepository 인터페이스이다. 어떻게 인터페이스를 구현하지 않았는데도 작동하는 방법이 궁금하다.

그래서 `getClass(); ` 메서드로 찍어보면 `com.sun.proxy.$Proxy107` 라는 클래스로 표현된다. 이는 스프링 부트가 자체적으로 내부에서 구현체를 생산하여 그 구현체를 인젝션하는 방식을 채택한다.

그외에 부가적으로 Jpa의 예외를 <b>스프링 예외로 공통처리를</b>하는 기능과 자동으로 <b>ComponentScan</b> 의 기능을 가지고 있다.

재미있는 것은 더 파고들면 JpaRepository 클래스는 `package org.springframework.data.jpa.repository;` 라는 패키지에서 나오고 있다. 

더 들어가보면 ` PagingAndSortingRepository<T, ID>` 이를 상속받는데 이거는 `package org.springframework.data.repository; ` 이쪽 패키지에 매핑이 되어있다.  data.jpa 와 data 의 차이에서 알 수 있었다. 이거는 REDIS나 MONGODB 등과 같이 커스텀적으로 공통으로 하는 인터페이스들과 JPA에 특화된 JPA인터페이스라는 것이다.

라이브러리 역시 다르다. Gradle: org.springframework.data:spring-data-commons:version 에 data 패키지와 같은 공통인터페이스들을 가지고 있으며 jpa는 spring-jpa 라이브러리에 묶여있다.

실무에서 프로젝트의 db를 바꾼다는 것은 상당히 까다롭고 어렵고 번거로운 일이다. 그래서 이를 db기준으로 볼게 아니라 그냥 <b>jpa의 사용을 더 편리하게 해준다는 점</b>에서 봐야할 것 같다.