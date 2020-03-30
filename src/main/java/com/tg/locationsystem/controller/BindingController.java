package com.tg.locationsystem.controller;

import com.tg.locationsystem.entity.Goods;
import com.tg.locationsystem.entity.Myuser;
import com.tg.locationsystem.entity.Person;
import com.tg.locationsystem.entity.Tag;
import com.tg.locationsystem.pojo.ResultBean;
import com.tg.locationsystem.service.IGoodsService;
import com.tg.locationsystem.service.IPersonService;
import com.tg.locationsystem.service.ITagService;
import com.tg.locationsystem.utils.SystemMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author hyy
 * @ Date2019/6/27
 */
@Controller
@RequestMapping("binding")
public class BindingController {
    @Autowired
    private IPersonService personService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IGoodsService goodsService;

    /*
    * 人员与标签绑定
    * */
    @RequestMapping(value = "AddBindingByPerAndTag",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddBindingByPerAndTag(HttpServletRequest request,
                                            @RequestParam("person_idCard") String idCard,
                                            @RequestParam("tag_address") String address){
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(5);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (address==null||"".equals(address)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签address未填");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (idCard==null||"".equals(idCard)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("人员身份证号码未填");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Person person = personService.getPersonByIdCard(idCard);
        if (person==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("人员不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
       // System.out.println("人员绑定:"+idCard+"--->"+person.getTagAddress());
        if (!person.getTagAddress().equals("")){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该人员已绑定过价签");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Tag tag = tagService.getTagByAddress(address);
        if (tag==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if ("1".equals(tag.getUsed())){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该标签已被别人使用");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //对人员表和标签表做更新
        person.setTagAddress(address);
        tag.setUsed("1");
        System.out.println(tag.toString());
        int personupdate = personService.updateByPrimaryKeySelective(person);
        int tagupdate = tagService.updateByPrimaryKeySelective(tag);
        if (personupdate>0&&tagupdate>0){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("绑定成功");
            List list = new ArrayList<>();
            list.add(person);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("添加绑定失败");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //事务回滚?


    }

    /*
    * 解除人员标签绑定
    * */
    @RequestMapping(value = "DeleteBindingByPerAndTag",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean DeleteBindingByPerAndTag(HttpServletRequest request,
                                            @RequestParam("person_idCard") String idCard) {
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user == null) {
            resultBean = new ResultBean();
            resultBean.setCode(5);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Person person = personService.getPersonByIdCard(idCard);
        if (person==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("人员不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (person.getTagAddress()==null||"".equals(person.getTagAddress())) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("人员没有绑定标签,无法解绑");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        String tagAddress = person.getTagAddress();
        if ("".equals(tagAddress)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("人员没有绑定标签,无法解绑");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        Tag tag = tagService.getTagByAddress(person.getTagAddress());
        if (tag == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        //更新人员表和标签表
        person.setTagAddress("");
        tag.setUsed("0");

        int personupdate = personService.updateByPrimaryKeySelective(person);
        int tagupdate = tagService.updateByPrimaryKeySelective(tag);
        if (personupdate > 0 && tagupdate > 0) {
            //把该标签在缓存中的记录清掉
            SystemMap.getUsermap().remove(tag.getAddress());
            SystemMap.getTagAndPersonMap().remove(tag.getAddress());
            SystemMap.getCountmap().remove(tag.getAddress());

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("解绑成功");
            List list = new ArrayList<>();
            list.add(person);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("解绑失败");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }


    /*
    * 物品与标签绑定
    * */
    @RequestMapping(value = "AddBindingByGoodsAndTag",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddBindingByGoodsAndTag(HttpServletRequest request,
                                            @RequestParam("goods_idCard") String idCard,
                                            @RequestParam("tag_address") String address){
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(5);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (address==null||"".equals(address)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签address未填");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (idCard==null||"".equals(idCard)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("物品唯一标识未填");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Goods goods = goodsService.getGoodsByIdCard(idCard, user.getId());
        if (goods==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("物品不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (!goods.getTagAddress().equals("")){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("物品已经绑定标签,请先解绑");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Tag tag = tagService.getTagByAddress(address);
        if (tag==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if ("1".equals(tag.getUsed())){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该标签已被别人使用");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //对物品表和标签表做更新
        goods.setTagAddress(address);
        tag.setUsed("1");

        int goodsupdate = goodsService.updateByPrimaryKeySelective(goods);
        int tagupdate = tagService.updateByPrimaryKeySelective(tag);
        if (goodsupdate>0&&tagupdate>0){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("绑定成功");
            List list = new ArrayList<>();
            list.add(goods);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("添加绑定失败");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
     * 解除物品与标签绑定
     * */
    @RequestMapping(value = "DeleteBindingByGoodsAndTag",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean DeleteBindingByGoodsAndTag(HttpServletRequest request,
                                               @RequestParam("goods_idCard") String idCard) {
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user == null) {
            resultBean = new ResultBean();
            resultBean.setCode(5);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        Goods goods = goodsService.getGoodsByIdCard(idCard, user.getId());
        if (goods == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("物品不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (goods.getTagAddress() == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("物品没有绑定标签,无法解绑");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Tag tag = tagService.getTagByAddress(goods.getTagAddress());
        if (tag == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签不存在");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //更新物品表和标签表
        goods.setTagAddress("");
        tag.setUsed("0");

        int tagupdate = tagService.updateByPrimaryKeySelective(tag);
        int goodsupdate = goodsService.updateByPrimaryKeySelective(goods);

        if (goodsupdate > 0 && tagupdate > 0) {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("解绑成功");
            List list = new ArrayList<>();
            list.add(goods);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("解绑失败");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
/*
* 解除绑定
* 人,物与标签解绑
* */
@RequestMapping(value = "DeleteBindingTag",method = RequestMethod.POST)
@ResponseBody
public ResultBean DeleteBindingTag(HttpServletRequest request,
                                             @RequestParam("tagadd") String address) {
    ResultBean resultBean;
    Myuser user = (Myuser) request.getSession().getAttribute("user");
    //未登录
    if (user == null) {
        resultBean = new ResultBean();
        resultBean.setCode(5);
            resultBean.setMsg("还未登录");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    Tag tag = tagService.getTagByAddress(address);
    if (tag==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("标签不存在");
        List list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    Person person = personService.getPersonByAddress(user.getId(), address);
    if (person!=null){
        //更新人员表和标签表
        person.setTagAddress("");
        tag.setUsed("0");

        int personupdate = personService.updateByPrimaryKeySelective(person);
        int tagupdate = tagService.updateByPrimaryKeySelective(tag);
        if (personupdate > 0 && tagupdate > 0) {
            //把该标签在缓存中的记录清掉
            SystemMap.getUsermap().remove(tag.getAddress());
            SystemMap.getTagAndPersonMap().remove(tag.getAddress());
            SystemMap.getCountmap().remove(tag.getAddress());

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("解绑成功");
            List list = new ArrayList<>();
            list.add(person);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
    Goods goods = goodsService.getGoodsByAddress(user.getId(), address);
    if (goods!=null){
        //更新物品表和标签表
        goods.setTagAddress("");
        tag.setUsed("0");

        int tagupdate = tagService.updateByPrimaryKeySelective(tag);
        int goodsupdate = goodsService.updateByPrimaryKeySelective(goods);

        if (goodsupdate > 0 && tagupdate > 0) {
            //把该标签在缓存中的记录清掉
            SystemMap.getUsermap().remove(tag.getAddress());
            SystemMap.getTagAndPersonMap().remove(tag.getAddress());
            SystemMap.getCountmap().remove(tag.getAddress());

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("解绑成功");
            List list = new ArrayList<>();
            list.add(goods);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
    resultBean = new ResultBean();
    resultBean.setCode(-1);
    resultBean.setMsg("解绑失败");
    List list = new ArrayList<>();
    resultBean.setData(list);
    resultBean.setSize(list.size());
    return resultBean;
}
}

