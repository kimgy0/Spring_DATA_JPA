package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

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

}
