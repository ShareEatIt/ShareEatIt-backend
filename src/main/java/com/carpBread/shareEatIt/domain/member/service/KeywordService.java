package com.carpBread.shareEatIt.domain.member.service;

import com.carpBread.shareEatIt.domain.member.dto.keyword.KeywordAvailableListResponseDto;
import com.carpBread.shareEatIt.domain.member.dto.keyword.KeywordCreateRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.keyword.KeywordResponseDto;
import com.carpBread.shareEatIt.domain.member.entity.Keywords;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.KeywordsRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordsRepository keywordsRepository;

    public KeywordResponseDto createNewKeyword(Member member, KeywordCreateRequestDto dto) {

        Keywords targetKeyword=null;

        // 해당 사용자가 사용하고 있는 경우
        if (keywordsRepository.existsByKeywordAndMember(dto.getKeyword(), member)){
            Keywords findKeyword = keywordsRepository.findByKeywordAndMember(dto.getKeyword(), member)
                    .orElseThrow(() -> new RuntimeException("domain.member.service.KeywordService inner server RUNTIME ERROR"));

            if (findKeyword.getAvail()){
//                throw new CustomException(CustomExceptionStatus.ALREADY_USING_KEYWORD,"이미 사용중인 Keyword 입니다","/keyword");
            }
            else{
                findKeyword.changeAvail(true);
                targetKeyword = keywordsRepository.save(findKeyword);
            }

        }
        else{
            // keyword 생성
            Keywords newKeyword = Keywords.builder()
                    .keyword(dto.getKeyword())
                    .avail(true)
                    .member(member)
                    .build();

            // keyword 저장
            targetKeyword = keywordsRepository.save(newKeyword);

        }

        return KeywordResponseDto.builder()
                .id(targetKeyword.getId())
                .keyword(targetKeyword.getKeyword())
                .avail(targetKeyword.getAvail())
                .build();

    }

    public KeywordAvailableListResponseDto getAllAvailKeywordList(Member member) {

        List<Keywords> allAvailKeywordList = keywordsRepository.findAllByMemberAndAvail(member, true);
        List<KeywordResponseDto> keywordList = new ArrayList<>();

        for (Keywords keyword : allAvailKeywordList){
            KeywordResponseDto keywordDto = KeywordResponseDto.builder()
                    .id(keyword.getId())
                    .avail(true)
                    .keyword(keyword.getKeyword())
                    .build();
            keywordList.add(keywordDto);
        }

        return KeywordAvailableListResponseDto.builder().keywordList(keywordList).build();

    }

    public KeywordAvailableListResponseDto getAllKeywordList(Member member) {
        List<Keywords> allKeywordList = keywordsRepository.findAllByMember(member);
        List<KeywordResponseDto> keywordList = new ArrayList<>();

        for (Keywords keyword : allKeywordList){
            KeywordResponseDto keywordDto = KeywordResponseDto.builder()
                    .id(keyword.getId())
                    .avail(keyword.getAvail())
                    .keyword(keyword.getKeyword())
                    .build();
            keywordList.add(keywordDto);
        }

        return KeywordAvailableListResponseDto.builder().keywordList(keywordList).build();

    }

    public KeywordResponseDto changeKeywordUsageToUnAvailable(Member member, Long id) {

        Keywords targetKeyword = keywordsRepository.findByMemberAndId(member, id)
                .orElseThrow(() -> null /* new CustomException(CustomExceptionStatus.NOT_FOUND_KEYWORD_UNAVAILABLE_ID, "update keyword unavailable - PATCH error", "/keyword/" + id)*/);

        targetKeyword.changeAvail(false);
        Keywords changedKeyword = keywordsRepository.save(targetKeyword);

        return KeywordResponseDto.builder()
                .keyword(changedKeyword.getKeyword())
                .avail(changedKeyword.getAvail())
                .id(changedKeyword.getId())
                .build();

    }

    public KeywordResponseDto deleteKeyword(Member member, Long id) {

        Keywords deleteKeyword = keywordsRepository.findByMemberAndId(member, id)
                .orElseThrow(() -> null /*new CustomException(CustomExceptionStatus.NOT_AVAILABLE_MEMBER_TO_DELETE_KEYWORD, "delete keyword - DELETE error", "/keyword/" + id)*/);

        keywordsRepository.delete(deleteKeyword);
        return KeywordResponseDto.builder()
                .id(deleteKeyword.getId())
                .avail(deleteKeyword.getAvail())
                .keyword(deleteKeyword.getKeyword())
                .build();
    }
}
