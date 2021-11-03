package study.datajpa.repository;

public interface NestedClosedProjections {
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo{
        String getName();
    }
    /**
     * 중첩구조의 처리
     * 멤버뿐 아니라 연관된 팀까지 가져온다.
     */
}
