package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

    @Test
    public void testMember() throws Exception{
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Optional<Member> byId = memberRepository.findById(savedMember.getId());
        //jpaRepository로 쓰게 되면 Optional로 반환하는데 엔티티가 있을 수 있거나 없을 수도 있기 때문이다.

        Member findMember = byId.get();
        //원래는 이런식으로 get으로 가져오면 안되고 or else 같은 키워드를 사용해서 검증한 후에 꺼내야합니다.

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);

    }
    @Test
    public void basicCRUD() throws Exception{
        //given
        Member member1 = new Member("member1");//when
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() throws Exception{
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findHelloBy() throws Exception{
        List<Member> result = memberRepository.findHelloBy();
    }

    @Test
    public void namedQuery() throws Exception{
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() throws Exception{

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }

        //실무에서 검증은 assertions로 하자.
    }

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

    @Test
    public void findByNames() throws Exception{
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() throws Exception{
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);


        List<Member> result = memberRepository.findListByUsername("AAA");
        System.out.println("result.size() = " + result.size());
        /*
         * 만약 매칭이 되지 않으면 빈 컬렉션을 제공한다.
         * 그래서 간혹 if(result == null) 이런 코드를 작성하는데 안좋은 코드이다.
         */

        Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println("findMember = " + findMember);
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
         */

        Optional<Member> findOptionalMember = memberRepository.findOptionalByUsername("AAA");
        System.out.println("findOptionalMember = " + findOptionalMember);
    }

}