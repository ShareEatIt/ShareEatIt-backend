package com.carpBread.shareEatIt.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class GeometryFactoryConfig {
    // WGS SRID 지정 좌표계
    private final int SRID=4326;

    @Bean
    public GeometryFactory geometryFactory(){
        // 기본
        PrecisionModel precisionModel = new PrecisionModel();
        return new GeometryFactory(precisionModel,SRID);
    }

}
