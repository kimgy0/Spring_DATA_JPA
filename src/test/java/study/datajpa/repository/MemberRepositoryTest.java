package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    @PersistenceContext
    EntityManager em;

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


    @Test
    public void paging() throws Exception{
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age=10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        //Spring data jpa는 페이징을 0부터 시작하고, 3개까지 가져오라는 얘기.
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        //구현을 하지 않아도 페이저블 객체를 넘기면(pageable 자식 객체) Page 형식이 반환된다.
        //        Sort.by(Sort.Direction.DESC, "username") 소팅 정렬은 생략 가능하다.
        // 반환 타입을 페이지라고 받게 되면 totalCount가 필요한걸 인지하고 totalCount를 실행한다.
//        long totalCount = memberRepository.totalCount(age);

        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        //dto로 바꾸는 방법.

        List<Member> content = page.getContent();
        //페이지 객체에 getcontent로 결과를 가져올 수 있다.
        long totalElements = page.getTotalElements();
        //토탈 카운트와 같은 쿼리가 나간것이다.

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);
        //OFFSET이 없는 이유는 스프링이 0을 기준으로 할때는 필요가 없다는 것을 느껴서 없앰.

        /*
         * member = Member(id=5, username=member5, age=10)
            member = Member(id=4, username=member4, age=10)
            member = Member(id=3, username=member3, age=10)
            totalElements = 5
         */

        /*
         * 여기서 마지막페이지와 최초페이지 등 공식을 적용하여 계산을 합니다.
         * 하지만 jpa-data 에서 페이징을 지원합니다.
         *
         * 옛날에 개발을 할 때는 수화통역 앱 만든 백엔드리포지토리를 가보게 되면 옛날 선배들이 고이 전수해주시는 페이징쿼리가 있는데
         * 그거 말고 springframework 에서 두가지 인터페이스로 공통화를 시켜서 만들어두었다.
         *
         * org.springframework.data.domain.Sort : 정렬 기능
         * org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)
         *
         *
         *
         * org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
         * org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능
         * 여기에서는 Page가 totalCount를 가져오는 쿼리를 날리고
         * Slice는 totalCount를 가져오는 쿼리를 날리지 않는다
         *  Slice : 쇼핑몰 사이트 더보기 기능 -> 1부터 11까지의 쿼리를 날리고 11이 존재하면 더보기를 표시하여 11 - 20까지 가져오고,
         *          이렇게 진행 하다가 마지막에 (21이 없을 경우) 더 가져올게 없을 경우 마지막 페이지 입니다를 호출하는 경우.
         *
         * 반환타입에 List 로 받으면 totalCount 없이 가져온다.
         */

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        // Assertions.assertThat(page.getNumber()).isEqualTo(0); 이렇게 하면 현재 페이지 번호도 가져올 수 있다.
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        // 이거는 전체 페이지 갯수 (직접계산) =2
        Assertions.assertThat(page.isFirst()).isTrue();
        // 이게 첫번째 페이지인가요 ? 라고 질의하면 true 임을 확인.
        Assertions.assertThat(page.hasNext()).isTrue();
        // 다음 페이지가 존재하나요? 라고 하는 질의 true 반환.
        /*
         * slice에는 없는 기능
         *  Assertions.assertThat(page.getTotalElements()).isEqualTo(5);
         *  Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
         *
         * total count 를 날리지 않는다(간단하게 다음 페이지가 있는지의 없는지의 경우만)
         *
         * Slice<Member> page = memberRepository.findByAge(age, pageRequest);
         * 페이지 리턴 타입을 slice 타입으로 바꾸어준다.
         *
         * 나중에 서버가 커지면 count에 기대는 데이터양이 너무 많아지면 slice로 바꿔주고 get total같은 기능이 없는 메서드를 없애준다.
         *
         * 슬라이스가 아니더라도 그냥 페이징만하고 싶을때는 List로 반환해서 바로 꺼내와도 상관은 없다.
         * List<Member> page = memberRepository.findByAge(age, pageRequest);
         *
         *
         *
         *
         * !------------------count query의 성능--------------------
         * count쿼리를 분리하는 방법 (인터페이스)
         *
         * 나중에 쿼리가 복잡해질 경우 sort를 하는데도 문제가 있다.
         * 단순 3번째인자인 sort로 안될 때가 있다. 그럴땐 과감하게 3번째 인자를 지워주고
         * 인터페이스로 넘어가서 @Query 안에 join을 하면서 sort 컨디션을 적어주도록 하자.
         */
    }



    @Test
    public void bulkUpdate() throws Exception{
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 41));

        int resultCount = memberRepository.bulkAgePlus(20);
        em.flush(); //or em.clear();
        /*
         * 벌크 연산의 특성상 1차 캐시와 동기화 되지 않고 update되어서 db만 업데이트 치고
         * 영속성 컨텍스는 동기화하지 않는다. 이게 벌크연산의 문제인데. em.refresh() 메서드나 다른 방법을로 동기화시켜주어야 한다.
         * 여기는 data - jpa를 다루는 공간이기 때문에 따로 언급하지 않겠다.
         *
         * em.flush(); //or em.clear(); hint
         *
         * 꼭 벌크연산 하고나면 실행해주는건 잊지말자.
         *
         * 이거말고 인터페이스가서 @Modifiying(clear = true) 라는 속성으로 깔끔하게 1차캐시를 비워주자
         *
         */
        assertThat(resultCount).isEqualTo(3);
    }





    @Test
    public void findMemberLazy() {
        //given
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
        for (Member member : members) {
            System.out.println("member = " + member);
        }
        /*
         * 1. findAll 할 때 전체 멤버를 다 조회한다.
         * 2. member.getTeam().getName();
         *      를 호출하게 되면 쿼리가 한 번씩 나가는 것을 지연로딩의 문제점으로 지적.
         *      member 객체에 있던 proxy 객체로 team이 가지고 있는 것이 진짜 team을 가져오기 위해서
         *      영속성컨텍스트에서 가져오려고 하려면 db에 쿼리를 select * from team 이런식으로 가지러나갑니다.
         *      그러면 쿼리가 당연히 한줄 더 나가는 겁니다.
         *      이걸 N+1 문제라고 합니다.
         *      1이 멤버를 불러오는 쿼리고 그에 붙어있는 연관된 아이들의 쿼리입니다.
         *
         *      해결방법!
         *      @Query("select m from Member m fetch join m.team") //연관된 team을 한방쿼리로 다 긁어옴.
         *      List<Member> findMemberFetchJoin();
         *
         *      하지만 fetch join을 할거면 항상 @Query를 적어줘야 한다.
         *      반복을 없애기 위해서 jpa 메서드 이름과 최소한의 작업으로 fetch join을 하고싶을 때,
         *
         *      해결방법2!
         *      @Override
         *      @EntityGraph(attributePaths = {"team"}) //엔티티 속성이름.
         *      List<Member> findAll();
         *      으로 엔티티그래프 어노테이션 하나로 fetch join으로 한거번에 불러온다.

         *      해결방법3! (짬뽕)
         *      @Query("select m from Member m")
         *      @EntityGraph(attributePaths = {"team"}) //엔티티 속성이름.
         *      List<Member> findMemberEntityGraph();
         *
         *      해결방법4 (이름으로 하는방법)
         *      @EntityGraph(attributePaths = {"team"}) //엔티티 속성이름.
         *      List<Member> findEntityGraphByUsername(@Param("username) String username);
         *
         *
         *  @EntityGraph 속성은 jpa 표준 속성입니다.
         *  @NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
         *  를 엔티티 클래스 위에 달아줘도 가능합니다.
         *      밑의 방식으로!
         *      @EntityGraph("Member.all") //엔티티 속성이름.
         *      List<Member> findEntityGraphByUsername(@Param("username) String username);
         * */
    }



    @Test
    public void queryHint() throws Exception{
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();
        /*
         * 영속성 컨텍스트를 다 날림.
         */

        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");
        //em.flush();
        /*
         * 이런식으로 변경감지가 됐을 때 flush를 치는 순간 쿼리가 나감.
         * 변경 감지가 뭘 하려면 원본이 있어야함.
         * 객체를 두개를 감시하는꼴이다 ( 원본, 바뀐객체 )
         *
         * 바뀐걸 알려면 원본이 있어야 비교를 하기 때문에 어떻게든 메모리를 더 쓰게 됩니다.
         *
         * 만약, 변경안하고 findMember 해서 db에서 단순히 조회만 하고 변경안할거야 라고 한다면
         * 가지고 오는 순간 이미 원본을 만들어 놓고 시작해
         * 나는 100퍼 이거를 조회용으로만 쓸거야 라고 한다면 그 방법이 있다.
         * jpa 표준 스펙이 아님.
         *
         * 그래서 하이버네이트기술을 이용하는데 이래서 hint가 필요한 것이다.
         *  @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
         *  Member findReadOnlyByUsername(String name);
         * */
        Member findByMember = memberRepository.findReadOnlyByUsername("member1");
        findByMember.setUsername("member2");
        /*
         * 이런식으로 적어주게 되면 queryhint readonly true를 시켜버리는 순간 변경을 무시해버린다.
         * 위에서 말한 원본 data를 만들지 않게 됨.
         * */
        em.flush();


    }

    @Test
    public void lock() throws Exception{
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        List<Member> result = memberRepository.findLockByUsername("member1");
        /*
         * 락을 걸어줄 때 db에 쿼리가 나가면 쿼리 뒤에 for Update 라는 키워드가 붙게 된다.
         * 단, db 방언에 따라 다르기 때문에 메뉴얼을 찾아보고 해보자.
         *
         * 내용이 너무 깊은 내용이기 때문에 jpa 책을 찾아보자.
         *
         * 저번학기 때 배웠던 db개론에서 배웠던 락 -> 나중에 은행서비스나 돈을 맞추는게 더 중요하거나 실시간 트래픽이 많지 않을 때 lock에 대해 더 공부해보자.
         */

    }

    @Test
    public void callCustom() throws Exception{
        //given
        List<Member> result = memberRepository.findMemberCustom();
    }

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
}