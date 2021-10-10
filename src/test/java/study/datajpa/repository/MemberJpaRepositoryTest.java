package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

//RunWith(---) junit5 에서는 적어주지 않아도 된다.
@SpringBootTest
@Transactional //트랜잭션을 달아주지 않으면 에러가 납니다. -> 모든 jpa데이터의 변경은 트랜잭션 안에서 이루어져야하기 때문.
                //DB에 트랜잭션을 달아주게 되면 자동으로 값이 롤백이 되어서 db에 아무것도 남지 않음.
                //그렇기 때문에 @Roleback(false) 어노테이션을 달아주게 되면 자동으로 db에 눈으로 볼 수 있게 기록을 남김.
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test
    void testMember() {
        Member member = new Member("memberA");
//        member.setUsername("memberA") - > setter를 이런식으로 적는 것 보다 생성자를 이용해서 생성하는게 올바른 방법이다.
        Member saveMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(member.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    
    //TeamJpaRepository 검증
    @Test
    public void basicCRUD() throws Exception{
        //given
        Member member1 = new Member("member1");//when
        Member member2 = new Member("member2");
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        //단건 조회
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회
        List<Member> all = memberJpaRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberJpaRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        //삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long deletedCount = memberJpaRepository.count();
        Assertions.assertThat(count).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() throws Exception{
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThen("AAA", 15);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);

    }
    
    @Test
    public void testNamedQuery() throws Exception{
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);

    }

    /*
     * 나이를 조건으로 페이징 하는 쿼리
     */
    @Test
    public void paging() throws Exception{
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;


        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

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

        Assertions.assertThat(members.size()).isEqualTo(3);
        Assertions.assertThat(totalCount).isEqualTo(5);

    }


    @Test
    public void bulkUpdate() throws Exception{
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 19));
        memberJpaRepository.save(new Member("member3", 20));
        memberJpaRepository.save(new Member("member4", 21));
        memberJpaRepository.save(new Member("member5", 41));

        int resultCount = memberJpaRepository.bulkAgePlus(20);

        Assertions.assertThat(resultCount).isEqualTo(3);
    }
}