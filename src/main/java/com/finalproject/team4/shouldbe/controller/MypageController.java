package com.finalproject.team4.shouldbe.controller;

import com.finalproject.team4.shouldbe.service.MypageService;
import com.finalproject.team4.shouldbe.util.EncryptUtil;
import com.finalproject.team4.shouldbe.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class MypageController {
    @Autowired
    MypageService service;

    EncryptUtil encrypt = new EncryptUtil();

    // 마이페이지 프로필
    @GetMapping("/mypage/change_user")
    public ModelAndView mypage_change_user(@SessionAttribute(name = "logId") String userid) {
        ModelAndView mav = new ModelAndView();
        MypageVO myvo = service.mypage_info(userid);
        System.out.println(myvo.toString());
        mav.addObject("myvo", myvo);
        mav.setViewName("mypage/change_user");
        return mav;
    }

    // 프로필 수정
    @PostMapping("/mypage/editProfileOk")
    public ModelAndView mypageEidt(MypageVO vo) {
        ModelAndView mav = new ModelAndView();
        System.out.println(vo.toString());

        int result = service.mypage_edit(vo);

        if (result > 0) {
            mav.setViewName("redirect:mypage/change_user");
        } else {
            mav.setViewName("redirect:mypage/mypage_editResult");
        }
        return mav;
    }

    // 친구목록
    @GetMapping("/mypage/friend_user")
    public ModelAndView mypage_friend_user(HttpSession session) {
        ModelAndView mav = new ModelAndView();
        String followed_user_id = (String) session.getAttribute("logId");

        List<FriendVO> flist = service.friendList(followed_user_id);

        mav.addObject("flist", flist);
        mav.setViewName("mypage/friend_user");
        return mav;
    }

    // 친구삭제
    @GetMapping("/mypage/deletefriend")
    @ResponseBody
    public String friendDelete(String followed_user_id, String following_user_id) {
        return service.friendDelete(followed_user_id, following_user_id) + "";
    }

    // 차단목록
    @GetMapping("/mypage/blockList")
    public ModelAndView mypage_blocklist(@SessionAttribute(name = "logId") String user_id) {
        ModelAndView mav = new ModelAndView();

        List<BlockVO> blist = service.blockList(user_id);
        System.out.println(blist);
        mav.addObject("blist", blist);
        mav.setViewName("mypage/blocklist_user");
        return mav;
    }
    
    // 차단 해제
    @GetMapping("/mypage/unlock")
    @ResponseBody
    public String unlock(@RequestParam("block_id") String block_id, @RequestParam("user_id") String user_id){
        return service.block_id_unlock(block_id, user_id)+"";
    }

    // 게시글
    @GetMapping("/mypage/post_user")
    public ModelAndView mypage_post_user(@SessionAttribute(name = "logId") String user_id) {
        ModelAndView mav = new ModelAndView();
        List<BoardVO> list = service.mypage_post_board(user_id);

        mav.addObject("list", list);
        mav.setViewName("mypage/post_user");
        return mav;
    }

    // 댓글
    @GetMapping("/mypage/post_user/reply")
    public ModelAndView mypage_post_user_reply(@SessionAttribute(name = "logId") String user_id) {
        ModelAndView mav = new ModelAndView();
        List<BoardReplyVO> list = service.mypage_post_board_reply(user_id);

        mav.addObject("list", list);
        mav.setViewName("mypage/post_user_reply");
        return mav;
    }

    // 회원탈퇴
    @GetMapping("/mypage/withdraw_user")
    public String mypage_withdraw_user() {
        return "mypage/withdraw_user";
    }
    // 회원탈퇴
    @GetMapping("/mypage/withdrawOk")
    public String withdrawOk(@RequestParam("user_id") String user_id, @RequestParam("password") String password, RedirectAttributes redirect, HttpSession session){
        MypageVO mVo = service.mypage_info(user_id);
        System.out.println(mVo.toString());

        if (mVo == null) {
            return "redirect:/mypage/withdraw_user";
        } else if (encrypt.encrypt(password, mVo.getSalt()).equals(mVo.getPassword())) {
            int result = service.withdraw_user_date(user_id);
            int wresult = service.withdraw_user(user_id);
            System.out.println("result:" + result);
            System.out.println("wresult:" + wresult);
            session.invalidate();
            return "redirect:/";
        }

        redirect.addFlashAttribute("result", "회원탈퇴 실패, 비밀번호를 확인해주세요!");
        return "redirect:/mypage/withdraw_user";
    }

    // 저장소
    @GetMapping("/mypage/save_user")
    public ModelAndView saveUser(HttpSession session, @RequestParam(required = false, defaultValue = "1") int page) {
        ModelAndView mav = new ModelAndView("mypage/save_user");

        String userId = (String) session.getAttribute("logId");
        PagingVO pvo = new PagingVO();
        pvo.setOnePageRecord(10);
        pvo.setNowPage(page);
        pvo.setTotalRecord(service.totalRecord(userId));
        List<SaveMessageVO> saveMessages = service.getSaveMessageList(pvo, userId);
        mav.addObject("saveMessages", saveMessages);
        mav.addObject("pVO", pvo);

        return mav;
    }
}