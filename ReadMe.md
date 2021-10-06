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



### USE

<HR>

##### method 명명 방법

`List<Member> findByUsernameAndAgeGreaterThan(String username, int age);`

MemberRepository 인터페이스에 이런 메소드가 있다.

findBy 해주고 Username 하면 where에 컨디션이 기본적으로 = (equals)로 들어가고 Age는 age 보다 클경우 즉, age>? 이런식으로 컨디션이 들어가게 된다. 이 메소드에 장점으로는 조건이 추가 되면 메서드 이름에 and , or 등이 붙어 이름이 정말 길어지게 됩니다.

이런 점에서 보았을 때, data jpa에는 짧은 간단한 쿼리들만 이용하는 것이 좋을 것 같다.

```java
em.createQuery("select m from Member m where m.username = :username and m.age > :age")
          .setParameter("username",username)
          .setParameter("age",age)
          .getResultList();
//상기 메서드와 동일한 기능을 하는 엔티티 매니저의 createQuery
```



`List<Member> findHelloBy();`

와 같이 find(설명)By --- by 절 뒤에 아무것도 없으면 다 찾아오는 쿼리를 날린다. (find와 By 사이에는 설명을 위한 문구가 들어오면 된다. <b>생략가능.</b>)



그 외,
 스프링 데이터 JPA가 제공하는 쿼리 메소드 기능
 		**조회**      :  find…By ,read…By ,query…By get…By,
           https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation

   **예:) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다.**
       **COUNT**	: 	count…By 반환타입 long
       **EXISTS**	 : 	exists…By 반환타입 boolean
       **삭제**		 : 	delete…By, remove…By 반환타입 long
       **DISTINCT**: 	findDistinct, findMemberDistinctBy
       **LIMIT** 	  : 	findFirst3, findFirst, findTop, findTop3
               https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.limit-query-result









##### namedQuery

네임드 쿼리를 사용하는 방식은 하단의 코드와 같이 엔티티 클래스 위에 이름과, 쿼리를 정해준다.

이렇게 되면 정적 리소스이기 때문에 기존 em.createQuery와 같이 문자열로 되어 체킹이 힘들 때, 네임드 쿼리로 해주게 되면 어플리케이션 실행시점에 오류를 체크해준다. **즉, 어플리케이션 실행시점에 파싱을 하고 오류를 올려준다는 측면에서는 좋다.**

```
@NamedQuery(name="Member.findByUsername",
            query="select m from Member m where m.username =:username")
```



기존 순수 jpa의 네임드 쿼리 사용 방법.

```
em.createNamedQuery("Member.findByUsername", Member.class)
                  .setParameter("username", username)
                  .getResultList();
```



하지만 기존의 방법을 버리고 data jpa는 `@Query` 어노테이션을 지원한다.

이 어노테이션을 사용하면 인터페이스 위에 네임드 쿼리 이름을 지정해주고 `@Param` 어노테이션을 사용하여 인자를 받아낼 수 있다.

```java
@Query(name = "Member.findByUsername")
List<Member> findByUsername(@Param("username") String username);
```

**@Query 어노테이션이 없어도 동작을 잘 한다.**

```java
List<Member> findByUsername(@Param("username") String username);
```

관례상 findByUsername 이라는 메서드이름과 네임드쿼리를 작성할 때 지정해준 이름으로 매핑하는 것을 우선순위를 하고 다음으로 우선순위를 지나 네임드쿼리가 없으면 쿼리를 만들어준다.











#### data jpa의 named query

```java
@Query("select m from Member m where m.username = :username and m.age = :age")
List<Member> findUser(@Param("username") String username, @Param("age") int age);
```

  네임드 쿼리 대신에 data jpa가 가진 강점이 너무 크다.
  @Query 어노테이션에 쿼리를 적어주게 되고 @Param 을 이용해서 파라미터를 설정하면 인터페이스 호출 만으로 내가 정해준 네임드쿼리를 호출한다.

  **이것도 네임드 쿼리 처럼 오타가 나도 애플리케이션 로딩 시점에 오류를 확인해준다.**





#### DTO로 조회하기

```java
@Query("select m.username from Member m")
List<String> findUsernameList();

@Query("select new study.datajpa.dto.MemberDto(m.id,m.username,t.name) from Member m join m.team t")
List<MemberDto> findMemberDto();
```

- select 쿼리를 대상으로 하는 필드의 경우 문자열로 컬렉션 제네릭을 사용하여 매핑하고

- dto 즉, 객체로 조회하게 될 경우에는 일반 jpa처럼 `new` 오퍼레이션을 사용하여 dto로 타입을 반환합니다.

  이 부분은 테스트 코드로 진행해서 직관적으로 파악해보자.

```java
@Test
public void findMemberDto() throws Exception{
    Team team = new Team("teamA");
    teamRepository.save(team);

    Member m1 = new Member("AAA", 10);
    m1.setTeam(team);
    memberRepository.save(m1);

    List<MemberDto> memberDto = memberRepository.findMemberDto();
    for (MemberDto dto : memberDto) {
        System.out.println("dto = " + dto);
    }
}
```

🎒 테스트코드는 그냥 for 문을 이용하여 dto에 `@Data` 를 이용해서 toString 메서드를 구현하였다.

🎒 실무에 가게된다면 Assertions (JUNIT) 을 이용하여 검증하도록 하자. (지금은 공부하는거니까?...)

​											





							#### + 파라미터 바인딩

```
@Query("select m from Member m where m.username in : names")
List<Member> findByNames(@Param("names") List<String> names);
```





#### Spring data jpa의 유연한 반환타입

Spring data jpa는 유연한 반환타입을 제공한다.

```java
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);
    
```

앞서 말했듯 find와 By 사이의 공간은 comment 설명으로 대체할 수 있는 곳이다. 아무거나 적어줘도 상관없다. 가독성을 고려하여 이해하기 쉬운 comment를 달아줄 수 있도록 하자.



```java
List<Member> result = memberRepository.findListByUsername("AAA");
System.out.println("result.size() = " + result.size());
```

`List<Member> result = memberRepository.findListByUsername("qweqweqweqweqweqwe");`

간혹 이런식으로 오타가 날 경우 jpa 스펙상 List는 빈 리스트를 반환하게 된다. 그리고 이걸 모르고

`if(result != null){}` 메서드로 검증하는 경우가 종종 있다고 한다. 이건 정말 안좋은 코드이다. 스프링을 믿고 빈 리스트를 사용해보자.



```java
Member findMember = memberRepository.findMemberByUsername("AAA");
System.out.println("findMember = " + findMember);
```

이 반환 형태는 없으면 **null**을 반환합니다.

순수 jpa 스펙상에서는 반환형태가 **null** 이면 `uniqueResultException` 을 반환하는데 이러면 **try catch문**으로 검증해야하니까 귀찮지 않을까? 라고도 하긴 하는데

spring data jpa는 자체적으로 값이 없을 때 `noResultException을` 반환한다.
이게 실제 논란이 있기도하다. null을 반환하는게 더 나은 것인지 , exception을 터치는게 맞는것인지에 대해서 ?

하지만 이건 자바8이 나오기 전의 논쟁이고, optional을 적어주게 되면 이게 클라이언트에 대해서 책임을 전가하는 형태가 되어버리기 때문에 orelse나 그런 메서드들을 적어주고 처리를 해주면 된다.



```java
Optional<Member> findOptionalMember = memberRepository.findOptionalByUsername("AAA");
System.out.println("findOptionalMember = " + findOptionalMember);
```

🎒 중요!!

결론은 db에 있을수도있고 없을 수도 있다는 가정하에 optional을 적어주자.
옵셔널은 두개 result가 나오게 되면 예외를 터치는데 db 마다 예외가 다르다. 그렇기 때문에 스프링은 추상화를 해놓은 스프링만의 예외로 변환을 해서 제공.

   * **처리해주는 타입들의 목록**   

      스프링 데이터 JPA 공식 문서: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-return-types