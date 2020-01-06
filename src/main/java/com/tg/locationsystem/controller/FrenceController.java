package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.mapper.FrenceMapper;
import com.tg.locationsystem.mapper.GoodsMapper;
import com.tg.locationsystem.mapper.PersonMapper;
import com.tg.locationsystem.pojo.*;
import com.tg.locationsystem.service.IFrenceHistoryService;
import com.tg.locationsystem.service.IFrenceService;
import com.tg.locationsystem.service.IMapService;
import com.tg.locationsystem.utils.StringUtils;
import com.tg.locationsystem.utils.SystemMap;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author hyy
 * @ Date2019/7/4
 */
@Controller
@RequestMapping("frence")
public class FrenceController {
    @Autowired
    private IFrenceService frenceService;
    @Autowired
    private IFrenceHistoryService frenceHistoryService;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private FrenceMapper frenceMapper;
    @Autowired
    private IMapService mapService;

    /*
     * 添加电子围栏
     * */
    @RequestMapping(value = "AddFrence", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddFrence(@Valid Frence frence, BindingResult result,
                                HttpServletRequest request) {
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
        //有必填项没填
        if (result.hasErrors()) {
            List<String> errorlist = new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = field + ":" + fieldError.getDefaultMessage();
                //System.out.println(field + ":" + message);
                errorlist.add(message);
            });
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }

        //围栏经存在
        Frence myfrence = frenceService.getFrenceByname(frence.getName(), user.getId());
        if (myfrence != null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("添加电子围栏,该围栏已经存在");
            List<Frence> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //设置围栏所属管理者
        frence.setUserId(user.getId());
        //默认打开开关
        frence.setSetSwitch("1");

        int insert = frenceService.insertSelective(frence);
        if (insert > 0) {
            //把围栏添加到缓存中
            Map<Integer, List<Frence>> frencemap = SystemMap.getFrencemap();
            List<Frence> frenceList = frencemap.get(user.getId());
            if (frenceList == null) {
                frenceList = new ArrayList<>();
                frenceList.add(frence);
                frencemap.put(user.getId(), frenceList);
            } else {
                frenceList.add(frence);
                frencemap.put(user.getId(), frenceList);
            }


            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("围栏添加成功");
            List<Frence> list = new ArrayList<>();
            list.add(frence);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("围栏添加失败");
            List<Frence> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
     * 查看已存在的电子围栏
     * */
    @RequestMapping(value = "QueryFrence", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean QueryFrence(HttpServletRequest request,
                                  @RequestParam(defaultValue = "1") Integer pageIndex,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
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
        PageInfo<Frence> pageInfo = frenceService.getfrenceByUserId(pageIndex, pageSize, user.getId());
        List<FrenceVO> frenceVO2List = new ArrayList<>();
        for (Frence frence : pageInfo.getList()) {
            FrenceVO frenceVO2 = new FrenceVO();
            frenceVO2.setId(frence.getId());
            frenceVO2.setUserId(frence.getUserId());
            frenceVO2.setPolyline(frence.getPolyline());
            frenceVO2.setPhone(frence.getPhone());
            frenceVO2.setData(frence.getData());
            frenceVO2.setName(frence.getName());
            frenceVO2.setMapKey(frence.getMapKey());
            frenceVO2.setSetSwitch(frence.getSetSwitch());

            com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(frence.getMapKey());
            if (map != null) {
                frenceVO2.setMapName(map.getMapName());
            }
            frenceVO2.setType(frence.getType());
            String type = frence.getType();
            if ("in".equals(type)) {
                frenceVO2.setType("进入");
            }
            if ("out".equals(type)) {
                frenceVO2.setType("离开");
            }
            if ("on".equals(type)) {
                frenceVO2.setType("停留");
            }
            frenceVO2List.add(frenceVO2);
        }
        PageInfo<FrenceVO> page = new PageInfo<>(frenceVO2List);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(frenceVO2List);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list = new ArrayList<>();
        list.add(page);
        resultBean.setData(list);
        resultBean.setSize(page.getSize());
        return resultBean;
    }

    /*
     * 删除电子围栏
     * */
    @RequestMapping(value = "deleteFrence", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean deleteFrence(HttpServletRequest request,
                                   @RequestParam("frenceid") Integer frenceid) {
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
        Frence frence = frenceService.selectByPrimaryKey(frenceid);
        if (frence == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该围栏不存在");
            List<Frence> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //清掉在缓存的记录
        List<Frence> frenceList1 = SystemMap.getFrencemap().get(user.getId());
        if (frenceList1 != null) {
            frenceList1.remove(frence);
            SystemMap.getFrencemap().put(user.getId(), frenceList1);
        }


        int delete = frenceService.deleteByPrimaryKey(frenceid);

        if (delete > 0) {
            //删除围栏报警记录
            int deleteHistoryByFrenceId = frenceHistoryService.deleteHistoryByFrenceId(frenceid);


            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("删除围栏成功");
            List<Frence> frenceList = new ArrayList<>();
            frenceList.add(frence);
            resultBean.setData(frenceList);
            resultBean.setSize(frenceList.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("删除围栏失败");
            List<Frence> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
     *查看电子围栏警报记录
     * */
    @RequestMapping(value = "getFrenceHistoryPage", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getFrenceHistoryPage(HttpServletRequest request,
                                           @RequestParam(defaultValue = "1") Integer pageIndex,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
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
        //System.out.println("用户:"+user.toString());
        PageInfo<FrenceHistory> pageInfo = frenceHistoryService.getFrenceHistoryPage(pageIndex, pageSize, user.getId());

        List<FrenceHistoryVO> frenceVOList = new ArrayList<>();
        for (FrenceHistory frenceHistory : pageInfo.getList()) {
            FrenceHistoryVO frenceVO = new FrenceHistoryVO();
            frenceVO.setId(frenceHistory.getId());
            frenceVO.setPersonIdcard(frenceHistory.getPersonIdcard());
            frenceVO.setX(frenceHistory.getX());
            frenceVO.setY(frenceHistory.getY());
            frenceVO.setStatus(frenceHistory.getStatus());
            frenceVO.setTime(frenceHistory.getTime());
            frenceVO.setFrenceId(frenceHistory.getFrenceId());
            frenceVO.setUserId(frenceHistory.getUserId());
            frenceVO.setMapkey(frenceHistory.getMapKey());

            com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(frenceHistory.getMapKey());
            if (map != null) {
                frenceVO.setMapName(map.getMapName());
            }
            Frence frence = frenceMapper.selectByPrimaryKey(frenceHistory.getFrenceId());
            if (frence != null) {
                frenceVO.setFrenceName(frence.getName());
                String type = frence.getType();
                frenceVO.setAlert_type(type);
                if ("in".equals(type)) {
                    frenceVO.setAlert_type("进入");
                }
                if ("out".equals(type)) {
                    frenceVO.setAlert_type("离开");
                }
                if ("on".equals(type)) {
                    frenceVO.setAlert_type("停留");
                }
                frenceVO.setData(frence.getData());
            }
            if ("0".equals(frenceHistory.getStatus())) {
                frenceVO.setIsDeal("未处理");
            }
            if ("1".equals(frenceHistory.getStatus())) {
                frenceVO.setIsDeal("已处理");
            }
            Person person = personMapper.getPersonByIdCard(frenceHistory.getPersonIdcard());
            if (person != null) {
                frenceVO.setTagName(person.getPersonName());
                frenceVO.setImg(person.getImg());
            } else {
                Goods goods = goodsMapper.getGoodsByByIdCard(frenceHistory.getPersonIdcard());
                if (goods != null) {
                    frenceVO.setTagName(goods.getGoodsName());
                    frenceVO.setImg(goods.getImg());
                }
            }
            frenceVOList.add(frenceVO);
        }

        PageInfo<FrenceHistoryVO> page = new PageInfo<>(frenceVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(frenceVOList);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list = new ArrayList<>();
        list.add(page);
        resultBean.setData(list);
        resultBean.setSize(page.getSize());
        return resultBean;
    }

    /*
     * 查看具体某个电子围栏警报记录
     * */
    @RequestMapping(value = "getFrenceHistory", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getFrenceHistory(HttpServletRequest request,
                                       @RequestParam("frenceid") Integer frenceid) {

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
        List<FrenceHistoryVO> frenceHistoryList = frenceHistoryService.getFrenceHistory(frenceid, user.getId());
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(frenceHistoryList);
        resultBean.setSize(frenceHistoryList.size());
        return resultBean;
    }

    /*
     * 修改单个围栏信息
     * */
    @RequestMapping(value = "UpdateFrence", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean UpdateFrence(@Valid Frence frence, BindingResult result,
                                   HttpServletRequest request) {
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

        //有必填项没填
        if (result.hasErrors()) {
            List<String> errorlist = new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = field + ":" + fieldError.getDefaultMessage();
                //System.out.println(field + ":" + message);
                errorlist.add(message);
            });
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }
        if (frence.getId() == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("id不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        Frence frence1 = frenceService.selectByPrimaryKey(frence.getId());
        if (frence1 == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该围栏不存在");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //设置围栏所属管理者
        frence.setUserId(user.getId());

        //修改
        int update = frenceService.updateByPrimaryKey(frence);
        if (update > 0) {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("围栏修改成功");
            List<Frence> list = new ArrayList<>();
            list.add(frence);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("围栏修改失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }

    /*
     * 模糊查询
     * 关键字搜索围栏
     * 分页
     * */
    @RequestMapping(value = "getFrenceByConditionPage", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getFrenceByConditionPage(HttpServletRequest request,
                                               @RequestParam(defaultValue = "1") Integer pageIndex,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @RequestParam(defaultValue = "") String msg) {
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
        if ("".equals(msg)) {
            PageInfo<Frence> pageInfo = frenceService.getfrenceByUserId(pageIndex, pageSize, user.getId());
            List<FrenceVO> frenceVO2List = new ArrayList<>();
            for (Frence frence : pageInfo.getList()) {
                FrenceVO frenceVO2 = new FrenceVO();
                frenceVO2.setId(frence.getId());
                frenceVO2.setUserId(frence.getUserId());
                frenceVO2.setPolyline(frence.getPolyline());
                frenceVO2.setPhone(frence.getPhone());
                frenceVO2.setData(frence.getData());
                frenceVO2.setName(frence.getName());
                frenceVO2.setMapKey(frence.getMapKey());
                frenceVO2.setSetSwitch(frence.getSetSwitch());
                com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(frence.getMapKey());
                if (map!=null){
                    frenceVO2.setMapName(map.getMapName());
                }
                //
                frenceVO2.setType(frence.getType());
                String type = frence.getType();
                if ("in".equals(type)) {
                    frenceVO2.setType("进入");
                }
                if ("out".equals(type)) {
                    frenceVO2.setType("离开");
                }
                if ("on".equals(type)) {
                    frenceVO2.setType("停留");
                }
                frenceVO2List.add(frenceVO2);
            }
            PageInfo<FrenceVO> page = new PageInfo<>(frenceVO2List);
            page.setPageNum(pageInfo.getPageNum());
            page.setSize(pageInfo.getSize());
            page.setSize(pageInfo.getSize());
            page.setStartRow(pageInfo.getStartRow());
            page.setEndRow(pageInfo.getEndRow());
            page.setTotal(pageInfo.getTotal());
            page.setPages(pageInfo.getPages());
            page.setList(frenceVO2List);
            page.setPrePage(pageInfo.getPrePage());
            page.setNextPage(pageInfo.getNextPage());
            page.setIsFirstPage(pageInfo.isIsFirstPage());
            page.setIsLastPage(pageInfo.isIsLastPage());

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List list = new ArrayList<>();
            list.add(page);
            resultBean.setData(list);
            resultBean.setSize(page.getSize());
            return resultBean;
        }
        msg = msg.replace(" ", "");
        msg = msg.replace("进入", "in");
        msg = msg.replace("离开", "out");
        msg = msg.replace("停留", "on");

        PageInfo<Frence> pageInfo = frenceService.getFrenceByConditionPage(pageIndex, pageSize, user.getId(), msg);
        List<FrenceVO> frenceVO2List = new ArrayList<>();
        for (Frence frence : pageInfo.getList()) {
            FrenceVO frenceVO2 = new FrenceVO();
            frenceVO2.setId(frence.getId());
            frenceVO2.setUserId(frence.getUserId());
            frenceVO2.setPolyline(frence.getPolyline());
            frenceVO2.setPhone(frence.getPhone());
            frenceVO2.setData(frence.getData());
            frenceVO2.setName(frence.getName());
            frenceVO2.setMapKey(frence.getMapKey());
            frenceVO2.setSetSwitch(frence.getSetSwitch());
            com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(frence.getMapKey());
            if (map!=null){
                frenceVO2.setMapName(map.getMapName());
            }

            //
            frenceVO2.setType(frence.getType());
            String type = frence.getType();
            if ("in".equals(type)) {
                frenceVO2.setType("进入");
            }
            if ("out".equals(type)) {
                frenceVO2.setType("离开");
            }
            if ("on".equals(type)) {
                frenceVO2.setType("停留");
            }
            frenceVO2List.add(frenceVO2);
        }
        PageInfo<FrenceVO> page = new PageInfo<>(frenceVO2List);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(frenceVO2List);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list = new ArrayList<>();
        list.add(page);
        resultBean.setData(list);
        resultBean.setSize(page.getSize());
        return resultBean;
    }
    /*
     * 将围栏警报设为已处理
     *
     *
     * */

    @RequestMapping(value = "dealFrenceHistory", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultBean dealFrenceHistory(@Param("") Integer frenceHistoryid,
                                        HttpServletRequest request
    ) {

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
        if (frenceHistoryid == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("围栏报警id不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        FrenceHistory frenceHistory = frenceHistoryService.selectByPrimaryKey(frenceHistoryid);
        if (frenceHistory != null) {
            frenceHistory.setStatus("1");
            int update = frenceHistoryService.updateByPrimaryKeySelective(frenceHistory);
            if (update > 0) {
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("围栏报警处理成功");
                List<FrenceHistory> list = new ArrayList<>();
                list.add(frenceHistory);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            } else {
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("围栏报警处理失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("围栏报警处理失败");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

    /*
     * 删除围栏警报
     *
     *
     * */

    @RequestMapping(value = "deleteFrenceHistory", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultBean deleteFrenceHistory(@Param("") Integer frenceHistoryid,
                                          HttpServletRequest request) {
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
        if (frenceHistoryid == null) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("围栏报警id不能为空");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        int delete = frenceHistoryService.deleteByPrimaryKey(frenceHistoryid);

        if (delete > 0) {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("删除围栏警报成功");
            List<FrenceHistory> frenceList = new ArrayList<>();
            resultBean.setData(frenceList);
            resultBean.setSize(frenceList.size());
            return resultBean;
        } else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("删除围栏警报失败");
            List<Frence> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
    /*
    * 根据条件搜索相关围栏报警记录
    * */
    @RequestMapping(value = "getFrenceHistoryByConditionPage", method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getFrenceHistoryByConditionPage(HttpServletRequest request,
                                               QueryFrenceHistoryCondition frenceHistoryCondition,
                                               @RequestParam(defaultValue = "1") Integer pageIndex,
                                               @RequestParam(defaultValue = "10") Integer pageSize
                                               ) {
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
        //没有条件 查全部
    if (frenceHistoryCondition.getFrenceId()==null&&frenceHistoryCondition.getPersonName()==null){
        System.out.println("没有条件 查全部");
            PageInfo<FrenceHistory> pageInfo = frenceHistoryService.getFrenceHistoryPage(pageIndex, pageSize, user.getId());

        List<FrenceHistoryVO> frenceVOList = new ArrayList<>();
        for (FrenceHistory frenceHistory : pageInfo.getList()) {
            FrenceHistoryVO frenceVO = new FrenceHistoryVO();
            frenceVO.setId(frenceHistory.getId());
            frenceVO.setPersonIdcard(frenceHistory.getPersonIdcard());
            frenceVO.setX(frenceHistory.getX());
            frenceVO.setY(frenceHistory.getY());
            frenceVO.setStatus(frenceHistory.getStatus());
            frenceVO.setTime(frenceHistory.getTime());
            frenceVO.setFrenceId(frenceHistory.getFrenceId());
            frenceVO.setUserId(frenceHistory.getUserId());
            frenceVO.setMapkey(frenceHistory.getMapKey());
            com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(frenceHistory.getMapKey());
            if (map!=null){
                frenceVO.setMapName(map.getMapName());
            }

            Frence frence = frenceMapper.selectByPrimaryKey(frenceHistory.getFrenceId());
            if (frence != null) {
                frenceVO.setFrenceName(frence.getName());
                String type = frence.getType();
                frenceVO.setAlert_type(type);
                if ("in".equals(type)) {
                    frenceVO.setAlert_type("进入");
                }
                if ("out".equals(type)) {
                    frenceVO.setAlert_type("离开");
                }
                if ("on".equals(type)) {
                    frenceVO.setAlert_type("停留");
                }
                frenceVO.setData(frence.getData());
            }
            if ("0".equals(frenceHistory.getStatus())) {
                frenceVO.setIsDeal("未处理");
            }
            if ("1".equals(frenceHistory.getStatus())) {
                frenceVO.setIsDeal("已处理");
            }
            Person person = personMapper.getPersonByIdCard(frenceHistory.getPersonIdcard());
            if (person != null) {
                frenceVO.setTagName(person.getPersonName());
                frenceVO.setImg(person.getImg());
            } else {
                Goods goods = goodsMapper.getGoodsByByIdCard(frenceHistory.getPersonIdcard());
                if (goods != null) {
                    frenceVO.setTagName(goods.getGoodsName());
                    frenceVO.setImg(goods.getImg());
                }
            }
            frenceVOList.add(frenceVO);
        }

        PageInfo<FrenceHistoryVO> page = new PageInfo<>(frenceVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(frenceVOList);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list = new ArrayList<>();
        list.add(page);
        resultBean.setData(list);
        resultBean.setSize(page.getSize());
        return resultBean;
    }
    //有条件 只根据围栏id查询
   else if (frenceHistoryCondition.getFrenceId()!=null&&frenceHistoryCondition.getPersonName()==null){
        //System.out.println("只根据围栏id查询");
        PageInfo<FrenceHistory> pageInfo = frenceHistoryService.getFrenceHistoryByFrenceId(pageIndex, pageSize, user.getId(),frenceHistoryCondition.getFrenceId());
        List<FrenceHistoryVO> frenceVOList = new ArrayList<>();
        for (FrenceHistory frenceHistory : pageInfo.getList()) {
            FrenceHistoryVO frenceVO = new FrenceHistoryVO();
            frenceVO.setId(frenceHistory.getId());
            frenceVO.setPersonIdcard(frenceHistory.getPersonIdcard());
            frenceVO.setX(frenceHistory.getX());
            frenceVO.setY(frenceHistory.getY());
            frenceVO.setStatus(frenceHistory.getStatus());
            frenceVO.setTime(frenceHistory.getTime());
            frenceVO.setFrenceId(frenceHistory.getFrenceId());
            frenceVO.setUserId(frenceHistory.getUserId());
            frenceVO.setMapkey(frenceHistory.getMapKey());
            com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(frenceHistory.getMapKey());
            if (map!=null){
                frenceVO.setMapName(map.getMapName());
            }
            Frence frence = frenceMapper.selectByPrimaryKey(frenceHistory.getFrenceId());
            if (frence != null) {
                frenceVO.setFrenceName(frence.getName());
                String type = frence.getType();
                frenceVO.setAlert_type(type);
                if ("in".equals(type)) {
                    frenceVO.setAlert_type("进入");
                }
                if ("out".equals(type)) {
                    frenceVO.setAlert_type("离开");
                }
                if ("on".equals(type)) {
                    frenceVO.setAlert_type("停留");
                }
                frenceVO.setData(frence.getData());
            }
            if ("0".equals(frenceHistory.getStatus())) {
                frenceVO.setIsDeal("未处理");
            }
            if ("1".equals(frenceHistory.getStatus())) {
                frenceVO.setIsDeal("已处理");
            }
            Person person = personMapper.getPersonByIdCard(frenceHistory.getPersonIdcard());
            if (person != null) {
                frenceVO.setTagName(person.getPersonName());
                frenceVO.setImg(person.getImg());
            } else {
                Goods goods = goodsMapper.getGoodsByByIdCard(frenceHistory.getPersonIdcard());
                if (goods != null) {
                    frenceVO.setTagName(goods.getGoodsName());
                    frenceVO.setImg(goods.getImg());
                }
            }
            frenceVOList.add(frenceVO);
        }

        PageInfo<FrenceHistoryVO> page = new PageInfo<>(frenceVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(frenceVOList);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list = new ArrayList<>();
        list.add(page);
        resultBean.setData(list);
        resultBean.setSize(page.getSize());
        return resultBean;
    }
    //只根据人员名称 模糊查询
    else if (frenceHistoryCondition.getFrenceId()==null&&frenceHistoryCondition.getPersonName()!=null){
        //System.out.println("只根据人员名称");
            List<Person> personList=personMapper.getPersonsByName(user.getId(),frenceHistoryCondition.getPersonName());
        List<FrenceHistoryVO> frenceVOList = new ArrayList<>();
             if (personList.size()>0){
                 List<String> list=new ArrayList<>();
                 PageInfo<FrenceHistory> pageInfo=null ;
                 if (personList.size()>0){
                     StringBuffer sb=new StringBuffer();
                     for (Person person : personList) {
                         list.add(person.getIdCard());
                     }
                     pageInfo = frenceHistoryService.getFrenceHistoryByPersonIdcardss(pageIndex,pageSize,list);
                     for (FrenceHistory frenceHistory :pageInfo.getList()) {
                         FrenceHistoryVO frenceVO = new FrenceHistoryVO();
                         frenceVO.setId(frenceHistory.getId());
                         frenceVO.setPersonIdcard(frenceHistory.getPersonIdcard());
                         frenceVO.setX(frenceHistory.getX());
                         frenceVO.setY(frenceHistory.getY());
                         frenceVO.setStatus(frenceHistory.getStatus());
                         frenceVO.setTime(frenceHistory.getTime());
                         frenceVO.setFrenceId(frenceHistory.getFrenceId());
                         frenceVO.setUserId(frenceHistory.getUserId());
                         frenceVO.setMapkey(frenceHistory.getMapKey());
                         com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(frenceHistory.getMapKey());
                         if (map!=null){
                             frenceVO.setMapName(map.getMapName());
                         }
                         Frence frence = frenceMapper.selectByPrimaryKey(frenceHistory.getFrenceId());
                         if (frence != null) {
                             frenceVO.setFrenceName(frence.getName());
                             String type = frence.getType();
                             frenceVO.setAlert_type(type);
                             if ("in".equals(type)) {
                                 frenceVO.setAlert_type("进入");
                             }
                             if ("out".equals(type)) {
                                 frenceVO.setAlert_type("离开");
                             }
                             if ("on".equals(type)) {
                                 frenceVO.setAlert_type("停留");
                             }
                             frenceVO.setData(frence.getData());
                         }
                         if ("0".equals(frenceHistory.getStatus())) {
                             frenceVO.setIsDeal("未处理");
                         }
                         if ("1".equals(frenceHistory.getStatus())) {
                             frenceVO.setIsDeal("已处理");
                         }
                         Person sqlperson = personMapper.getPersonByIdCard(frenceHistory.getPersonIdcard());
                         if (sqlperson != null) {
                             frenceVO.setTagName(sqlperson.getPersonName());
                             frenceVO.setImg(sqlperson.getImg());
                         } else {
                             Goods goods = goodsMapper.getGoodsByByIdCard(frenceHistory.getPersonIdcard());
                             if (goods != null) {
                                 frenceVO.setTagName(goods.getGoodsName());
                                 frenceVO.setImg(goods.getImg());
                             }
                         }
                         frenceVOList.add(frenceVO);
                     }
                 }
                 PageInfo<FrenceHistoryVO> page = new PageInfo<>(frenceVOList);
                 page.setPageNum(pageInfo.getPageNum());
                 page.setSize(pageInfo.getSize());
                 page.setSize(pageInfo.getSize());
                 page.setStartRow(pageInfo.getStartRow());
                 page.setEndRow(pageInfo.getEndRow());
                 page.setTotal(pageInfo.getTotal());
                 page.setPages(pageInfo.getPages());
                 page.setList(frenceVOList);
                 page.setPrePage(pageInfo.getPrePage());
                 page.setNextPage(pageInfo.getNextPage());
                 page.setIsFirstPage(pageInfo.isIsFirstPage());
                 page.setIsLastPage(pageInfo.isIsLastPage());

                 resultBean = new ResultBean();
                 resultBean.setCode(1);
                 resultBean.setMsg("操作成功");
                 List lists=new ArrayList();
                 lists.add(page);
                 resultBean.setData(lists);
                 resultBean.setSize(pageInfo.getSize());
                 return resultBean;
             }else {
                 PageInfo<FrenceHistoryVO> page = new PageInfo<>(frenceVOList);
                 page.setPageNum(0);
                 page.setSize(0);
                 page.setSize(0);
                 page.setStartRow(0);
                 page.setEndRow(0);
                 page.setTotal(0);
                 page.setPages(0);
                 page.setList(frenceVOList);
                 page.setPrePage(0);
                 page.setNextPage(0);

                 resultBean = new ResultBean();
                 resultBean.setCode(1);
                 resultBean.setMsg("操作成功");
                 List lists=new ArrayList();
                 lists.add(page);
                 resultBean.setData(lists);
                 resultBean.setSize(lists.size());
                 return resultBean;
             }


            }
        //围栏跟人员名称都有给
        else {
        List<Person> personList=personMapper.getPersonsByName(user.getId(),frenceHistoryCondition.getPersonName());
        List<FrenceHistoryVO> frenceVOList = new ArrayList<>();
        if (personList.size()>0){
            List<String> list=new ArrayList<>();
            PageInfo<FrenceHistory> pageInfo=null ;
            if (personList.size()>0){
                StringBuffer sb=new StringBuffer();
                for (Person person : personList) {
                    list.add(person.getIdCard());
                }
                pageInfo = frenceHistoryService.getFrenceHistoryByFrenceIdAndPersonName(pageIndex,pageSize,frenceHistoryCondition.getFrenceId(),list);
                for (FrenceHistory frenceHistory :pageInfo.getList()) {
                    FrenceHistoryVO frenceVO = new FrenceHistoryVO();
                    frenceVO.setId(frenceHistory.getId());
                    frenceVO.setPersonIdcard(frenceHistory.getPersonIdcard());
                    frenceVO.setX(frenceHistory.getX());
                    frenceVO.setY(frenceHistory.getY());
                    frenceVO.setStatus(frenceHistory.getStatus());
                    frenceVO.setTime(frenceHistory.getTime());
                    frenceVO.setFrenceId(frenceHistory.getFrenceId());
                    frenceVO.setUserId(frenceHistory.getUserId());
                    frenceVO.setMapkey(frenceHistory.getMapKey());
                    com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(frenceHistory.getMapKey());
                    if (map!=null){
                        frenceVO.setMapName(map.getMapName());
                    }
                    Frence frence = frenceMapper.selectByPrimaryKey(frenceHistory.getFrenceId());
                    if (frence != null) {
                        frenceVO.setFrenceName(frence.getName());
                        String type = frence.getType();
                        frenceVO.setAlert_type(type);
                        if ("in".equals(type)) {
                            frenceVO.setAlert_type("进入");
                        }
                        if ("out".equals(type)) {
                            frenceVO.setAlert_type("离开");
                        }
                        if ("on".equals(type)) {
                            frenceVO.setAlert_type("停留");
                        }
                        frenceVO.setData(frence.getData());
                    }
                    if ("0".equals(frenceHistory.getStatus())) {
                        frenceVO.setIsDeal("未处理");
                    }
                    if ("1".equals(frenceHistory.getStatus())) {
                        frenceVO.setIsDeal("已处理");
                    }
                    Person sqlperson = personMapper.getPersonByIdCard(frenceHistory.getPersonIdcard());
                    if (sqlperson != null) {
                        frenceVO.setTagName(sqlperson.getPersonName());
                        frenceVO.setImg(sqlperson.getImg());
                    } else {
                        Goods goods = goodsMapper.getGoodsByByIdCard(frenceHistory.getPersonIdcard());
                        if (goods != null) {
                            frenceVO.setTagName(goods.getGoodsName());
                            frenceVO.setImg(goods.getImg());
                        }
                    }
                    frenceVOList.add(frenceVO);
                }
            }
            PageInfo<FrenceHistoryVO> page = new PageInfo<>(frenceVOList);
            page.setPageNum(pageInfo.getPageNum());
            page.setSize(pageInfo.getSize());
            page.setSize(pageInfo.getSize());
            page.setStartRow(pageInfo.getStartRow());
            page.setEndRow(pageInfo.getEndRow());
            page.setTotal(pageInfo.getTotal());
            page.setPages(pageInfo.getPages());
            page.setList(frenceVOList);
            page.setPrePage(pageInfo.getPrePage());
            page.setNextPage(pageInfo.getNextPage());
            page.setIsFirstPage(pageInfo.isIsFirstPage());
            page.setIsLastPage(pageInfo.isIsLastPage());

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List datalist = new ArrayList<>();
            datalist.add(page);
            resultBean.setData(datalist);
            resultBean.setSize(page.getSize());
            return resultBean;
        }else {
            PageInfo<FrenceHistoryVO> page = new PageInfo<>(frenceVOList);
            page.setPageNum(0);
            page.setSize(0);
            page.setSize(0);
            page.setStartRow(0);
            page.setEndRow(0);
            page.setTotal(0);
            page.setPages(0);
            page.setList(frenceVOList);
            page.setPrePage(0);
            page.setNextPage(0);


            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List lists=new ArrayList();
            lists.add(page);
            resultBean.setData(lists);
            resultBean.setSize(lists.size());
            return resultBean;
        }

    }


    }
    /*
     * 批量将围栏警报设为已处理
     *
     *
     * */

    @RequestMapping(value = "dealFrenceHistoryBatch", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultBean dealFrenceHistoryBatch(@Param("") String frenceHistoryIds,
                                        HttpServletRequest request
    ) {
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
        if (frenceHistoryIds==null||"".equals(frenceHistoryIds)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("处理参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        String[]  ids = frenceHistoryIds.split(",");
        int size=0;
        List<Integer> idsList=new ArrayList<>();
        for (String id : ids) {
            if (StringUtils.isNumeric(id)){
                idsList.add(Integer.parseInt(id));
               /* TagStatus tagStatus = tagStatusService.selectByPrimaryKey(Integer.parseInt(id));
                if (tagStatus!=null){
                    tagStatus.setIsdeal("1");
                    int i = tagStatusService.updateByPrimaryKeySelective(tagStatus);
                    if (i>0){
                        size++;
                    }
                }*/
            }
        }

        int update = frenceHistoryService.updateBatch(user.getId(),idsList);
        //System.out.println(update);
        if (update>0){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            resultBean.setSize(update);
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("标签报警处理失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }

    /*
    * 查看所有已处理或未处理的围栏告警
    * */

    @RequestMapping(value = "getAllFrenceHistoryByIsDeal",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getAllFrenceHistoryByIsDeal(HttpServletRequest request,
                                              @RequestParam(defaultValue = "") String isdeal,
                                              @RequestParam(defaultValue = "1") Integer pageIndex,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
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
        if (isdeal == null || "".equals(isdeal)) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("处理参数不能为空");
            List<TagStatusVO> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (!"0".equals(isdeal) && !"1".equals(isdeal)) {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("参数有误");
            List<TagStatusVO> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        PageInfo<FrenceHistory> pageInfo = frenceHistoryService.getAllFrenceHistoryByIsDeal(pageIndex, pageSize, user.getId(),isdeal);

        List<FrenceHistoryVO> frenceVOList = new ArrayList<>();
        for (FrenceHistory frenceHistory : pageInfo.getList()) {
            FrenceHistoryVO frenceVO = new FrenceHistoryVO();
            frenceVO.setId(frenceHistory.getId());
            frenceVO.setPersonIdcard(frenceHistory.getPersonIdcard());
            frenceVO.setX(frenceHistory.getX());
            frenceVO.setY(frenceHistory.getY());
            frenceVO.setStatus(frenceHistory.getStatus());
            frenceVO.setTime(frenceHistory.getTime());
            frenceVO.setFrenceId(frenceHistory.getFrenceId());
            frenceVO.setUserId(frenceHistory.getUserId());
            frenceVO.setMapkey(frenceHistory.getMapKey());
            com.tg.locationsystem.entity.Map map = mapService.getMapByUuid(frenceHistory.getMapKey());
            if (map != null) {
                frenceVO.setMapName(map.getMapName());
            }
            Frence frence = frenceMapper.selectByPrimaryKey(frenceHistory.getFrenceId());
            if (frence != null) {
                frenceVO.setFrenceName(frence.getName());
                String type = frence.getType();
                frenceVO.setAlert_type(type);
                if ("in".equals(type)) {
                    frenceVO.setAlert_type("进入");
                }
                if ("out".equals(type)) {
                    frenceVO.setAlert_type("离开");
                }
                if ("on".equals(type)) {
                    frenceVO.setAlert_type("停留");
                }
                frenceVO.setData(frence.getData());
            }
            if ("0".equals(frenceHistory.getStatus())) {
                frenceVO.setIsDeal("未处理");
            }
            if ("1".equals(frenceHistory.getStatus())) {
                frenceVO.setIsDeal("已处理");
            }
            Person person = personMapper.getPersonByIdCard(frenceHistory.getPersonIdcard());
            if (person != null) {
                frenceVO.setTagName(person.getPersonName());
                frenceVO.setImg(person.getImg());
            } else {
                Goods goods = goodsMapper.getGoodsByByIdCard(frenceHistory.getPersonIdcard());
                if (goods != null) {
                    frenceVO.setTagName(goods.getGoodsName());
                    frenceVO.setImg(goods.getImg());
                }
            }
            frenceVOList.add(frenceVO);
        }

        PageInfo<FrenceHistoryVO> page = new PageInfo<>(frenceVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(frenceVOList);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list = new ArrayList<>();
        list.add(page);
        resultBean.setData(list);
        resultBean.setSize(page.getSize());
        return resultBean;
    }

    /*
    *将所有未处理围栏告警设成已处理
    * */
    @RequestMapping(value = "setAllFrenceHistoryDeal",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean setAllFrenceHistoryDeal(HttpServletRequest request) {
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

       int update= frenceHistoryService.setAllFrenceHistoryDeal(user.getId());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(update);
        return resultBean;
    }

    /*
    * 设置围栏开关
    * */
    @RequestMapping(value = "setFrenceSwitch",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean setFrenceSwitch(HttpServletRequest request,
                                      @RequestParam(defaultValue = "") Integer frenceId,
                                       @RequestParam(defaultValue = "") String setSwitch) {


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
        if (setSwitch==null||"".equals(setSwitch)||frenceId==null||"".equals(frenceId)){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("围栏开关参数不能为空");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        if ("1".equals(setSwitch)){
           //设置该用户下该围栏打开
            int update=frenceService.setSwitch(user.getId(),setSwitch,frenceId);
            //缓存
            Map<Integer, List<Frence>> frencemap = SystemMap.getFrencemap();
            List<Frence> frenceList = frencemap.get(user.getId());
            Frence frence = frenceService.selectByPrimaryKey(frenceId);
            if (frence!=null){
                boolean Isexist=false;
                for (Frence frence1 : frenceList) {
                    if (frence1.getId()==frence.getId()){
                        Isexist=true;
                        break;
                    }else {
                        Isexist=false;
                    }
                }
                if (!Isexist){
                    frenceList.add(frence);
                    frencemap.put(user.getId(),frenceList);
                }
            }
            System.out.println("个数:"+frenceList.size());


            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("打开围栏");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(update);
            return resultBean;
        }
        if ("0".equals(setSwitch)){
            //设置该用户下该围栏关闭
            int update=frenceService.setSwitch(user.getId(),setSwitch,frenceId);
            //在缓存中将围栏去除
            //缓存
            Map<Integer, List<Frence>> frencemap = SystemMap.getFrencemap();
            List<Frence> frenceList = frencemap.get(user.getId());
            Frence frence = frenceService.selectByPrimaryKey(frenceId);
            if (frence!=null){
                ////除去自己 
                Iterator<Frence> iterator = frenceList.iterator();
                while (iterator.hasNext()) {
                    Frence f = iterator.next();
                    if (frence.getId().equals(f.getId())) {
                        iterator.remove();//使用迭代器的删除方法删除  
                }
            }

            }
            System.out.println("个数:"+frenceList.size());

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("关闭围栏");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(update);
            return resultBean;
        }


        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("围栏开关设置失败");
        List list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}
