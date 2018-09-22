package com.pyg.user.controller;
import java.util.Date;
import java.util.List;

import com.pyg.util.PhoneFormatCheckUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.CharSequenceUtils;
import org.apache.commons.lang3.CharSetUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbUser;
import com.pyg.user.service.UserService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){


		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String code){
		try {
			String password = user.getPassword();
			user.setPassword(DigestUtils.md5Hex(password));
			user.setCreated(new Date());
			user.setUpdated(new Date());
			user.setSourceType("1");
			//校验验证码
			boolean flag=userService.checkSmsCode(user.getPhone(),code);
			if(!flag){
				return new Result(false, "验证码错误");
			}
			userService.add(user);
			return new Result(true, "注册成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "注册失败");
		}
	}

	/**
	 * 验证码
	 */
	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		try {
			boolean phoneLegal = PhoneFormatCheckUtils.isPhoneLegal(phone);
			//校验手机
			if(!phoneLegal){
				return new Result(false, "手机号格式不正确");
			}


			userService.createCode(phone);
			return new Result(true, "发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "发送失败");
		}
	}

	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}
	
}
