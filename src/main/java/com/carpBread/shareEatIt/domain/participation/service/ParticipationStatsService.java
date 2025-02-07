package com.carpBread.shareEatIt.domain.participation.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.dto.stats.ParticipationPeriodByCategoryResponseComponent;
import com.carpBread.shareEatIt.domain.participation.dto.stats.ParticipationStatsPeriodResponseDto;
import com.carpBread.shareEatIt.domain.participation.dto.stats.StatsCategoryParticipationListSimpleResponseComponent;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationQuerydslRepository;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.RankResponseDto;
import com.carpBread.shareEatIt.global.entity.Period;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j @Transactional
@RequiredArgsConstructor
public class ParticipationStatsService {

    private final ParticipationQuerydslRepository participationQuerydslRepository;
    private final double radius = 10000;

    public ParticipationStatsPeriodResponseDto getStatsByPeriod(Member member, String periodType) {
        // 1. 조회 기간
        LocalDateTime ago = null;
        LocalDateTime now = LocalDateTime.now();
        String period="";

        if (Period.MONTH.getValue().equals(periodType)){
            ago=LocalDateTime.of(
                    now.getYear(), now.getMonthValue(),
                    1,0,0
            );
            period = now.getYear() + "-" + now.getMonthValue();

        } else if (Period.WEEK.getValue().equals(periodType)) {
            LocalDateTime minusDays = now.minusDays(7);
            ago=LocalDateTime.of(
                    minusDays.getYear(), minusDays.getMonthValue(),
                    minusDays.getDayOfMonth(), 0,0
            );
            period=ago.getYear()+"-"+ago.getMonthValue()+"-"+ago.getDayOfMonth()
                    +" ~ "
                    +now.getYear()+"-"+now.getMonthValue()+"-"+now.getDayOfMonth();
        }else {
            throw new CustomException(
                    CustomExceptionStatus.INVALID_ENUM_VALUE,
                    "올바르지 않은 PeriodType ENUM 값입니다",
                    this.getClass().getSimpleName(),
                    periodType,
                    Domain.PARTICIPATION
            );
        }

        // 2. paricipation 조회
        List<Participation> participationList = participationQuerydslRepository.findByReceiverInPeriod(member, ago, now);

        // 3. 음식 카테고리별 참여 수
        List<ParticipationPeriodByCategoryResponseComponent> categoryCountList = createCategoryCountList(participationList);

        // 4. 가장 많이 참여한 음식 카테고리
        String popularCategory=null;
        int tempMaxV=0;

        for (ParticipationPeriodByCategoryResponseComponent component : categoryCountList){
            if (tempMaxV<=component.getCount()){
                popularCategory=component.getCategory();
                tempMaxV=component.getCount();
            }
        }

        // 5. 기간 내 총 참여 횟수
        int totalParticipationCount = participationList.size();

        // 6. 10km 이내 거주자 중 총 참여 순위
        int userRankIn10km =  rankIn10kmUsers(member);

        // 7. response dto 생성
        return new ParticipationStatsPeriodResponseDto(
                period,
                categoryCountList,
                popularCategory,
                totalParticipationCount,
                userRankIn10km
        );


    }

    // 사용자 총 참여 순위
    public RankResponseDto participationRankOfReceiver(Member receiver) {
        return new RankResponseDto(participationQuerydslRepository.findParticipationRank(receiver));

    }

    private int rankIn10kmUsers(Member receiver) {
        return participationQuerydslRepository.findParticipationRankInRadius(receiver, radius);

    }

    private List<ParticipationPeriodByCategoryResponseComponent> createCategoryCountList(List<Participation> participationList) {
        HashMap<String, List<StatsCategoryParticipationListSimpleResponseComponent>> map = new HashMap<>();

        for (Participation p : participationList){
            StatsCategoryParticipationListSimpleResponseComponent simpleComponent = new StatsCategoryParticipationListSimpleResponseComponent(
                    p.getId(), p.getStatus().name(), p.getCreatedAt()
            );
            map.computeIfAbsent(p.getPost().getCategory().name(), v -> new ArrayList<>())
                    .add(simpleComponent);
        }

        List<ParticipationPeriodByCategoryResponseComponent> categoryComponentList = new ArrayList<>();
        for (String category : map.keySet()){
            List<StatsCategoryParticipationListSimpleResponseComponent> components = map.get(category);
            categoryComponentList.add(new ParticipationPeriodByCategoryResponseComponent(category,components.size(), components));
        }
        return categoryComponentList;

    }


}
