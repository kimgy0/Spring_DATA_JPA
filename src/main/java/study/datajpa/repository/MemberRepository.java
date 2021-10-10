package study.datajpa.repository;

import org.hibernate.LockMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    /**
     * 구현체가 없는데 어떻게 동작을 하지?
     *
     * 클래스를 getClass 를 찍어봤을 때 com.sun.proxy.$Proxy107
     * 프록시 객체를 띄는데 인터페이스 구현체를 개발자가 직접 작성한게 아니라
     * 스프링 자체에서 구현체를 만들어서 proxy로 injection을 해서
     * 사용자가 spring data jpa를 사용할 수 있게한다.
     *
     * JpaRepository<Team, Long>
     * 이렇게 제네릭에 엔티티 타입고 pk 타입을 적어주면 구현체를 스프링이 만들어줌.
     * 한가지 기능이 더있는데 jpa의 예외를 스프링 예외로 공통 처리 하는 기능과 componentscan 같이 기본적으로 동작하게 하는 기능도 존재한다.
     */


    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /*
     * findBy 해주고 Username 하면 where에 컨디션이 기본적으로 = (equals)로 들어가고
     *  Age는 age 보다 클경우 즉, age>? 이런식으로 컨디션이 들어가게 된다.
     *
     * 단점은 조건이 추가되면 메서드 이름에 and or 등이 붙어 이름이 길어지게 됨.
     *
     * 쿼리 em.createQuery("select m from Member m where m.username = :username and m.age > :age")
     *           .setParameter("username",username)
     *           .setParameter("age",age)
     *           .getResultList();
     *
     *   쿼리 메소드 필터 조건
     *   스프링 데이터 JPA 공식 문서 참고: (https://docs.spring.io/spring-data/jpa/docs/current/
     *   reference/html/#jpa.query-methods.query-creation
     */


    List<Member> findHelloBy();
    /*
     * find(설명)By --- by 절 뒤에 아무것도 없으면 다 찾아오는 쿼리를 날린다.
     *
     * dataJpa 는 짤막짤막한 쿼리들을 날릴 때 쓴다.
     * 하지만 복잡한 문제에 직면했을 때 NameQuery 등 다른 방법등을 쓰자.
     *
     * 그 외,
     * 스프링 데이터 JPA가 제공하는 쿼리 메소드 기능
     *   조회: find…By ,read…By ,query…By get…By,
     *           https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
     *               #repositories.query-methods.query-creation
     *   예:) findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다.
     *       COUNT: count…By 반환타입 long
     *       EXISTS: exists…By 반환타입 boolean
     *       삭제: delete…By, remove…By 반환타입 long
     *       DISTINCT: findDistinct, findMemberDistinctBy
     *       LIMIT: findFirst3, findFirst, findTop, findTop3
     *               https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
     *               #repositories.limit-query-result
     * */

    @Query(name = "Member.findByUsername")
    //@Query 어노테이션이 없어도 동작을 잘 한다.
    // 관례상 findByUsername 이라는 메서드이름과 네임드쿼리를 작성할 때 지정해준 이름으로 매핑하는 것을 우선순위를 하고
    // 다음으로 우선순위를 지나 네임드쿼리가 없으면 쿼리를 만들어준다.
    List<Member> findByUsername(@Param("username") String username);
    /*
     * JPA는 NamedQuery를 더 쉽게 호출할 수 있게 도와준다.
     * 또 네임드 쿼리가 가지고 있는 큰 장점 -> em.createQuery 메서드로 변수 네임에 오타가 나면 문자 자체이기 때문에
     *                                  문법오류가 나면 이 기능을 쓰기 전까지는 아직 모름.
     *                                  하지만 네임드쿼리는 오류가나면 어플리케이션 로딩 시점에 쿼리를 다 파싱을 함
     *                                  파싱을 할 수 있는 이유는 정적인 것이라서 바로 파싱 쌉가능 ㄱㄱ 오류 한번에 캐치
     *
     * em.createNamedQuery("Member.findByUsername", Member.class)
     *           .setParameter("username", username)
     *           .getResultList();
     * @Param("속성 이름") String username 으로 명확하게 인자로 넘겨주면 자동으로 매핑해서 쿼리를 실행합니다.
     */

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);
    /*
     * 네임드 쿼리 대신에 data jpa가 가진 강점이 너무 크다.
     * @Query 어노테이션에 쿼리를 적어주게 되고 @Param 을 이용해서 파라미터를 설정하면
     * 인터페이스 호출 만으로 내가 정해준 네임드쿼리를 호출한다.
     *
     * 이것도 네임드 쿼리 처럼 오타가 나도 애플리케이션 로딩 시점에 오류를 확인해준다.
     */

    //DTO로 조회하기
    @Query("select m.username from Member m")
    List<String> findUsernameList();
    /*
     * String 타입으로 username 과 타입을 맞춰주면 반환이 된다.
     */

    @Query("select new study.datajpa.dto.MemberDto(m.id,m.username,t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //파라미터 바인딩
    @Query("select m from Member m where m.username in : names")
    List<Member> findByNames(@Param("names") List<String> names);



    //스프링 data jpa는 유연한 반환타입을 제공한다.
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);
    /*
     * find와 By 사이에는 어떠한 설명이 와도 상관이 없다.
     *
     */
//    List<Member> result = memberRepository.findListByUsername("AAA");
//        System.out.println("result.size() = " + result.size());
    /*
     * 만약 매칭이 되지 않으면 빈 컬렉션을 제공한다.
     * 그래서 간혹 if(result == null) 이런 코드를 작성하는데 안좋은 코드이다.
     */
//    Member findMember = memberRepository.findMemberByUsername("AAA");
//        System.out.println("findMember = " + findMember);
    /*
     * 이 반환 형태는 없으면 null을 반환합니다.
     *
     * 순수 jpa 스펙상에서는 반환형태가 null 이면 uniqueResultException 을 반환하는데
     * 이러면 try catch문으로 검증해야하니까 귀찮지 않을까? 라고도 하긴 하는데
     *
     * spring data jpa는 자체적으로 값이 없을 때 noResultException을 반환한다.
     * 이게 실제 논란이 있기도하다. null을 반환하는게 더 나은 것인지 , exception을 터치는게 맞는것인지에 대해
     *
     * 하지만 이건 자바8이 나오기 전의 논쟁이고, optional을 적어주게 되면 이게 클라이언트에 대해서 책임을 전가하는 형태가
     * 되어버리기 때문에 orelse나 그런 메서드들을 적어주고 처리를 해주면 된다.
     *
     * 결론은 db에 있을수도있고 없을 수도 있다는 가정하에 optional을 적어주자.
     *
     * !!중요!!
     * 옵셔널은 두개 result가 나오게 되면 예외를 터치는데 db 마다 예외가 다르다. 그렇기 때문에
     * 스프링은 추상화를 해놓은 스프링만의 예외로 변환을 해서 제공.
     *
     * 처리해주는 타입들의 목록록     * 스프링 데이터 JPA 공식 문서: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-return-types
     *
     */

//    Optional<Member> findOptionalMember = memberRepository.findOptionalByUsername("AAA");
//        System.out.println("findOptionalMember = " + findOptionalMember);









    @Query(value = "select m from Member m left join m.team t" , countQuery = "select count(m.username) from Member m")
    /*
     * 성능상 join을 해오면서 별다른 where 절이 없다고 가정을 했을 때,
     * count까지 같이 가지고 오려함.
     *
     * 이거는 오히려 내부에서 count 자체도 같이 해오기 때문에 데이터가 많아질 경우 성능상의 이슈를 불러올 수 있음.
     * 그렇기 때문에 count와 해당 데이터만 페이징해서 불러오는 쿼리를 따로 분리시켜줄 필요가 있기 때문에 이런식으로 분리를 해줄 수 있다.
     *
     * 쿼리가 좀 복잡하고 느릴때는 카운트 쿼리를 분리해줄 수 있는 개발자가 되자.
     */
    Page<Member> findByAge(int age, Pageable pageable);
    /*
     * 쿼리에 대한 조건 : Pageable 은 현재 내가 1페이지인지 2페이지인지 정보가 들어감.
     */







    @Modifying
    /*
     * modifying을 넣지 않게 되면 쿼리가 singleResult나 등등 executeUpdate를 실행해야할 것을 다르게 수정한다.
     *  modifying을 적어주지 않게 되면 오류가 생깁니다.
     */
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);











    /**
     * jpa Hint(Sql 힌드가 아니라 Jpa 구현체에 제공하는 힌트)
     * 하이버네이트한테 알려주는 힌드
     *
     * jpa 표준이란 > 인터페이스의 모음.
     * 하이버네이트의 기능을 더 쓰고  싶을때 힌트를 보냄.
     */

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String name);


    /**
     * 락
     * 해당 데이터에 대해서 나만 건들고 다른애들은 못건들게 할거야
     * 저번학기에 배웠던 내용복습
     *
     * 힌트말고 락도 걸어줄 수 있다.
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

}
