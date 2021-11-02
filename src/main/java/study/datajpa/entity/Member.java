package study.datajpa.entity;


import lombok.*;

import javax.persistence.*;


@NamedQuery(name="Member.findByUsername",
            query="select m from Member m where m.username =:username")
//namedQuery : 자주 사용하는 쿼리에 이름을 붙여서 사용하는 방식.
/**
 * 네임드 쿼리 사용 방법
 * em.createNamedQuery("Member.findByUsername", Member.class)
 *                 .setParameter("username", username)
 *                 .getResultList();
 */
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// Setter 는 엔티티에 다는 것을 권장하지 않는다.
@ToString(of = {"id","username","age"})
//@ToString(of = {"id","username","age","team"}) 팀을 추가 해주면 연관관계를 타고 들어가서 toString 을 출력해서 무한 루프에 빠질 수 있음!.
public class Member extends JpaBaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null){
            changeTeam(team);
        }
    }


    //연관관계 의존 메서드.
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }



// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// 위의 문장으로 대체가 되어질 수 있음.

//    protected Member() {
//        // 아무 곳이나 호출되게 하지 않기 위해 protected로 기본생성자를 감싸줌.
//        // protected 인 이유는 jpa스펙 상도 그렇고 도큐먼트도 protected라고 되어있다.
//    }

    public Member(String username) {
        this.username = username;
    }
}
