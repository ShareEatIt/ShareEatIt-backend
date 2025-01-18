package com.carpBread.shareEatIt.global.config;

import com.querydsl.core.annotations.Config;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PointGeometryFactoryConfig {

    // WGS SRID 지정 좌표계
    private final int SRID=4326;

    @Bean
    public GeometryFactory geometryFactory(){
        // 기본
        PrecisionModel precisionModel = new PrecisionModel();
        return new GeometryFactory(precisionModel,SRID);
    }
}
