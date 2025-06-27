package com.myTMS.demo.controller;

import com.myTMS.demo.config.security.CustomUserDetails;
import com.myTMS.demo.dao.Post;
import com.myTMS.demo.dao.markerinterface.EmployeeAnswerGroup;
import com.myTMS.demo.dao.typeconst.MessageType;
import com.myTMS.demo.dao.typeconst.UserType;
import com.myTMS.demo.dao.users.Users;
import com.myTMS.demo.service.AlarmService;
import com.myTMS.demo.service.PostService;
import com.myTMS.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/cs")
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceController {

    @Value("${page.post.size}")
    private int pageSize;

    private final PostService postService;
    private final UserService userService;
    private final AlarmService alarmService;
    private final LocalValidatorFactoryBean validatorBean;


    /**
     * CS 서비스 페이지의 포스트를 페이징해주는 메소드
     *
     * @param data  page 와 keyword 를 포함한 Map
     * @param model 데이터를 담을 Model
     * @return 기본 페이지는 0이고 정렬 기준은 page Id 로 설정됐으며 keyword 데이터를 가질 수 있는
     * Page<Post> 객체를 모델에 담아서 csboard 페이지로 리턴
     */
    @GetMapping(value = "/post")
    public String csPage(@RequestParam(required = false) Map<String, String> data, Model model) {
        String pageNum = data.get("page");
        String keyword = data.get("keyword");

        PageRequest pageRequest = PageRequest.of(
                Optional.ofNullable(pageNum).map(Integer::parseInt).orElse(0), // 기본 페이지 번호를 0으로 설정
                pageSize,
                Sort.by(Sort.Direction.ASC, "id") // 정렬 기준을 id로 설정
        );

        Page<Post> postList;

        if (keyword != null) {  // 키워드 여부 확인
            postList = postService.getPostsByTitle(keyword, pageRequest);
        } else {
            postList = postService.getPostList(pageRequest);
        }

        model.addAttribute("postList", postList);

        return "valid/customerservice/csboard";
    }

    /**
     * CS 서비스 페이지의 상세 페이지를 가져오는 메서드
     * 나의 게시물인지 확인 후 boolean 값을 설정함
     *
     * @param id          PathVariable 로 넘어온 게시물 id
     * @param userDetails 세션에 저장된 유저 데이터
     * @return post 데이터를 model 에 담아 csboardinfo 페이지로 리턴
     */
    @GetMapping(value = "/post/{id}")
    public String csPostInfo(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Post post = postService.getPost(id).orElse(null);
        if (post.getUserId() == userDetails.getUserId()) {
            post.setMyPost(true);
        } else {
            post.setMyPost(false);
        }
        model.addAttribute("post", post);
        return "/valid/customerservice/csboardinfo";
    }

    /**
     * CS 페이지의 게시물 작성, 수정 을 위한 메서드
     * 기존에 존재하던 페이지라면 post 를 수정하고 이를 model 에 담아 전송함 (기존에 존재한 값을 매핑해주기 위해서)
     * 아니라면 새로운 post 를 생성하여 새로운 post 를 받아올 수 있도록 비어있는 객체를 보냄
     *
     * @param data id 를 포함한 Map
     * @return csboardedit 페이지로 리턴
     */
    @GetMapping(value = "/post/edit")
    public String csPostEdit(@RequestParam(required = false) Map<String, String> data, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String s = data.get("id");
        if (s != null) {
            Long id = Long.parseLong(s);
            Post post = postService.getPost(id).orElse(null);
            model.addAttribute("post", post);
        } else {
            model.addAttribute("post", new Post());
        }
        return "valid/customerservice/csboardedit";
    }

    /**
     * CS 페이지의 Post 데이터를 받아와서 검증한 후 db에 저장하는 메서드
     * id 가 null 이라면 새로운 post 를 생성하고
     * id 가 존재한다면 기존의 post 를 수정함
     * 비어있는 객체에서 받아온 데이터를 매핑하기 떄문에 lombok 에서 제공하는 accesslevel.none 은 사용하면 안 됨. (postId)
     *
     * @param post 자동 매핑된 post 객체 (th:Object)
     * @param br   유효성 검증에 위배될 시에 에러를 담는 객체
     * @return 다시 post 페이지로 redirect 시켜줌
     */
    @PostMapping(value = "/post/edit")
    public String csPostEditSubmit(@Valid @ModelAttribute(value = "post") Post post, BindingResult br, @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (br.hasErrors()) {
            return "valid/customerservice/csboardedit";
        }

        log.info("post = {}", post.toString());

        if (post.getId() == null) {
            post.setUserId(userDetails.getUserId());
            post.setWriter(userDetails.getUsername());
            post.setAnswered(false);
            post.setResponser(" ");
            postService.savePost(post);
            alarmService.sendAlarmToAll("alarm.post.create", true, MessageType.alarmMessage);
        } else {
            Post existingPost = postService.getPost(post.getId()).orElse(null);
            if (existingPost != null) {
                if (existingPost.getUserId().equals(userDetails.getUserId())) {
                    existingPost.setTitle(post.getTitle());
                    existingPost.setContent(post.getContent());
                    postService.updatePost(existingPost);
                }else {
                    return "redirect:/error";
                }
            }
        }
        return "redirect:/cs/post";
    }

    /**
     * 포스트 삭제를 위한 메서드 parameter 로 넘어온 map 데이터를 통해서 실제 작성자인지, 직원, 어드민인지 확인 후에 삭제함
     *
     * @param data postId 를 포함한 map
     * @return csboard 페이지로 redirect
     */
    @GetMapping(value = "/post/delete")
    public String csPostDelete(@RequestParam(required = true) Map<String, String> data, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String s = data.get("id");
        if (s != null) {
            Long id = Long.parseLong(s);
            Post post = postService.getPost(id).orElse(null);

            if (Objects.equals(post.getUserId(), userDetails.getUserId()) ||
                    userDetails.getUserType() == UserType.Employee ||
                    userDetails.getUserType() == UserType.Admin) {
                postService.deletePost(id);
            }
        }
        return "redirect:/cs/post";
    }

    @GetMapping(value = "/post/response/{id}")
    public String csPostResponse(@PathVariable(value = "id") Long postId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model, HttpServletRequest req) {
        if (userDetails.getUserType().equals(UserType.Employee) ||
                userDetails.getUserType().equals(UserType.Admin)) {
            postService.getPost(postId).ifPresent(post -> {
                model.addAttribute("post", post);
            });
            return "valid/customerservice/csboardresponse";
        }
        return "redirect:/error";
    }

    @PostMapping(value = "/post/response/edit")
    public String csPostResponseSubmit(@ModelAttribute(value = "post") Post post, BindingResult br, @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails.getUserType().equals(UserType.Employee)) {
            Validator validator = validatorBean.getValidator();
            Set<ConstraintViolation<Post>> violations = validator.validate(post, EmployeeAnswerGroup.class);

            for (ConstraintViolation<Post> violation : violations) {
                String fieldName = violation.getPropertyPath().toString();
                String message = violation.getMessage();
                br.rejectValue(fieldName, "", message);
            }
        }
        if (br.hasErrors()) {
            return "valid/customerservice/csboardresponse";
        }

        if (userDetails.getUserType().equals(UserType.Employee)) {
        postService.getPost(post.getId()).ifPresent(existingPost -> {
            Users users = userService.findByEmail(userDetails.getUsername()).orElse(null);
            if (users != null) {
                existingPost.setResponser(users.getFirstName() != null ? users.getFirstName() + " " + users.getLastName() : users.getEmail());
                existingPost.setAnswered(true);
                existingPost.setAnswerContent(post.getAnswerContent());
            }
            postService.savePost(existingPost);
            alarmService.sendAlarm(existingPost.getUserId(), "alarm.post.answered", false, MessageType.alarmMessage);
        });
        }else {
            return "redirect:/error";
        }



        return "redirect:/cs/post";
    }
}
