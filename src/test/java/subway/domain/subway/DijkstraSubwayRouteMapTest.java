package subway.domain.subway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.domain.Path;
import subway.domain.line.Line;
import subway.domain.section.Section;
import subway.domain.station.Station;
import subway.exception.InvalidStationException;

class DijkstraSubwayRouteMapTest {

    @Nested
    @DisplayName("최단 거리를 가지는 경로를 계산할 시")
    class findShortestPath {

        final Station upward = new Station(1L, "잠실역");
        final Station middle = new Station(2L, "사당역");
        final Station downward = new Station(3L, "서울역");
        DijkstraSubwayRouteMap routeMap;

        @BeforeEach
        void setUp() {
            final List<Section> sectionForLineTwo = List.of(new Section(upward, middle, 3));
            final List<Section> sectionsForLineFour = List.of(new Section(middle, downward, 4));

            final Line lineTwo = new Line(1L, "2호선", "초록색", new LinkedList<>(sectionForLineTwo));
            final Line lineFour = new Line(2L, "4호선", "하늘색", new LinkedList<>(sectionsForLineFour));

            routeMap = new DijkstraSubwayRouteMap(List.of(lineTwo, lineFour));
        }

        @Test
        @DisplayName("올바른 역 정보가 전달되면 해당 경로 정보를 반환한다.")
        void findShortestPathWithValidStations() {
            //given
            //when
            final Path shortestPath = routeMap.findShortestPath(upward, downward);

            //then
            assertAll(
                    () -> assertThat(shortestPath.getStations()).containsExactly(upward, middle, downward),
                    () -> assertThat(shortestPath.getDistance()).isEqualTo(7)
            );
        }

        @Test
        @DisplayName("노선에 등록되지 않은 역이 전달되면 예외를 던진다.")
        void findShortestPathWithInvalidStations() {
            //given
            final Station station = new Station(Long.MAX_VALUE, "미등록역");

            //when
            //then
            assertThatThrownBy(() -> routeMap.findShortestPath(station, downward))
                    .isInstanceOf(InvalidStationException.class)
                    .hasMessage("노선에 등록되지 않은 역 정보입니다.");
        }
    }
}
