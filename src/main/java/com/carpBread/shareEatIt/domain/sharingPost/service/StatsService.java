package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.RankResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.SharingPostPeriodByCategoryResponseComponent;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.SharingPostStatsPeriodResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.StatsCategoryPostListSimpleResponseComponent;
import com.carpBread.shareEatIt.global.entity.Period;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostQuerydslRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsService {
    private final SharingPostQuerydslRepository sharingPostQuerydslRepository;

    // 10km radius
    private final double radius = 10000;

    public SharingPostStatsPeriodResponseDto getStatsByPeriod(Member member, String periodType) {
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
                    Domain.SHARING_POST
            );
        }

        // 2. post 조회
        List<SharingPost> postList =
                sharingPostQuerydslRepository.findByWriterInPeriod(member, ago,now);

        // 3. 음식 카테고리별 나눔글 작성 수
        List<SharingPostPeriodByCategoryResponseComponent> categoryCountList = createCategoryCountList(postList);

        // 4. 가장 많이 나눔한 음식 카테고리
        String popularCategory=null;
        int tempMaxV=0;
        int countAlreadyParticipation=0;

        for (SharingPostPeriodByCategoryResponseComponent component : categoryCountList){
            if (tempMaxV<=component.getCount()){
                popularCategory=component.getCategory();
                tempMaxV=component.getCount();
            }
            for (StatsCategoryPostListSimpleResponseComponent simplec : component.getSharingPostList()){
                if (simplec.getStatus().equals(PostStatus.COMPLETED.name())){
                    countAlreadyParticipation+=1;
                }
            }
        }

        // 5. 기간 내 총 나눔 횟수
        int totalSharedCount=postList.size();

        // 6. 나눔 성사 비율
        float participationRate = 0.0f;
        if (totalSharedCount>0){
            participationRate= (float) (countAlreadyParticipation /totalSharedCount);
        }

        // 7. 10km 이내 거주자 중 나눔 순위
        int userRankIn10km = rankIn10kmUsers(member);

        // 8. response dto 생성
        return new SharingPostStatsPeriodResponseDto(period,
                categoryCountList,
                popularCategory,
                totalSharedCount,
                participationRate,
                userRankIn10km);
    }

    private List<SharingPostPeriodByCategoryResponseComponent> createCategoryCountList(
            List<SharingPost> postList){

        HashMap<String, List<StatsCategoryPostListSimpleResponseComponent>> map = new HashMap<>();

        for (SharingPost post : postList){
            StatsCategoryPostListSimpleResponseComponent simpleComponent = new StatsCategoryPostListSimpleResponseComponent(post.getId(),
                    post.getStatus().name(), post.getCreatedAt());
            map.computeIfAbsent(post.getCategory().name(),v->new ArrayList<>()).add(simpleComponent);
        }

        List<SharingPostPeriodByCategoryResponseComponent> categoryComponentList = new ArrayList<>();

        for(String category : map.keySet()){
            List<StatsCategoryPostListSimpleResponseComponent> components = map.get(category);
            categoryComponentList.add(new SharingPostPeriodByCategoryResponseComponent(category,components.size(),components));

        }

        return categoryComponentList;

    }

    public RankResponseDto sharingRankOfWriter(Member writer){
        return new RankResponseDto(sharingPostQuerydslRepository.findSharingRank(writer));
    }

    private int rankIn10kmUsers(Member writer){
        return sharingPostQuerydslRepository.findSharingRankInRadius(writer, radius);
    }


}
