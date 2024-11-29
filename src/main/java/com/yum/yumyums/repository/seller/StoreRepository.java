package com.yum.yumyums.repository.seller;

import com.yum.yumyums.entity.seller.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Integer> {
	Store findByName(String storeName);
    Page<Store> findBySellerId(String sellerId, Pageable pageable);



/*
    "이 쿼리문은 특정 위치에서 일정 반경 안에 있는 가게들을 찾기 위한 것입니다.
     입력된 위도(lat), 경도(lon), 반경(radius)를 기준으로, Haversine 공식을 사용하여 지구 상의 두 점 사이의 거리를 계산합니다.
     이 공식은 두 지점의 위도와 경도를 이용하여 구면 좌표계에서의 거리를 구하는 데 사용됩니다.

        1. 거리 계산에 Haversine 공식 사용:
            -   (6371 * acos(...)) 부분은 위도와 경도를 라디안으로 변환하여 삼각함수 계산을 통해 두 점 사이의 거리를 구합니다.
                여기서 6371은 지구의 평균 반지름(단위: 킬로미터)입니다.
            -   cos와 sin 함수는 기준점의 위도와 경도, 그리고 각 가게의 좌표(convX, convY)를 사용하여 거리 계산을 수행합니다.

        2. 반경 조건 설정:
            -   <= (:radius / 1000.0) 부분은 입력된 반경을 미터 단위로 받아 킬로미터로 변환한 뒤, 계산된 거리와 비교합니다.
                이 조건을 만족하는 가게들만 결과로 반환됩니다.

        3. 쿼리의 결과:
            -   이 쿼리는 입력된 위치에서 지정된 반경 내에 있는 가게 목록을 반환합니다.
                이를 통해 사용자는 원하는 위치 근처에 있는 가게들을 쉽게 찾을 수 있습니다.
*/
    @Query(value = "SELECT * FROM store " +
            "WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(convY)) * " +
            "cos(radians(convX) - radians(:lon)) + sin(radians(:lat)) * " +
            "sin(radians(convY)))) <= (:radius / 1000.0)", nativeQuery = true)
    List<Store> findStoresWithinRadius(@Param("lat") double lat, @Param("lon") double lon, @Param("radius") int radius);




    /*

    ### 공간 인덱스(Spatial Indexing)를 활용하여 성능 최적화 ###

    " 공간 인덱싱(Spatial Indexing)은 지리적 데이터나 공간 데이터를 효율적으로 검색하기 위한 데이터베이스 인덱싱 기법입니다.
     일반적인 인덱스가 숫자나 문자열을 정렬하여 빠르게 검색하는 것처럼,
     공간 인덱스는 지리적 좌표(예: 위도와 경도)를 기반으로 데이터의 위치를 빠르게 검색할 수 있도록 도와줍니다. "

    1. 기존 Haversine 방식의 문제점 :
    인덱스를 사용하지 않음: Haversine 방식은 좌표값에 인덱스를 사용하지 않고, 모든 데이터를 계산하는 방식이기 때문에 데이터가 많아질수록 성능이 떨어질 수 있습니다.
    계산 비용이 큼: 각 데이터에 대해 삼각함수(sin, cos, acos) 계산을 수행하므로, 계산 비용이 큽니다.


    2. 공간 인덱스의 이점 :
     - 인덱스를 사용하여 성능 최적화 : 쿼리가 모든 데이터를 검사할 필요 없이 인덱스에 의해 미리 필터링된 데이터에 대해서만 계산을 수행합니다.
     - 단계 감소: 기존 Haversine 쿼리와 달리 모든 행에 대해 거리 계산을 하지 않으므로, 데이터의 양이 많을수록 성능 향상 폭이 커집니다.
    */
    @Query(value = "SELECT * FROM store " +
            "WHERE ST_Distance_Sphere(location,  ST_GeomFromText(:point, 4326)) <= :radius", nativeQuery = true)
    List<Store> findStoresWithinRadiusV2(@Param("point") String point, @Param("radius") int radius);
}
