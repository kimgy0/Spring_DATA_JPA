package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/*
 * 등록일, 수정일, 등록자, 수정자 를 테이블 생성할 때 이런 comment가 따라와야 할 경우
 */
@Getter
@Setter
@MappedSuperclass
public class JpaBaseEntity {

    @Column(updatable = false, insertable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    // persist하기 전에 이벤트가 발생한다.
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.updatedDate = now;
    }

    @PreUpdate
    public void preUpdate(){
        this.updatedDate = LocalDateTime.now();
    }

    /*
     * 멤버테이블에 extends JpaBaseEntity 이런 식으로 상속해주고
     * 속성만 내려쓰는 상속관계를 이용
     * @MappedSuperClass
     */


//      @Test
//          public void jpaEventBaseEntity() throws Exception{
//              //given
//              Member member = new Member("member1");
//              memberRepository.save(member); //@PrePersist가 발생!
//              Thread.sleep(100);
//              member.setUsername("member2");
//              em.flush(); //@PreUpdate가 실행
//              em.clear();
//              //when
//
//              Member findMember = memberRepository.findById(member.getId()).get();
//
//              //then
//              System.out.println("findMember.getCreatedDate = " + findMember.getCreatedDate());
//              System.out.println("findMember.getUpdatedDate = " + findMember.getUpdatedDate());
//          }
//


    //public class Team extends JpaBaseEntity
    //team같은 경우에도 관심사가 하나로 몰렸기때문에 이런식으로 해주면 좋다.
    //추가로 @PostPersist
    //@PostUpdate

    //-------------------------------------------------------
    //더 나가서 @EnableJpaAuditing 을 메인 클래스에 갖다가 집어넣어준다.
    //그리고나서 baseEntity 클래스 생성
    /**
     * 이벤트로 기반한다는 것을 알려주는 리스너를 추가해주고
     * @EntityListeners(AuditingEntityListener.class)
     *
     * @EntityListeners(AuditingEntityListener.class)
     * ->귀찮을 경우(글로벌적용하고싶은 경우)에는 /META-INF/orm.xml에 등록해주면 됨 xml코드
     * PDF검색
     *
     *
     * @MappedSuperclass
     * @Getter
     * public class BaseEntity {
     *
     *     @CreatedDate
     *     @Column(updatable = false)
     *     private LocalDateTime createdDate;
     *
     *     @LastModifiedDate
     *     private LocalDateTime lastModifiedDate;
     *
     *     @CreatedBy
     *     @Column(updatable = false)
     *     private String createdBy;
     *
     *     @LastModifiedBy
     *     private String lastModifiedBy;
     * }
     */

    /**
     * @CreatedBy
     *      *     @Column(updatable = false)
     *      *     private String createdBy;
     *      *
     *      *     @LastModifiedBy
     *      *     private String lastModifiedBy;
     *
     *      테이블마다 특성이 다를 수 있어서 ID값을
     *      필요로하지 않을 경우가 있다. 이때는 BASEENTITY 가 상속받는 클래스를 하나 만들고
     *
     *      @CreatedDate
     *      *     @Column(updatable = false)
     *      *     private LocalDateTime createdDate;
     *      *
     *      *     @LastModifiedDate
     *      *     private LocalDateTime lastModifiedDate;
     *      이 두개 속성만 있는 클래스를 똑같이 만들어 주고
     *      다 필요로하면 베이스엔티티를 상속
     *      OR
     *      시간만 필요하면 타임 엔티티만 상속! 하는 경우를 예로 들 수 있다.
     *
     *
     */

    //추가로 더
//    @Bean
//    public AuditorAware<String> auditorProvider(){
//        return new AuditorAware<String>() {
//            //인터페이스에서 메서드가 하나면 람다로 변환 가능.
//            @Override
//            public Optional<String> getCurrentAuditor() {
//                return Optional.of(UUID.randomUUID().toString());
//                //예를들어 스프링시큐리티를 쓰게 되지게 되면 정보를 꺼내서 id값을 넣어주거나? 하면된다.
////				//이렇게 id값을 넘겨주게 되면
////				@CreatedBy
////				@Column(updatable = false)
////				private String createdBy;
////
////				@LastModifiedBy
////				private String lastModifiedBy;
//
//                //이공간에 이렇게 넘겨준 uuid값이 string 형태로 넘어가서 채워지게 된다.
//            }
//        };
//    }
//    //return () -> Optional.of(UUID.randomUUID().toString());
}









