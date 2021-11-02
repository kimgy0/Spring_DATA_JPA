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





<br>

<br>

<br>

<br>

### PAGING

<HR>

```java
public List<Member> findByPage(int age, int offset,int limit){
        return em.createQuery("select m from Member m where m.age =:age order by m.username desc" )
                .setParameter("age",age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

public long totalCount(int age){
        return em.createQuery("select count(m) from Member m where m.age =:age", Long.class)
                .setParameter("age",age)
                .getSingleResult();
    }

```

페이징을 간단하게 하면 이런식으로 할 수 있을 것이다. `select m from Member m where m.age =:age order by m.username desc` 이런 식으로 쿼리를 날려서 `Member.class` 객체를 다 가져온 다음, offset과 limit을 받아서 데이터를 가져오고 페이징 쿼리를 날리기 위해 `count()` 함수를 이용해서 계산하려고 할 것이다.

하지만 Jpa에서 지원하는 기능을 알아버렸다. 저번 예약시스템을 프로젝트로 만들던 도중 페이징 쿼리를 네이티브코드로 짜느라 애좀 먹었고 페이징도 `@Bean`으로 만들어서 로직을 짰었는데 이제는 그 수고가 덜어질 것 같다.

- ```java
  @Query(value = "select m from Member m left join m.team t" , countQuery = "select count(m.username) from Member m")
  ```

- ```java
  Page<Member> findByAge(int age, Pageable pageable);
  ```

  일단 이를 적용시키기 위해 <b>Repository</b> 클래스에 이 두줄을 추가 해줬습니다.



​		그리고 테스트 코드를 작성했습니다.

​		지금부터 이 코드들에 대해서 설명하겠습니다.

```java
@Test
    public void paging() throws Exception{
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age=10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.isFirst()).isTrue();
        Assertions.assertThat(page.hasNext()).isTrue();
    }
```

스프링은 우리가 기존에 `RowNum` 등을 이용해서 네이티브쿼리를 짜거나, 직접 `count()` 함수를 이용해서 전체를 카운트 하여 페이징을 계산했었는데,

스프링은 이런 기능들은 공통으로 인터페이스화 해서 우리에게 기능을 제공하는데, 이렇게 네가지 또한 추가로 설명하려 합니다.

```java
org.springframework.data.domain.Sort
org.springframework.data.domain.Pageable
org.springframework.data.domain.Page
org.springframework.data.domain.Slice
```

우선 인터페이스이다.  `Page<Member> findByAge(int age, Pageable pageable);` 이 코드에서 인자로 들어간 `Pagable` 클래스는 현재 내가 1페이지 인지 2페이지 인지에 대한 현재 상태를 나타낸다.

그래서 테스트코드에서

```java
PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
```

`Pagable` 인자로 `PageRequest` 객체를 만들어줬는데 이는 클래스 안에 들어가보면 부모 자식 관계로 상속하고 있습니다. 자세한 내용은 생략하겠습니다.

여기서 JPA-DATA는 0을 베이스 인덱스로 하는데 첫번째 0부터 3번째 데이터를 username을 기준으로 정렬하라는 의미입니다. <b>(Sort는 생략가능)</b> ,이렇게 페이지 정보를 지정해서 인자로 주게 되면 <b>Page<Member></b> 라는 반환형으로 받게 되는데 이 Page 객체는 우리가 findByAge 함수로 찾아온 결과 값과, 페이징 정보를 포함하고 있습니다. 여기서 페이징 쿼리를 하기 위해  이 인터페이스를 사용하는 순간

*select*<br>
        *count(member0.member_id) as col_0_0*<br> 
    *from*<br>
        *member member0_*<br> 
    *where*<br>
        member0_.age=?*<br>

이런 식으로 `count` 함수를  사용한 쿼리를 날리게 됩니다.<br>

🎒 <b>나중에 쿼리가 복잡해질 경우 sort를 하는데도 문제가 생길 수 있다. 그러면 과감하게 3번째 인자를 지워주고 @Query 안에 Join을 하면서 컨디션을 적는 방법을 선택해도 괜찮을 것 같다.</b>

<br>

##### PageMethod

```java
List<Member> content = page.getContent();
long totalElements = page.getTotalElements();
```

이렇게 `getContent()` 함수와 `getTotalElements();` 함수를 쓰게 되면 결과에 해당하는 `Member.class` 의 아이들과 `count()` 함수로 쿼리가 나간 결과값을 5라고 반환하게 됩니다.

*member = Member(id=5, username=member5, age=10)*<br>
*member = Member(id=4, username=member4, age=10)*<br>
*member = Member(id=3, username=member3, age=10)*<br>
*totalElements = 5*<br>

그 외,

`Assertions.assertThat(page.getTotalElements()).isEqualTo(5)` : 전체 count 갯수<br>

`Assertions.assertThat(page.getNumber()).isEqualTo(0)` : 현재 내가 어느 페이지인지<br>

`Assertions.assertThat(page.getTotalPages()).isEqualTo(2)`: 0-3 페이지를 했기 때문에 총 페이지가 몇페이지로 이루어져있는지<br>
`Assertions.assertThat(page.isFirst()).isTrue();`: 첫번째 페이지 인지?<br>

`Assertions.assertThat(page.hasNext()).isTrue();`: 다음 페이지가 존재하는지?<br>



```java
Page<Member> page = memberRepository.findByAge(age, pageRequest);
```

굳이 페이지 객체가 반환형이 아니여도 된다, 바로 List<Member>로 불러와도 상관 없다.

```java
Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
```

🎒 참고로 map 함수는 api 서버에서 엔티티를 외부로 노출시키지 않고 사용하는 방법이다, 꼭 기억하자.

<br>

페이징의 마지막으로 복잡한 쿼리가 생겼을 경우에 불러오는 방법이다. 쿼리가 복잡해지면 (Join) Jpa 스펙상 join을 치면서 내부적으로 count를 실행한다. 이거는 오히려 내부에서 데이터가 많아질 경우 성능상의 이슈를 불러오기 쉬울 수 있다.

그렇기 때문에 해당 count와 해당 데이터만 페이징해서 불러오는 쿼리를 따로 분리시켜줄 필요가 있을 때,

```java
@Query(value = "select m from Member m left join m.team t" , countQuery = "select count(m.username) from Member m")
```

paging 단위 위에 적어준 어노테이션의 정체가 바로 이것이다.

**쿼리가 좀 복잡하고 느릴때는 성능 테스트를 해보면서 쿼리를 분리해줄 수 있는 개발자가 될 수 있도록 하자.**

<br>

<br>

<br>

<br>

### 벌크성 쿼리

<hr>

```java
public int bulkAgePlus(int age){
        return em.createQuery("update Member m set m.age = m.age+1 where m.age >= :age")
                .setParameter("age",age)
                .executeUpdate();
}
```

이 쿼리는 모든 멤버의 age를 조건 where절에 맞추어서 나이를 +1을 해주는 쿼리이다.

벌크성 쿼리는 벌크 연산 특성상 `.executeUpdate()` 를 날려주면서 1차 캐시 즉, 영속성 컨텍스트와 동기화 되지 않고 DB에만 업데이트를 치고 영속성 컨텍스는 동기화하지 않는다. 그래서 저 쿼리를 날리더라도 영속성 컨텍스트에 있는 DATA들을 꺼내어 보면 업데이트 되지 않은 상태를 유지한다.

이 연산을 `em.flush();` 나 `em.clear();` 를 통해 영속성 컨텍스를 비워주고 쿼리를 날려준다.

*<u>**em.flush를 쳐주는 이유는 반영되지 않은 데이터들에 대해서 db에 반영시키는 작업.**</u>*



하지만 이 작업은 수동으로 하기 힘든 작업이다. 벌크 연산을 할 때 어노테이션을 SPRING DATA가 지원한다.

```java
@Modifying
@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
int bulkAgePlus(@Param("age") int age);
```

이런식으로 업데이트 쿼리를 적어주게 되면 저 쿼리를 날리면서 `.executeUpdate`를 날려야하는데 `@Modifying` 어노테이션이 없으면 `.getResultList` 같은 엉뚱한 메서드가 나가게 돼서 오류가 발생하게 됩니다.

```java
@Modifiying(clear = true)
```

그리고 옵션중에 clear를 true 값으로 주게 되면 자동으로 `em.clear()` 효과와 같이 볼 수 있다.

<br>

<br>

<br>

<br>

### Lazy Loading

<hr>

```java
@Test
public void findMemberLazy() {
    //member1 -> teamA를 참조하고 있을 때.
    //member2 -> teamB를 참조하고 있을 때.

    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);
    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 10, teamB);
    memberRepository.save(member1);
    memberRepository.save(member2);

    em.flush();
    em.clear();

    List<Member> members = memberRepository.findAll();
}
```

지연로딩이란 ?

Member 객체와 Team객체가 연관이 되어있을 때,  Member 객체를 불러오면 Member 객체 안에 있는 Team은 우리가 Team 속성을 직접적으로 사용하기 전까지 Proxy 객체로 채워져 있다가 우리가 실제 값을 호출하게 되면 Proxy객체가 아닌 진짜 Team 객체를 불러오기 위해 select 쿼리가 나가는 것을 말합니다.

그러면 쿼리가 한줄 더 나가게 되고 우리는 이것을 N+1 문제라고 하는데 (1 = member를 select하는 쿼리 1줄.) , (N = 그와 연관된 값을 가져오는 쿼리) 이게 나중에 커지게 되면 심각한 성능 상 문제를 초래할 수 있습니다.

***<u>+추가적인 내용들은 지연로딩에 대해서 다시 한 번 볼 기회가 있으면 그 때 자세한 내용을 다루려고 합니다.</u>***

이를 위한 해결 방법은,

1.

```java
@Query("select m from Member m fetch join m.team") //연관된 team을 한방쿼리로 다 긁어옴.
List<Member> findMemberFetchJoin();
```

2.

**하지만 fetch join을 할거면 항상 @Query를 적어줘야 한다.**

**반복을 없애기 위해서 jpa 메서드 이름과 최소한의 작업으로 fetch join을 하고싶을 때,**

```java
@EntityGraph(attributePaths = {"team"})//엔티티 속성이름.
List<Member> findAll();
```

3.

```java
@Query("select m from Member m")
@EntityGraph(attributePaths = {"team"}) //엔티티 속성이름.
List<Member> findMemberEntityGraph();
```

4.이름으로 정의하는 방법.

```java
@EntityGraph(attributePaths = {"team"}) //엔티티 속성이름.
List<Member> findEntityGraphByUsername(@Param("username) String username);
```

5.

엔티티 `Member.class`

```java
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
...
public class Member{
		...
}
```

이런식으로 클래스 위에 적어주고,

```java
@EntityGraph("Member.all")
List<Member> findEntityGraphByUsername(@Param("username) String username);
```

이런식으로 그래프를 적어주는 것도 가능하다.

사실 지연로딩의 개념만 알면 되는 내용이고, 지연로딩에 대해서 숙지 햇을 시 이런 어노테이션들은 그냥 이런게 있구나 하고 넘어갈 수 있을 것 같다.

<br>

<br>

<br>

<br>

### 변경 감지

<hr>

```java
    @Test
    public void queryHint() throws Exception{
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();


        Member findMember = memberRepository.findById(member1.getId()).get();
        //findMember.setUsername("member2");
        //em.flush();
       
        Member findByMember = memberRepository.findReadOnlyByUsername("member1");
        findByMember.setUsername("member2");
        em.flush();

    }
```

여기서도 테스트 코드에 대해서 차례로 설명하겠습니다.

```java
Member findMember = memberRepository.findById(member1.getId()).get();
        //findMember.setUsername("member2");
        //em.flush();
```

findById 를 이용해 객체를 꺼내고 엔티티 객체에 대해서 수정을 해주면 flush를 하는 순간 update 쿼리가 나가게 됩니다.

이런식으로 변경감지 기능을 사용하게 될 때는 무조건 원본이 있어야 합니다.  거의 객체 두개를 감시하는 꼴이 됩니다. <b>(원본, 바뀐객체 )</b>, 어쨌거나 바뀐걸 알려면 원본이 있어야 비교를 하기 때문에 어떻게든 메모리를 더 쓰게 됩니다. 즉, 가지고 오는 순간 이미 원본을 만들어 놓고 시작하기 때문입니다.

만약, 변경안하고 findMember 해서 db에서 단순히 조회만 하고 변경안할거야 라고 한다면, 또는 나는 100퍼 이거를 조회용으로만 쓸거야 라고 한다면 그 방법이 있습니다.

원본을 아예 만들지 않는 방법인데 이것은 jpa 표준 스펙이 아니고 하이버네이트의 기능을 필요로 합니다.

여기서 `@QueryHints` 어노테이션의 필요성을 느꼈습니다. 이는 jpa보다 더 많은 기능을 사용하기 위해 sql쿼리에 힌트를 주는 것이 아니라 jpa에게 다른 기능을 더 쓰고 싶으니 이렇게 해줘라 라고 하는 힌트를 던져주는 것과 같습니다.

```java
@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
Member findReadOnlyByUsername(String name);
```

이런 식으로 적어주게 되면,

```java
Member findByMember = memberRepository.findReadOnlyByUsername("member1");
findByMember.setUsername("member2");
em.flush();
```

이런식으로 적어주게 되면 queryhint readonly를 true로 줘버리는 순간 위에서 언급한 원본 data를 만들지 않게 됩니다.







<h4>사용자 확장 기능</h4>

<hr>

- **사용자 정의 리포지토리**

  JPA의 직접사용과 스프링 JDBC 템플릿 사용, Connection을 직접 사용해야 할 경우, QueryDsl 을 사용해야할 경우 등등 다양한 이유에서 직접 구현해야할 일이 있을 때 구현하는 방법이다.

  ```java
  public interface MemberRepositoryCustom {
      List<Member> findMemberCustom();
  }
  ```

  위와 같이 만들고자 하는 인터페이스를 생성하고

  ```java
  @RequiredArgsConstructor
  public class MemberRepositoryImpl implements MemberRepositoryCustom{
  
      @PersistenceContext
      private final EntityManager em;
  
      @Override
      public List<Member> findMemberCustom() {
          return em.createQuery("select m from Member m").getResultList();
      }
  }
  ```

  그리고 그 인터페이스를 구현하여 <b>네이티브 쿼리</b> 등 커넥션을 얻어오거나 다양한 활동을 해준다.

  이 코드에서는 JPA를 직접 다루기 위해서 `@PersistenceContext` 를 이용해 em객체를 얻어왔다.

  ```java
  public interface MemberRepository
      extends JpaRepository<Member, Long> , MemberRepositoryCustom{
  	.
  	.
  	.
  	.
  }
  ```

  그리고 JPA인터페이스를 구현해놓은 인터페이스로 가서 

  ​	-> 여기서 MemberRepositoryCustom 인터페이스를 인터페이스 to 인터페이스로 상속해주어야 한다.

  이렇게 되면 스프링 자체에서 MemberRepositoryCustom을 구현한 클래스의 기능을 이어준다.(?)

  🎒  이게 자바 자체의 기능은 아니고 스프링 자체에서 하는 기능이다.

  🎒  여기서 **규칙!**

    1. 구현되어지는 인터페이스는 이름을 아무거나 가져도 상관이 없지만, MemberRepository에 상속되어지는 그 **인터페이스** 를 구현한 클래스의 이름은 JPA를 구현한 인터페이스 이름 + **Impl** 이 되어야만 한다.

    2. Impl 규칙을 굳이 바꾸고 싶다면.

         1. ```java
            @EnableJpaRepositories
            (basePackages = "study.datajpa.repository",repositoryImplementationPostfix = "Impl")
            ```

            위의 방법은 어노테이션 컨피그변경 방법

       		2. ```xml
            <repositories base-package="study.datajpa.repository"
             repository-impl-postfix="Impl" />
            ```

            xml로 설정시의 방법을 통해 규칙을 바꿔줄 수 있다.





- **Auditing (엔티티를 변경, 생성, 생성자, 수정자를 주기)**

  기존 순수 JPA 방식

  ```java
  @Getter
  @Setter
  @MappedSuperclass
  public class JpaBaseEntity {
  
      @Column(updatable = false, insertable = false)
      private LocalDateTime createdDate;
      private LocalDateTime updatedDate;
  
      @PrePersist
      public void prePersist(){
          LocalDateTime now = LocalDateTime.now();
          this.createdDate = now;
          this.updatedDate = now;
      }
  
      @PreUpdate
      public void preUpdate(){
          this.updatedDate = LocalDateTime.now();
      }
  }
  ```

  - **@PreUpdate** : UPDATE 발생시 (변경 감지) 실행되는 메서드.

  - **@PrePersist** : PERSIST 를 진행하기 전에 발생하는 메서드.

  - @PostUpdate , @PostPersist 어노테이션 등등이 있다.

    

  그리고 이 클래스를 `@MappedSuperclass` 어노테이션을 사용해서 속성만 내려쓰는 상속관계를 이용했다.

  ```java
  @Test
  public void jpaEventBaseEntity() throws Exception{
       //given
       Member member = new Member("member1");
       memberRepository.save(member); //@PrePersist가 발생!
       Thread.sleep(100);
       
       member.setUsername("member2");
       em.flush(); //@PreUpdate가 실행
       em.clear();
       //when
       Member findMember = memberRepository.findById(member.getId()).get();
       
       //then
       System.out.println("findMember.getCreatedDate = " + findMember.getCreatedDate());
       System.out.println("findMember.getUpdatedDate = " + findMember.getUpdatedDate());
  }
       
  ```

  위의 테스트코드를 이용해서 업데이트 된 날짜와 만들어진 날자를 

  `System.out.println("findMember.getCreatedDate = " + findMember.getCreatedDate());`

  `System.out.println("findMember.getUpdatedDate = " + findMember.getUpdatedDate());`

  이 두문장으로 확인했을 때 약 100ms 정도의 차이를 보였다.

  

  

  *위와 같은 방식을 쓰지 말고 Auditing을 제대로 써보자.*

  ```java
  @EnableJpaAuditing
  @SpringBootApplication
  //@EnableJpaRepositories(basePackages = "study.datajpa.repository")
  //스프링 부트 자동 처리
  public class DataJpaApplication {
  
  	public static void main(String[] args) {
  		SpringApplication.run(DataJpaApplication.class, args);
  	}
  ```

  **@EnableJpaAuditing** 어노테이션을 이런식으로 등록 해준다. (활성화 하겠다는 뜻.)

  ```java
  @EntityListeners(AuditingEntityListener.class)
  @MappedSuperclass
  @Getter
  public class BaseEntity {
  
      @CreatedDate
      @Column(updatable = false)
      private LocalDateTime createdDate;
  
      @LastModifiedDate
      private LocalDateTime lastModifiedDate;
  
      @CreatedBy
      @Column(updatable = false)
      private String createdBy;
  
      @LastModifiedBy
      private String lastModifiedBy;
  }
  ```

  `@EntityListener(AuditingEntityListener.class)` 로 일단 이벤트 기반으로 동작하는 것을 알려주는 설정을 입력하는 어노테이션이라고 한다. 그리고 구현하고자 하는 클래스 위에 어노테이션을 붙여주고

  ```java
  @Bean
  public AuditorAware<String> auditorProvider(){
        return new AuditorAware<String>() {
           @Override
           public Optional<String> getCurrentAuditor() {
              return Optional.of(UUID.randomUUID().toString());
           }
        };
     }
     //return () -> Optional.of(UUID.randomUUID().toString());
  }
  ```

  이런식으로 빈등록을 해주어야 한다. AuditorAware<String> 반환 형식으로 인터페이스를 inner class로 구현해주었다. getCurrentAuditor() 메서드를 구현했으며 반환값으로 랜덤UUID를 넘겼다.

  `return () -> Optional.of(UUID.randomUUID().toString());` 이 코드같이 인터페이스가 하나이면 람다식으로 구현 가능하다는 것도 잊지말자. 자세히 보고자 풀어서 이너클래스로 구현하였다.

  여기서 UUID를 넘겨준 이유가 무었이냐면 

  ```java
  @CreatedBy
  @Column(updatable = false)
  private String createdBy;
  
  @LastModifiedBy
  private String lastModifiedBy;
  ```

  이 두 필드 때문이다.

  **@---By**로 인해서 그 해당 컬럼에 uuid를 넘겨주는 것이다.

  나중에 **시큐리티를 배우면** 시큐리티홀더 세션 자체에 id값이나 member정보를 꺼내어 와서 이쪽으로 반환하는 방법도 좋은 방법이다.

  여기서 내가 구현한 것처럼 4개속성을 다 갖다가 한클래스에 배치하는 방법도 있지만 클래스마다 속성이 다르기때문에 @by 어노테이션이 필요한경우도 있고 없는 경우도 있다. 그럴때는 필드만 나누어서 클래스를 따로 구현하고 상속관계로서 매핑을 해주자.

  

- **도메인 클래스 컨버터**

  ```java
  @RestController
  @RequiredArgsConstructor
  public class MemberController {
      private final MemberRepository memberRepository;
  
      @GetMapping("/members/{id}")
      public String findMember(@PathVariable("id") Long id){
          Member member = memberRepository.findById(id).get();
          return member.getUsername();
      }
  
      @GetMapping("/members2/{id}")
      public String findMember(@PathVariable("id") Member member){
          return member.getUsername();
      }
  }
  ```

- **웹에서의** **Pageable**

  위 코드를 이어서 씀.

  ```java
  @PostConstruct
  public void init(){
  	memberRepository.save(new Member("userA"));
      for(int i =0; i<100; i++){
          memberRepository.save(new Member("user"+i));
      }
  }
  ```

  우선 테스트 샘플 100개를 준비하고

  ```java
  @GetMapping("/members")
  public Page<Member> list(Pageable pageable){
      return memberRepository.findAll(pageable);
  }
  ```

  Pageable 객체를 빈으로 받아서 findAll메서드를 받는다.

  인자가 pageable 객체를 받는 메서드나 인터페이스를 구현하지 않았는데 있는 이유는

  `public interface JpaRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T>`

  jparepository에 `PagingAndSortingRepository<T, ID>` 이쪽으로 들어가게 되면,

  ```java
  @NoRepositoryBean
  public interface PagingAndSortingRepository<T, ID> extends CrudRepository<T, ID> {
  
  	/**
  	 * Returns all entities sorted by the given options.
  	 *
  	 * @param sort
  	 * @return all entities sorted by the given options
  	 */
  	Iterable<T> findAll(Sort sort);
  
  	/**
  	 * Returns a {@link Page} of entities meeting the paging restriction provided in the {@code Pageable} object.
  	 *
  	 * @param pageable
  	 * @return a page of entities
  	 */
  	Page<T> findAll(Pageable pageable);
  }
  
  ```

  이렇게 스프링 코드 자체에서 구현을 해놓았기 때문이다.

  `Page<T> findAll(Pageable pageable);` 를 이용해 페이징을 할 때

  `@GetMapping("/members")` 이 경로로 들어가게 되면 보통은 100개의 정보를 다 반환하지만

  - http://localhost:8080/members?page=0 : 기본 20개씩 데이터를 불러오고
  - http://localhost:8080/members?page=1 : pk가 21인 것부터 또 불러오게 된다. (20개를)
  - http://localhost:8080/members?page=0&size=3 : size로 인해서 pk가 1,2,3
  - http://localhost:8080/members?page=1&size=3 : page와 size로 인해서 pk가 4,5,6 인 멤버를 불러옴.
  - http://localhost:8080/members?page=0&size=3&sort=id,desc : 이런식으로 정렬도 되고&sort=username,desc 이런식으로 이어서 써도된다.

  하지만 위를 보면 기본 20개씩 불러오는 디폴트 설정을 바꾸고 싶으면

  ```yaml
  data:
  	web:
  		pageable:
  			default-page-size: 10
              max-page-size: 2000
  ```

  ```java
  public Page<Member> list(@PageableDefault(size=5, sort="username") Pageable pageable)
  ```

  위의 yaml에서 바꿔주는 것과 자바코드로 바꿔주는 @PageableDefault 어노테이션이 있다.

  🎒 여기서 중요한 점은 엔티티를 그대로 외부로 유출하면 안된다는 점이다. 그렇기 떄문에 map함수로 DTO로 변환해서 바꿔주는 방법을 고려하자.

  🎒 `Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));`

  🎒 0인덱스가 프리미티브 값으로 맘에 들지 않을 떄는 기본 Pageable객체를 쓰지 말고 pageRequest.of(1,2,sort --- ) 썼던 방법으로 

  `PageRequest pageRequest = PageRequest.of(0, 3,Sort.by(Sort.Direction.DESC, "username"));`

  JPA인터페이스를 구현하여서 사용하자.

  또, yaml에서 바꿔주는 방법이 있는데

  ```yaml
  ata:
  	web:
  		pageable:
           one-indexed-parameters: true
  ```

  위 방법과 같이 바꿔줘도 되지만 json 불일치 문제가 발생기하기도 한다.

  권장하지는 않는 방법이다.