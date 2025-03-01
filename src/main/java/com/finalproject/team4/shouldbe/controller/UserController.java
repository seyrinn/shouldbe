package com.finalproject.team4.shouldbe.controller;

import com.finalproject.team4.shouldbe.service.*;
import com.finalproject.team4.shouldbe.util.EncryptUtil;
import com.finalproject.team4.shouldbe.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;
    EncryptUtil encrypt = new EncryptUtil();

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("logStatus") == "Y") {
            return "redirect:/";
        }
        return "login/login_form";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/create")
    public String create_membership(HttpSession session) {
        session.invalidate();//회원가입중 새로고침시 인증정보 날리기
        return "create_membership/create_membership";
    }

    @PostMapping("/create/sendcode")
    @ResponseBody
    public Map<String, Object> createSendCode(@RequestParam("email") String email, HttpSession session) {

        int result = userService.userCheckEmail(email);
        var map = new HashMap<String, Object>();

        if(result>1){
            //중복이메일
            map.put("result", false);
            map.put("msg", "Duplicated email error");
            return map;
        }

        //System.out.println(result);
        try {
            var authNum = emailService.sendAuthMail(email);
            session.setAttribute("authNum", authNum);
            session.setAttribute("authTime", System.currentTimeMillis());
            map.put("result", true);
            return map;
        } catch (Exception e) {
        }
        map.put("result", false);
        map.put("msg", "Mail service error");
        return map;
    }

    @PostMapping("/create/verify")
    @ResponseBody
    public boolean createVerifyCode(@RequestParam("code") String code, HttpSession session) {
        var time = (long) session.getAttribute("authTime");
        if (System.currentTimeMillis() > time + 1000 * 60 * 3) {
            //System.out.println("시간초과");
            session.removeAttribute("authNum");
            session.removeAttribute("authTime");
            return false;
        }
        if (!code.equals(session.getAttribute("authNum"))) {
            //System.out.println("번호미일치");
            return false;
        }
        //인증성공
        session.removeAttribute("authNum");
        session.removeAttribute("authTime");
        session.setAttribute("emailValid", "Y");

        return true;
    }

    @PostMapping("/createOk")
    public String createOk(UserVO vo, HttpSession session, RedirectAttributes redirect) {
        if (session.getAttribute("emailValid") != "Y" || session.getAttribute("idValid") != "Y") {
            redirect.addFlashAttribute("result", "이메일, 아이디 중복검사를 해주세요");
            return "redirect:/create";
        }

        //Salt 생성
        var salt = encrypt.getSalt();
        //패스워드 해싱
        var encrypted = encrypt.encrypt(vo.getUserpwd(), salt);

        //Salt, 패스워드 set
        vo.setUserpwd(encrypted);
        vo.setSalt(salt);

        //System.out.println(vo);

        int result = userService.useridInsert(vo);
        return "create_membership/create_success";
    }

    @PostMapping("/idcheck")
    @ResponseBody
    public boolean idCheck(@RequestParam("userid") String userid, HttpSession session) {
        int result = userService.useridCheck(userid);
        if (result == 0) {
            //중복x
            session.setAttribute("idValid", "Y");
            return true;
        } else {
            //중복
            return false;
        }

    }

    @PostMapping("/loginOk")
    public String loginOk(HttpSession session,
                          @RequestParam("userid") String userid,
                          @RequestParam("userpwd") String userpwd,
                          RedirectAttributes redirect) {
        LoginVO vo = userService.userLoginCheck(userid);
        System.out.println(vo);

        if (vo == null) {//로그인 실패
            redirect.addFlashAttribute("result", "로그인 실패, 아이디를 확인해주세요!");
            return "redirect:/login";
        } else if (vo.getWithdraw()!= null) {
            redirect.addFlashAttribute("result", "탈퇴 예정 회원입니다. 탈퇴를 취소하고 싶다면 문의해주세요.");
            return "redirect:/login";
        } else  if (vo.getSuspended_time() != null) {
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(vo.getSuspended_time());
            calendar.add(Calendar.DAY_OF_MONTH, vo.getSuspended_period());
            vo.setSuspended_time(calendar.getTime());
            if(vo.getSuspended_time().after(now)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String d = dateFormat.format(vo.getSuspended_time());
                redirect.addFlashAttribute("result", "정지 회원입니다. " + d + " 이후에 로그인이 가능합니다.");
                return "redirect:/login";
            }
        } else if (encrypt.encrypt(userpwd, vo.getSalt()).equals(vo.getPassword())) {
            userService.logUser(userid);
            session.setAttribute("logStatus", "Y");
            session.setAttribute("logName", vo.getUser_name());
            session.setAttribute("logId", userid);
            if(userService.ismanager(userid)){
                return "redirect:/admin/";
            }else{
                return "redirect:/";
            }
        }
        redirect.addFlashAttribute("result", "로그인 실패, 비밀번호를 확인해주세요!");
        return "redirect:/login";

    }


    @GetMapping("/login/findid")
    public String find_id() {
        return "login/find_id";
    }

    @GetMapping("/login/findpwd")
    public String find_pwd() {
        return "login/find_pwd";
    }

    @PostMapping("/login/findidOk")
    @ResponseBody
    public String findidOk(@RequestParam("username") String username, @RequestParam("email") String email) { //ajax
        String result = userService.userFindId(username, email);
        if (result != null) return result;
        return "";
    }

    @PostMapping("/login/sendcode")
    @ResponseBody
    public boolean sendCode(@RequestParam("userid") String userid, @RequestParam("email") String email, HttpSession session) {


        int result = userService.userCheckId(userid, email);
        //System.out.println(result);
        if (result == 1) {
            //email발송
            try {
                var authNum = emailService.sendAuthMail(email);
                // System.out.println(authNum);
                session.setAttribute("authNum", authNum);
                session.setAttribute("authTime", System.currentTimeMillis());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @PostMapping("/login/verify")
    @ResponseBody
    public boolean verifyCode(@RequestParam("code") String code, @RequestParam("userid") String userid, @RequestParam("email") String email, HttpSession session) {
        var time = (long) session.getAttribute("authTime");
        if (System.currentTimeMillis() > time + 1000 * 60 * 3) {
            //System.out.println("시간초과");
            session.removeAttribute("authNum");
            session.removeAttribute("authTime");
            return false;
        }
        if (!code.equals(session.getAttribute("authNum"))) {
            //System.out.println("번호미일치");
            session.removeAttribute("authNum");
            session.removeAttribute("authTime");
            return false;
        }
        session.removeAttribute("authNum");
        session.removeAttribute("authTime");
        //임시비밀번호 생성
        String newPass = emailService.sendNewPasswd(email);
        //Salt 생성
        var salt = encrypt.getSalt();
        //패스워드 해싱
        var encrypted = encrypt.encrypt(newPass, salt);
        UserVO vo = new UserVO();
        vo.setUserid(userid);
        vo.setSalt(salt);
        vo.setUserpwd(encrypted);
        int result = userService.userpwdUpdate(vo);

        return true;
    }
}
