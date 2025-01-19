package com.carpBread.shareEatIt.domain.member.controller;

import com.carpBread.shareEatIt.config.WithMockCustomUser;
import com.carpBread.shareEatIt.domain.member.dto.response.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.member.dto.response.MemberProfileResponseDto;
import com.carpBread.shareEatIt.domain.member.dto.request.MemberProfileUpdateRequestDto;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/* MemberController.java Controller 단위 테스트 */
@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(put("/**").with(csrf()))
                .defaultRequest(patch("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .build();
    }



//    @Test
//    @DisplayName("성공 : 회원 정보 조회 controller 성공 테스트")
//    void getMemberProfileSuccessTest() {
//        // given
//        Mockito.when(memberService.)
//
//
//        // when
//
//
//        // then
//
//
//
//    }

//    @Test
//    void memberStickers() {
//    }
//
//    @Test
//    void getMemberSharingStatus() {
//    }
//
//    @Test
//    void updateMemberProfile() {
//    }
//
//    @Test
//    void updateMemberAvailKeyword() {
//    }

    @Test
    @DisplayName("성공 : 회원정보 수정 성공 테스트")
    @WithMockCustomUser
    void updateMemberProfileSuccessTest() throws Exception {
        // given
        // 매개변수 생성
        // 1. @WithMockUser
        // 2. MultipartFile
        String filePath = "src/test/resources/073d2624-b839-4d79-8b29-4dddc8455498.jpg";
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        MockMultipartFile newImagePart = new MockMultipartFile(
                "imgFile",
                file.getName(),
                "image/jpeg",
                fileInputStream
        );

        // 3. requestDto
        MemberProfileUpdateRequestDto requestDto = MemberProfileUpdateRequestDto.builder()
                .profileImg("testimgurl")
                .nickname("test22")
                .provider("STORE")
                .latitude(127.099492)
                .longitude(36.798331)
                .addressSt("충청남도 수한군 행복동")
                .addressDetail("사랑길 56번지")
                .build();
        MockMultipartFile requestDtoPart=new MockMultipartFile(
                "dto",
                "request.json",
                "application/json",
                objectMapper.writeValueAsBytes(requestDto)
        );


        // responseDto 설정
        MemberProfileResponseDto responseDto = MemberProfileResponseDto.builder()
                        .id(1L).profileImg("testimgurl")
                        .nickname("test22").email("test@gmail.com")
                        .location(
                                LocationResponseDtoComponent.builder().build()
                        ).provider("STORE")
                        .joinedAt(LocalDateTime.now()).recentModifiedAt(LocalDateTime.now())
                        .build();

        // memberService updateProfile mock stub 결과 지정
        Mockito.when(memberService.updateProfile(Mockito.any(),Mockito.any(),Mockito.any()))
                .thenReturn(responseDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                        multipart("/members")
                        .file(newImagePart)
                        .file(requestDtoPart)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        );


        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value(requestDto.getNickname()));

    }

//    @Test
//    void memberWithdrawal() {
//    }
//
//    @Test
//    void getOpponentInfo() {
//    }


    Member createTestMember(){
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);
        return Member.builder()
                .id(1L)
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .profileImgUrl("testimgurl")
                .locationPoint(memberLocation)
                .addressSt("충청남도 천안시 서북구 불당동")
                .addressDetail("공원로 176 303동")
                .provider(Provider.STORE)
                .build();
    }
}