package com.sherlock.gmall.member.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.sherlock.common.Annotation.GmallMapping;
import com.sherlock.common.exception.BizCodeEnume;
import com.sherlock.gmall.member.exception.PhoneExistException;
import com.sherlock.gmall.member.exception.UserNameExistException;
import com.sherlock.gmall.member.feign.CouponFeignService;
import com.sherlock.gmall.member.vo.MemberLoginVo;
import com.sherlock.gmall.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sherlock.gmall.member.entity.MemberEntity;
import com.sherlock.gmall.member.service.MemberService;
import com.sherlock.common.utils.PageUtils;
import com.sherlock.common.utils.R;



/**
 * 会员
 *
 * @author sherlockXue
 * @email xuesherlock@gmail.com
 * @date 2020-05-10 01:04:30
 */
@RestController
@GmallMapping("member/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

    @RequestMapping("/callCoupon")
    public R callCoupon(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("Sherlock")
                .setCity("xi'an");
        return R.ok().put("member",memberEntity)
                .put("remoteCoupons",couponFeignService.couponList().get("coupons"));
    }

    @PostMapping("/regist")
    public R<String> regist(@RequestBody MemberRegistVo vo) {
        try {
            memberService.regist(vo);
        } catch (PhoneExistException phoneExist){
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UserNameExistException userNameExist){
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody MemberLoginVo vo) {
         MemberEntity memberEntity = memberService.login(vo);
         if (memberEntity != null) {
             return R.ok();
         } else {
            return R.error(BizCodeEnume.LOGINACC_PASSWORD_INVAILD_EXCEPTION.getCode(),BizCodeEnume.LOGINACC_PASSWORD_INVAILD_EXCEPTION.getMsg());
         }
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
