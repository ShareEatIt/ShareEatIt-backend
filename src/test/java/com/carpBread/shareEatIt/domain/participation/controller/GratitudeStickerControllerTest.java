package com.carpBread.shareEatIt.domain.participation.controller;

import com.carpBread.shareEatIt.config.WithMockCustomUser;
import com.carpBread.shareEatIt.domain.participation.dto.GratitudeResponseDto;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import com.carpBread.shareEatIt.domain.participation.service.GratitudeStickerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GratitudeStickerController.class) //web mvc 관련 컴포넌트를 로드해 특정 컨트롤러를 테스트하는데 필요한 설정을 제공
@MockBean(JpaMetamodelMappingContext.class)  // 스프링 테스트 환경에서 실제 DB와의 상호작용없이 테스트 가능
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)  // 테스트 메서드의 언더 스코어를 공백으로 대체
class GratitudeStickerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GratitudeStickerService gratitudeStickerService;

    // 테스트를 위한 상수 선언
    private static final Long MOCK_POST_ID = 111L;
    private static final Long MOCK_MEMBER_ID = 1L;
    private static final Long MOCK_GS_ID = 1L;
    private static final GratitudeType GRATITUDE_TYPE = GratitudeType.SMILE1;


    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(patch("/**").with(csrf()))
                .build();
    }

    // 가짜 responseDto 생성 메서드
    private GratitudeResponseDto createMockResponseDto() {
        return GratitudeResponseDto.builder()
                .gratitudeStickersId(1L)
                .sharingPostId(MOCK_POST_ID)
                .giverId(MOCK_MEMBER_ID)
                .gratitudeType(GRATITUDE_TYPE)
                .createdAt(LocalDateTime.now())
                .build();
    }


    @Test
    @WithMockCustomUser
    void 고마움스티커_생성_성공() throws Exception {

        // given
        GratitudeResponseDto mockResponseDto = createMockResponseDto();

        // stub 설정: 서비스 계층의 반환값 미리 지정 , 위에서 만들어 둔 responseDto로 지정해 둠
        Mockito.when(gratitudeStickerService.createGratitudeSticker(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mockResponseDto);

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/gratitudeStickers/{postId}", MOCK_POST_ID)
                .param("gratitudeType", GRATITUDE_TYPE.name()) // 요청 파라미터
                .contentType(MediaType.APPLICATION_JSON) // Content-Type
                .accept(MediaType.APPLICATION_JSON) // Accept 헤더
        );

        // then
        resultActions
                .andExpect(status().isCreated()) // HTTP 상태코드 201 확인
                .andExpect(jsonPath("$.status").value(201)) // 응답 JSON의 상태값 확인
                .andExpect(jsonPath("$.message").value("고마움 생성 성공")) // 성공 메시지 확인
                .andExpect(jsonPath("$.data.gratitudeStickersId").value(mockResponseDto.getGratitudeStickersId())) // 반환 데이터 검증
                .andExpect(jsonPath("$.data.giverId").value(MOCK_MEMBER_ID))
                .andExpect(jsonPath("$.data.gratitudeType").value(GRATITUDE_TYPE.name()));

        Mockito.verify(gratitudeStickerService, Mockito.times(1))  // 서비스 함수가 1번만 호출되었는지 검증
                .createGratitudeSticker(Mockito.any(), Mockito.any(), Mockito.any());
    }


    @Test
    @WithMockCustomUser
    void 고마움스티커_수정_성공() throws Exception {

        // given
        GratitudeResponseDto mockResponseDto = createMockResponseDto();

        // stub 설정
        Mockito.when(gratitudeStickerService.updateGratitudeStickers(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(mockResponseDto);

        // when
        ResultActions resultActions = mockMvc
                .perform(patch("/gratitudeStickers/{gsId}", MOCK_GS_ID)
                .param("gratitudeType", GRATITUDE_TYPE.name()) // 요청 파라미터
                .contentType(MediaType.APPLICATION_JSON) // Content-Type
                .accept(MediaType.APPLICATION_JSON) // Accept 헤더
        );

        // then
        resultActions
                .andExpect(status().isOk()) // HTTP 상태코드 200 확인
                .andExpect(jsonPath("$.status").value(200)) // 응답 JSON의 상태값 확인
                .andExpect(jsonPath("$.message").value("고마움 스티커 수정 성공")) // 성공 메시지 확인
                .andExpect(jsonPath("$.data.gratitudeStickersId").value(mockResponseDto.getGratitudeStickersId())) // 반환 데이터 검증
                .andExpect(jsonPath("$.data.gratitudeType").value(GRATITUDE_TYPE.name())) // 수정된 gratitudeType 검증
                .andExpect(jsonPath("$.data.giverId").value(MOCK_MEMBER_ID)); // giverId 검증

        Mockito.verify(gratitudeStickerService, Mockito.times(1)) // 서비스 함수가 1번만 호출되었는지 검증
                .updateGratitudeStickers(Mockito.anyLong(), Mockito.any(), Mockito.any());

    }

}