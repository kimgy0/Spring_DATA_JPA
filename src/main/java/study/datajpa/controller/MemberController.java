package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;
import study.datajpa.repository.MemberRepositoryCustom;

import javax.annotation.PostConstruct;

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
        //스프링 도메인 클래스 컨버터라고 해서 아이디 값을 받아오면 멤버 객체를 바로 찾아주는 것이다.
        //권장하지 않는 기능이지만, pk로 외부에 공개하기도 하면서 막상 뭘하려고하고
        //그렇게 하면 쿼리가 단순하게 돌아가는 경우도 많지 않다.
        // 간단한 경우에만 쓰고 복잡하게 되면 쓰지 못한다. 그리고 조회용으로만 써야함.

        return member.getUsername();
    }

    @PostConstruct
    public void init(){
//        memberRepository.save(new Member("userA"));
   //이전 도메인클래스 컨버터에서 테스트 샘플할 때 넣어준 코드

//        for(int i =0; i<100; i++){
//            memberRepository.save(new Member("user"+i));
//        }

    }



    //======================================
    @GetMapping("/members")
    public Page<Member> list(Pageable pageable){
        return memberRepository.findAll(pageable);
    }
    /*
    for(int i =0; i<100; i++){
            memberRepository.save(new Member("user"+i));
        }
     */
    //postconstructor에서 넣어준 100개의 것으로 members에 들어가면 100개를 다 반환하지만
    //members?page=0 으로 하게되면 20개씩 불러온다.
    //그리고 동시에 members?page=0&size=3 이런식으로 하면 1,2,3 으로 pk를 가지고 있는 멤버가 출력되며
    //members?page=1&size=3 이런식으로 하게되면 4,5,6 이런식으로 spring jpa에서 알아서 넣어준다.
    // 그외에 members?page=1&size=3&sort=id,desc 이런식으로 정렬도 되고 &sort=username,desc 이런식으로 이어서 써도된다.

    // 글로벌 설정
    //근데 두번째줄에서 20개씩 불러오는것을 디폴트로20인데 바꾸고 싶으면
    //application.yml 에서 data:web:pageable:default-page-size:10
                                            //max-page-size:2000
    //지역설정
    //public Page<Member> list(@PageableDefault(size=5, sort="username") Pageable pageable)
    //이런식으로 어노테이션 설정도 가능

    //그외에 다른 속성들이 왜나오냐면 total 쿼리를 날리기 때문이다 PAGE반환형은 토탈쿼리를 날리기 떄문이다.
    //접두사로 @Qualifier("member") Pageable memberPageable,
    //       @Qualifier("order") Pageable orderPageable
    // 인자로 두개를 이렇게 지정해주면 /members?member_page=0&order_page=1
    // order_page, member_page 페이징 정보가 두개가 필요할때. 이런식으로 적어준다.

    /*
     * tip.
     * 엔티티를 그대로 반환하지말고 map함수를 써서 dto로 바꿔라
     * dto는 엔티티를 봐도 상관 없다. 때문에 생성자에 member를 받아서 this.id = member.getId()
     * 이런식으로 받아서 dto로 생성해도 상관 없다.
     *
     * tip2.
     * -----쓰는 방법----- 페이징을 1부터 시작하는 방법
     * Pageable pageable 이런식으로 쓰지말고
     * 앞에서 pageRequest.of(1,2,sort --- ) 이런식으로 새로 정의해서 써도 됩니다.
     * api스펙에 맞춰서 하는 방법
     *
     * 또는
     * data:web:pageable:
     *          :one-indexed-parameters:true
     * 이런식으로 설정이 (yml에 설정)되면 된다.
     * *한계점은 좀 api 스펙이 서로 안맞을 떄가 많다. 하나씩 틱나는 페이지 값이 있다.
     */
}
