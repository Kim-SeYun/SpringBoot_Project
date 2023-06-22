package com.market.controller;

import com.market.dto.ItemFormDto;
import com.market.dto.ItemSearchDto;
import com.market.dto.ReviewFormDto;
import com.market.entity.Item;
import com.market.entity.Review;
import com.market.entity.ReviewImg;
import com.market.repository.ItemRepository;
import com.market.service.ItemService;
import com.market.service.ReviewImgService;
import com.market.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    private final ReviewService reviewService;

    private final ReviewImgService reviewImgService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    // 상품등록
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model, @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 값 입니다.");
            return "item/itemForm";
        }

        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        }catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){

        System.out.println("======================" + itemFormDto);
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

    @DeleteMapping(value = "/item/delete/{itemId}")
    public @ResponseBody ResponseEntity deleteItem(@PathVariable("itemId") Long itemId){

        itemService.deleteItem(itemId);
        return new ResponseEntity<Long>(itemId, HttpStatus.OK);
    }

//    @PostMapping(value = "/item/addReview")
//    public String addReview(@Valid @ModelAttribute("reviewFormDto")ReviewFormDto reviewFormDto,
//                            BindingResult bindingResult,
//                            Model model,
//                            @RequestParam("reviewImgFile") List<MultipartFile> reviewImgFileList,
//                            @RequestParam("itemId") Long itemId) {
//
//        if (bindingResult.hasErrors()) {
//            return "item/itemDtl";
//        }
//
//        if (reviewImgFileList.get(0).isEmpty() && reviewFormDto.getId() == null) {
//            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 값입니다.");
//            return "item/itemDtl";
//        }
//
//        try {
//            reviewService.saveReview(reviewFormDto, reviewImgFileList, itemId);
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
//            return "item/itemDtl";
//        }
//
//        return "redirect:/";
//    }


}