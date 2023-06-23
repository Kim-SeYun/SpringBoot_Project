package com.market.controller;

import com.google.gson.Gson;
import com.market.dto.*;
import com.market.entity.Answer;
import com.market.entity.Inquiry;
import com.market.entity.Member;
import com.market.repository.InquiryRepository;
import com.market.repository.MemberRepository;
import com.market.service.AnswerService;
import com.market.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.market.entity.QInquiry.inquiry;

@RequestMapping("/inquiry")
@Controller
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    private final MemberRepository memberRepository;

    private final AnswerService answerService;

    private final InquiryRepository inquiryRepository;

    @GetMapping(value = {"/list", "/list/{page}"})
    public String inquiryList(Model model, @RequestParam(defaultValue = "1") @PathVariable(required = false) int page) {

        int pageSize = 8; // 한 페이지에 보여줄 글 수
        int totalItems = (int) inquiryService.getTotalInquiryCount();
        if (totalItems == 0) {
            totalItems = 1;
        }

        int totalPages = (int) Math.ceil(totalItems / (double) pageSize);

        if (page < 1) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        // 현재 페이지에 해당하는 글 조회
        List<Inquiry> inquiries = inquiryService.getInquiriesByPage(page, pageSize);

        // 페이지 번호 범위 계산
        int startPage = ((page - 1) / 5) * 5 + 1;
        int endPage = Math.min(startPage + 4, totalPages);

        model.addAttribute("inquiries", inquiries);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "inquiry/list";
    }

    @GetMapping(value = "/create")
    public String inquiryForm(Model model, Principal principal) {
        String userId = principal.getName();
        model.addAttribute("userId", userId);
        model.addAttribute("inquiryFormDto", new InquiryFormDto());
        return "inquiry/inquiryForm";
    }

    @PostMapping(value = "/create")
    public String inquiryNew(@Valid InquiryFormDto inquiryFormDto, BindingResult bindingResult, Model model,
                             @RequestParam("inquiryImgFile") List<MultipartFile> inquiryImgFileList,
                             @RequestParam String writer) {

        if (bindingResult.hasErrors()) {
            return "inquiry/inquiryForm";
        }

        Member member = memberRepository.findByEmail(writer);  // 적절한 repository 메서드를 사용하여 Member 객체를 조회해야 합니다.
        Long memberId = member.getId();
        inquiryFormDto.setMemberId(memberId);

        try {
            inquiryService.saveInquiry(inquiryFormDto, inquiryImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "문의글 등록 중 에러가 발생하였습니다.");
            return "inquiry/inquiryForm";
        }
        return "redirect:/inquiry/list";
    }

    @GetMapping(value = "/{id}")
    public String getInquiry(@PathVariable("id") Long inquiryId, Model model, Principal principal) {
        // 로그인 여부 확인
        boolean isLoggedIn = principal != null;

        // 문의 정보 조회
        InquiryFormDto inquiryFormDto = inquiryService.getInquiryById(inquiryId);
        int count = answerService.getCountById(inquiryId);

        if (count >= 1) {
            List<Answer> answers = answerService.getAnswerById(inquiryId);
            System.out.println(answers);
            model.addAttribute("answers", answers);
        }

        model.addAttribute("inquiryFormDto", inquiryFormDto);
        
        if (isLoggedIn) {
            String username = principal.getName();
            model.addAttribute("username", username);
        }
        return "inquiry/detail";
    }

    @PostMapping(value = "/modify/{id}")
    public String modify(@Valid InquiryFormDto inquiryFormDto, BindingResult bindingResult, @RequestParam("inquiryImgFile") List<MultipartFile> inquiryImgFileList, Model model) {

        System.out.println("================="+inquiryFormDto);
        if (bindingResult.hasErrors()){
            return "inquiry/detail";
        }

        try {
            inquiryService.update(inquiryFormDto, inquiryImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage", "문의글 수정 중 에러가 발생하였습니다.");
            return  "inquiry/detail";
        }
        return "redirect:/inquiry/" + inquiryFormDto.getId();
    }

    @DeleteMapping(value = "/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable("id") Long inquiryId) {
        inquiryService.deleteInquiry(inquiryId);

        Gson gson = new Gson();
        return gson.toJson(true);
    }

    @PostMapping(value = "/addComment")
    @ResponseBody
    public String addComment(@RequestParam Long inquiryId, @RequestParam String comment, Principal principal) {
        String username = principal.getName();

        Answer answer = new Answer();
        answer.setAnswerer(username);
        answer.setContent(comment);
        answer.setAnswerDate(LocalDateTime.now());

        Inquiry inquiry = inquiryRepository.getInquiryById(inquiryId);
        if (inquiry == null) {
            return "Inquiry not found";
        }

        answer.setInquiry(inquiry);

        answerService.save(answer);

        Gson gson = new Gson();
        return gson.toJson(true);
    }

    @DeleteMapping(value = "/deleteComment/{commentId}")
    @ResponseBody
    public String deleteAnswer(@PathVariable("commentId") Long commentId) {
        answerService.delete(commentId);

        Gson gson = new Gson();
        return gson.toJson(true);
    }

    @PostMapping(value = "/editComment/{commentId}")
    @ResponseBody
    public String editedAnswer(@PathVariable("commentId") Long commentId, @RequestParam String comment) {

        Optional<Answer> optionalAnswer = answerService.getAnswerById2(commentId);

        if (optionalAnswer.isPresent()) {
            Answer answer = optionalAnswer.get();
            answer.setContent(comment);
            answerService.update(answer);
        }
        Gson gson = new Gson();
        return gson.toJson(true);

    }
}