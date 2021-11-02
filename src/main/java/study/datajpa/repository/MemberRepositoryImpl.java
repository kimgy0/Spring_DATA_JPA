package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m").getResultList();
        /*
         * data jpa로 순수하게 구현하고 싶을때는 jdbc로 커넥션을 얻어오거나 이런식으로 구현해준다.
         */
        //그리고 우리가 구현한 spring jpa에 대해서
        //public interface MemberRepository extends JpaRepository<Member, Long> , MemberRepositoryCustom
        //이런식으로 우리가 지금 클래스에서 인터페이스 상속받은 것을 가져다가 붙여주면 된다.
        //인터페이스는 텅텅비었는데 어떻게되는가?
        //그 인터페이스를 구현한 클래스를 가져다가 집어넣어줌. -> 스프링자체 기능 자바기능이 아님.

        //단 여기서 정해주어야할 규칙이 있음 jpaRepository에서 구현한 메서드를 사용하기 위해서는
        //memberRepository Impl 이라는 이름을 붙여야한다.  ImplImplImplImpl

        //impl이 싫으면 impl대신 다른 이름으로 변경하고 싶으면을 pdf에서 검색
        //왠만하면 관례를 따르세요 이새꺄 유지보수를 생각해야지지

        /*
        * @Test
                 public void callCustom() throws Exception{
                    //given
                    List<Member> result = memberRepository.findMemberCustom();
                }
                *
                * 테스트 메서드로 확인 한겨 경우
                 querydsl 이나 커스텀을 해서 많이 씀. (복잡한 경우)
                 * 또는 jdbc에 직접 붙여할 경우
         */

    }
}
