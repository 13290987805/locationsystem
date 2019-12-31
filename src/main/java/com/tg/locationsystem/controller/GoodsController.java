package com.tg.locationsystem.controller;

import com.github.pagehelper.PageInfo;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.mapper.GoodsMapper;
import com.tg.locationsystem.mapper.GoodsTypeMapper;
import com.tg.locationsystem.mapper.TableMapper;
import com.tg.locationsystem.pojo.*;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.SystemMap;
import com.tg.locationsystem.utils.UploadFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.Map;
import java.util.*;

/**
 * @author hyy
 * @ Date2019/6/28
 */
@Controller
@RequestMapping("goods")
public class GoodsController {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IGoodsTypeService goodsTypeService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsTypeMapper goodsTypeMapper;
    @Autowired
    private TableMapper tableMapper;
    @Autowired
    private ITagStatusService tagStatusService;
    @Autowired
    private ITagHistoryService tagHistoryService;
    @Autowired
    private IFrenceHistoryService frenceHistoryService;


    /*
    * 添加物品
    * */
    @RequestMapping(value = "AddGoods",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddGoods(@Valid Goods goods, BindingResult result,
                               HttpServletRequest request, @RequestParam(value="image",required=false)MultipartFile file){

        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //有必填项没填
        if (result.hasErrors()) {
            List<String> errorlist=new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = field+":"+fieldError.getDefaultMessage();
                //System.out.println(field + ":" + message);
                errorlist.add(message);
            });
            resultBean =new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }
        //物品已经存在
        Goods mygoods=goodsService.getGoodsByIdCard(goods.getGoodsIdcard(),user.getId());
        if (mygoods!=null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("该物品已经存在");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (goods.getTagAddress()!=null||!"".equals(goods.getTagAddress())){
            Goods goods2 = goodsService.getGoodsByAddress(user.getId(), goods.getTagAddress());
            if (goods2!=null){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("该标签已被别人使用");
                List list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }

        //设置物品所属管理者以及添加时间
        goods.setUserId(user.getId());
        goods.setAddTime(new Date());
        //设置物品标签为""
        if (goods.getTagAddress()==null){
            goods.setTagAddress("");
        }
        Tag tag = tagService.getTagByAddress(goods.getTagAddress(), user.getId());
        if (tag!=null){
            tag.setUsed("1");
            //更新标签表
            tagService.updateByPrimaryKeySelective(tag);
        }
        if (file==null){
            GoodsType goodsType = goodsTypeService.selectByPrimaryKey(goods.getGoodsTypeid());
            if (goodsType!=null){
                goods.setImg(goodsType.getImg());
            }
        }

        //设置图片路径
        //获取文件名加后缀
        if (file!=null){
            //保存图片的路径
            String s = System.getProperty("user.dir");//C:\whzy\locationsystem
            String filePath =s.split(":")[0]+":\\img";
            UploadFileUtil.isChartPathExist(filePath);
            //String filePath = "C:\\whzy\\locationsystem\\src\\main\\resources\\static\\goods";
            //获取原始图片的拓展名
            String originalFilename = file.getOriginalFilename();
            //新的文件名字
            String newFileName = UUID.randomUUID()+originalFilename;
            //封装上传文件位置的全路径
            File targetFile = new File(filePath,newFileName);
            //把本地文件上传到封装上传文件位置的全路径
            try {
                file.transferTo(targetFile);
                goods.setImg(targetFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("上传物品照片失败");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        int insert = goodsService.insertSelective(goods);

        if (insert>0){
            //把该标签放到缓存中
            Map<String, Integer> usermap = SystemMap.getUsermap();
            usermap.put(tag.getAddress(),tag.getUserId());
            //设置次数
            SystemMap.getCountmap().put(goods.getTagAddress(),0);

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("添加物品成功");
            List<Goods> list = new ArrayList<>();
            list.add(goods);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("添加物品失败");
            List list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
/*
* 查看所有物品信息
* */
@RequestMapping(value = "getGoods",method = RequestMethod.GET)
@ResponseBody
public ResultBean getGoods(HttpServletRequest request,
                             @RequestParam(defaultValue = "1") Integer pageIndex,
                             @RequestParam(defaultValue = "10") Integer pageSize){

    ResultBean resultBean;
    Myuser user = (Myuser) request.getSession().getAttribute("user");
    //未登录
    if (user==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("还未登录");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

    PageInfo<Goods> pageInfo=goodsService.getGoodsByUserId(pageIndex,pageSize,user.getId());
    List<GoodsVO> goodsVOList=new ArrayList<>();
    for (Goods goods : pageInfo.getList()) {
        GoodsVO goodsVO=new GoodsVO();
        goodsVO.setId(goods.getId());
        goodsVO.setGoodsName(goods.getGoodsName());
        goodsVO.setUserId(goods.getUserId());
        goodsVO.setGoodsTypeid(goods.getGoodsTypeid());
        goodsVO.setTagAddress(goods.getTagAddress());
        goodsVO.setImg(goods.getImg());
        goodsVO.setAddTime(goods.getAddTime());
        goodsVO.setGoodsIdcard(goods.getGoodsIdcard());
        //goodsVO.setRank(goods.getRank());

        //物品类型名字
        GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
       // System.out.println(goodsType.toString());
        if (goodsType!=null){
            goodsVO.setGoodsTypeName(goodsType.getName());
        }
        //System.out.println(goodsVO.toString());
        goodsVOList.add(goodsVO);
    }
    PageInfo<GoodsVO> page= new PageInfo<>(goodsVOList);
    page.setPageNum(pageInfo.getPageNum());
    page.setSize(pageInfo.getSize());
    page.setSize(pageInfo.getSize());
    page.setStartRow(pageInfo.getStartRow());
    page.setEndRow(pageInfo.getEndRow());
    page.setTotal(pageInfo.getTotal());
    page.setPages(pageInfo.getPages());
    page.setList(goodsVOList);
    page.setPrePage(pageInfo.getPrePage());
    page.setNextPage(pageInfo.getNextPage());
    page.setIsFirstPage(pageInfo.isIsFirstPage());
    page.setIsLastPage(pageInfo.isIsLastPage());

    resultBean = new ResultBean();
    resultBean.setCode(1);
    resultBean.setMsg("操作成功");
    List list=new ArrayList<>();
    list.add(page);
    resultBean.setData(list);
    resultBean.setSize(page.getSize());
    return resultBean;
}

    /*
     * 查看物品位置
     * */

    @RequestMapping(value = "getLocation",method = RequestMethod.GET)
    @ResponseBody
    public AllTagLocationResult getLocation(HttpServletRequest request,
                                            @RequestParam("goodsids") String goodsids){

        AllTagLocationResult resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new AllTagLocationResult();
            resultBean.setCode(-1);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            return resultBean;
        }
        String[] split = goodsids.split(",");
        List<GoodsLocation> locationList=new ArrayList<>();
        for (String s : split) {
            GoodsLocation location=new GoodsLocation();
            Goods goods = goodsService.selectByPrimaryKey(Integer.parseInt(s));
            if (goods!=null){
                location.setId(goods.getId());
                location.setAddTime(goods.getAddTime());
                location.setGoodsName(goods.getGoodsName());
                location.setImg(goods.getImg());
                location.setGoodsTypeid(goods.getGoodsTypeid());
                location.setUserId(goods.getUserId());
                location.setTagAddress(goods.getTagAddress());
                location.setGoodsIdcard(goods.getGoodsIdcard());
                //location.setRank(goods.getRank());

                Tag tag = tagService.getTagByAddress(goods.getTagAddress(), user.getId());
                if (tag!=null){
                    location.setTag(tag);
                }
                locationList.add(location);
            }
        }

        resultBean = new AllTagLocationResult();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
      resultBean.setGoodsLocations(locationList);
        return resultBean;
    }

    /*
    * 添加物品类型
    * */
    @RequestMapping(value = "AddGoodsType",method = RequestMethod.POST)
    @ResponseBody
    public ResultBean AddGoodsType(@Valid GoodsType goodsType, BindingResult result,
                                    HttpServletRequest request,@RequestParam(value="image",required=false) MultipartFile file){

        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        //有必填项没填
        if (result.hasErrors()) {
            List<String> errorlist=new ArrayList<>();
            result.getAllErrors().forEach((error) -> {
                FieldError fieldError = (FieldError) error;
                // 属性
                String field = fieldError.getField();
                // 错误信息
                String message = field+":"+fieldError.getDefaultMessage();
                //System.out.println(field + ":" + message);
                errorlist.add(message);
            });
            resultBean =new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("信息未填完整");
            resultBean.setData(errorlist);
            resultBean.setSize(errorlist.size());
            return resultBean;
        }
        //物品类型已经存在
        GoodsType mygoodstype= goodsTypeService.getGoodsTypeByName(goodsType.getName(),user.getId());
        if (mygoodstype!=null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("类型已经存在,无法重复添加");
            List<GoodsType> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        //设置物品类型创建时间和所属管理者
        goodsType.setCreateTime(new Date());
        goodsType.setUserId(user.getId());
        if (file==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("图标不能为空");
            List<PersonType> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        //设置图片路径
        //获取文件名加后缀
        if (file!=null){
            //保存图片的路径
            String s = System.getProperty("user.dir");//C:\whzy\locationsystem
            String filePath =s.split(":")[0]+":\\img";
            UploadFileUtil.isChartPathExist(filePath);
            //String filePath = "C:\\whzy\\locationsystem\\src\\main\\resources\\static\\goods";
            //获取原始图片的拓展名
            String originalFilename = file.getOriginalFilename();
            //新的文件名字
            String newFileName = UUID.randomUUID()+originalFilename;
            //封装上传文件位置的全路径
            File targetFile = new File(filePath,newFileName);
            //把本地文件上传到封装上传文件位置的全路径
            try {
                file.transferTo(targetFile);
                goodsType.setImg(targetFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("物品类型上传图片失败");
                List<GoodsType> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        int insert = goodsTypeService.insertSelective(goodsType);
        if (insert>0){
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("物品类型添加成功");
            List<GoodsType> list = new ArrayList<>();
            list.add(goodsType);
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("物品类型添加失败");
            List<GoodsType> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

    }
    /*
     * 查询所有物品类型
     * 分页
     * */
    @RequestMapping(value = "getGoodsTypesPage",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getGoodsTypesPage(HttpServletRequest request,
                                         @RequestParam(defaultValue = "1") Integer pageIndex,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        PageInfo<GoodsType> pageInfo=goodsTypeService.getGoodsTypesByUserId(pageIndex,pageSize,user.getId());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list=new ArrayList<>();
        list.add(pageInfo);
        resultBean.setData(list);
        resultBean.setSize(pageInfo.getSize());
        return resultBean;
    }
    /*
     * 查询所有物品类型
     * 不分页
     * */
    @RequestMapping(value = "getGoodsTypes",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getGoodsTypes(HttpServletRequest request){
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        List<GoodsType> goodsTypeList=goodsTypeService.getGoodsTypes(user.getId());
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(goodsTypeList);
        resultBean.setSize(goodsTypeList.size());
        return resultBean;
    }
    /*
     * 根据类型查询相关物品
     * 分页
     * */
    @RequestMapping(value = "getGoodsByTypePage",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getGoodsByTypePage(HttpServletRequest request,
                                             @RequestParam("goods_typeid") Integer typeid,
                                             @RequestParam(defaultValue = "1") Integer pageIndex,
                                             @RequestParam(defaultValue = "10") Integer pageSize){
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        PageInfo<Goods> pageInfo=goodsService.getGoodsByteTypePage(pageIndex,pageSize,typeid,user.getId());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list=new ArrayList<>();
        list.add(pageInfo);
        resultBean.setData(list);
        resultBean.setSize(pageInfo.getSize());
        return resultBean;
    }

    /*
     * 根据类型查询相关物品
     * 不分页
     * */
    @RequestMapping(value = "getGoodsByType",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getGoodsByType(HttpServletRequest request,
                                       @RequestParam("goods_typeid") Integer typeid){

        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }

        List<Goods> personTypeList=goodsService.getGoodsByType(typeid,user.getId());
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        resultBean.setData(personTypeList);
        resultBean.setSize(personTypeList.size());
        return resultBean;
    }

/*
* 删除物品信息
* */
@RequestMapping(value = "deleteGoods",method = RequestMethod.POST)
@ResponseBody
public ResultBean deleteGoods(HttpServletRequest request,
                               @RequestParam("goodsid") Integer goodsid){
    ResultBean resultBean;
    Myuser user = (Myuser) request.getSession().getAttribute("user");
    //未登录
    if (user==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("还未登录");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    Goods goods = goodsService.selectByPrimaryKey(goodsid);
    if (goods==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("该物品不存在");
        List<Person> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

    int delete = goodsService.deleteByPrimaryKey(goodsid);
    if (delete>0){
        Tag tag = tagService.getTagByAddress(goods.getTagAddress(), user.getId());
        if (tag!=null){
            tag.setUsed("0");
            int updatetag = tagService.updateByPrimaryKeySelective(tag);

            //把该标签在缓存中的记录清掉
            SystemMap.getUsermap().remove(tag.getAddress());
            SystemMap.getTagAndPersonMap().remove(tag.getAddress());
            SystemMap.getCountmap().remove(tag.getAddress());

            //删除轨迹,报警,电子围栏等
            List<String> Tablelist = tableMapper.listTable();
            for (String tableName : Tablelist) {
                if (tableName.contains("tag_history")){
                    int deleteHistory=tagHistoryService.deleteHistory(tableName,tag.getAddress());
                }
                if (tableName.contains("tag_status")){
                    int deletetagStatus=tagStatusService.deletetagStatus(tableName,tag.getAddress());
                }
                if (tableName.contains("frence_history")){
                    int deleteHistory=frenceHistoryService.deleteHistory(tableName,tag.getAddress());
                }

            }
            if (updatetag>0){
                resultBean = new ResultBean();
                resultBean.setCode(1);
                resultBean.setMsg("成功删除物品");
                List<Goods> list = new ArrayList<>();
                list.add(goods);
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
            }
        }
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("成功删除物品");
        List<Goods> list = new ArrayList<>();
        list.add(goods);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }else {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("删除失败");
        List<Goods> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}
    /*
     * 查看物品图片
     * 图片下载
     * */
    @RequestMapping(value = "querygoodsImg/{goodsId}",method = RequestMethod.GET)
    public void querygoodsImg(@PathVariable Integer goodsId, HttpServletResponse response,
                         HttpServletRequest request){
        Goods goods = goodsService.selectByPrimaryKey(goodsId);
        if (goods==null){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("查询不到相关信息");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String url=goods.getImg();
        if (url==null || "".equals(url)){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("暂无图片");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        FileInputStream hFile = null;      //得到文件大小
        try {
            hFile = new FileInputStream(url);
            int i=hFile.available();
            byte data[]=new byte[i];        //读数据

            hFile.read(data);         //得到向客户端输出二进制数据的对象

            OutputStream toClient=response.getOutputStream();         //输出数据

            toClient.write(data);

            toClient.flush();
            toClient.close();

            hFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //重新设置session存活时间
            //request.getSession().setMaxInactiveInterval(30*60);
        }
    }
    /*
     * 查看物品类型图片
     * 图片下载
     * */
    @RequestMapping(value = "querygoodsTypeImg/{goodsTypeId}",method = RequestMethod.GET)
    public void querygoodsTypeImg(@PathVariable Integer goodsTypeId, HttpServletResponse response,
                                   HttpServletRequest request){
        GoodsType goodsType = goodsTypeService.selectByPrimaryKey(goodsTypeId);
        if (goodsType==null){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("查询不到相关信息");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String url=goodsType.getImg();
        if (url==null || "".equals(url)){
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("暂无图片");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        FileInputStream hFile = null;      //得到文件大小
        try {
            hFile = new FileInputStream(url);
            int i=hFile.available();
            byte data[]=new byte[i];        //读数据

            hFile.read(data);         //得到向客户端输出二进制数据的对象

            OutputStream toClient=response.getOutputStream();         //输出数据

            toClient.write(data);

            toClient.flush();
            toClient.close();

            hFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //重新设置session存活时间
            //request.getSession().setMaxInactiveInterval(30*60);
        }
    }
/*
* 修改物品信息
* */
@RequestMapping(value = "UpdateGoods",method = {RequestMethod.POST,RequestMethod.GET})
@ResponseBody
public ResultBean UpdateGoods(@Valid Goods goods, BindingResult result,
                               HttpServletRequest request, @RequestParam(value="image",required=false)MultipartFile file){
    ResultBean resultBean;
    Myuser user = (Myuser) request.getSession().getAttribute("user");
    //未登录
    if (user==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("还未登录");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }


    //有必填项没填
    if (result.hasErrors()) {
        List<String> errorlist=new ArrayList<>();
        result.getAllErrors().forEach((error) -> {
            FieldError fieldError = (FieldError) error;
            // 属性
            String field = fieldError.getField();
            // 错误信息
            String message = field+":"+fieldError.getDefaultMessage();
            //System.out.println(field + ":" + message);
            errorlist.add(message);
        });
        resultBean =new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("信息未填完整");
        resultBean.setData(errorlist);
        resultBean.setSize(errorlist.size());
        return resultBean;
    }
    if (goods.getId()==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("id不能为空");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

    Goods sqlGoods=goodsService.selectByPrimaryKey(goods.getId());
    if (!sqlGoods.getGoodsIdcard().equals(goods.getGoodsIdcard())){
        Goods goodsByGoodsIdCard = goodsService.getGoodsByGoodsIdCard(goods.getGoodsIdcard());
        if (goodsByGoodsIdCard!=null){
                resultBean = new ResultBean();
                resultBean.setCode(-1);
                resultBean.setMsg("该物品编码不唯一");
                List<Myuser> list = new ArrayList<>();
                resultBean.setData(list);
                resultBean.setSize(list.size());
                return resultBean;
        }

    }
    //设置人员所属管理者
    goods.setUserId(user.getId());
    //设置修改时间
    goods.setAddTime(new Date());

    if (file==null){
        Goods goods1 = goodsService.selectByPrimaryKey(goods.getId());
        if (goods1!=null){
            goods.setImg(goods1.getImg());
        }
    }

    //设置图片路径
    //获取文件名加后缀
    if (file!=null){
        //保存图片的路径
        String s = System.getProperty("user.dir");//C:\whzy\locationsystem
        String filePath =s.split(":")[0]+":\\img";
        UploadFileUtil.isChartPathExist(filePath);
       // String filePath = "C:\\whzy\\locationsystem\\src\\main\\resources\\static\\goods";
        //获取原始图片的拓展名
        String originalFilename = file.getOriginalFilename();
        //新的文件名字
        String newFileName = UUID.randomUUID()+originalFilename;
        //封装上传文件位置的全路径
        File targetFile = new File(filePath,newFileName);
        //把本地文件上传到封装上传文件位置的全路径
        try {
            file.transferTo(targetFile);
            goods.setImg(targetFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("上传物品照片失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
    //更新标签表
    Goods sqlgoods = goodsService.selectByPrimaryKey(goods.getId());
    if (sqlgoods!=null) {
        if (!sqlgoods.getTagAddress().equals(goods.getTagAddress())) {
            Tag tag = tagService.getTagByOnlyAddress(sqlgoods.getTagAddress());
            if (tag != null) {
                tag.setUsed("0");
                //把该标签在缓存中的记录清掉
                SystemMap.getUsermap().remove(tag.getAddress());
                SystemMap.getTagAndPersonMap().remove(tag.getAddress());
                SystemMap.getCountmap().remove(tag.getAddress());

                tagService.updateByPrimaryKeySelective(tag);
            }
            Tag sqltag = tagService.getTagByOnlyAddress(goods.getTagAddress());
            if (sqltag!=null){
                if (sqltag.getAddress()!=null){
                    //把该标签放到缓存中
                    Map<String, Integer> usermap = SystemMap.getUsermap();
                    usermap.put(goods.getTagAddress(),user.getId());
                    SystemMap.getTagAndPersonMap().put(sqltag.getAddress(),goods.getGoodsIdcard());
                    //设置次数
                    SystemMap.getCountmap().put(sqltag.getAddress(),0);
                }


                sqltag.setUsed("1");
                tagService.updateByPrimaryKeySelective(sqltag);

            }
        }
    }
            int update = goodsService.updateByPrimaryKeySelective(goods);

    if (update>0){
        //把该标签放到缓存中
        Map<String, Integer> usermap = SystemMap.getUsermap();
        usermap.put(goods.getTagAddress(),user.getId());
        //设置次数
        SystemMap.getCountmap().put(goods.getTagAddress(),0);

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("物品修改成功");
        List<Goods> list = new ArrayList<>();
        list.add(goods);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }else {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("物品修改失败");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}

/*
* 修改物品类型信息
* */
@RequestMapping(value = "UpdateGoodsType",method = {RequestMethod.POST,RequestMethod.GET})
@ResponseBody
public ResultBean UpdateGoodsType(@Valid GoodsType goodsType, BindingResult result,
                                   HttpServletRequest request,@RequestParam(value="image",required=false)MultipartFile file){
    ResultBean resultBean;
    Myuser user = (Myuser) request.getSession().getAttribute("user");
    //未登录
    if (user==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("还未登录");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }

    //有必填项没填
    if (result.hasErrors()) {
        List<String> errorlist=new ArrayList<>();
        result.getAllErrors().forEach((error) -> {
            FieldError fieldError = (FieldError) error;
            // 属性
            String field = fieldError.getField();
            // 错误信息
            String message = field+":"+fieldError.getDefaultMessage();
            //System.out.println(field + ":" + message);
            errorlist.add(message);
        });
        resultBean =new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("信息未填完整");
        resultBean.setData(errorlist);
        resultBean.setSize(errorlist.size());
        return resultBean;
    }
    if (goodsType.getId()==null){
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("id不能为空");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
    //设置人员所属管理者
    goodsType.setUserId(user.getId());
    //设置时间
    goodsType.setCreateTime(new Date());

    if (file==null){
        GoodsType goodsType1 = goodsTypeService.selectByPrimaryKey(goodsType.getId());
        if (goodsType1!=null){
            goodsType.setImg(goodsType1.getImg());
        }
    }
//设置图片路径
    //获取文件名加后缀
    if (file!=null){
        System.out.println("文件不为空");
        //保存图片的路径
        String s = System.getProperty("user.dir");//C:\whzy\locationsystem
        String filePath =s.split(":")[0]+":\\img";
        UploadFileUtil.isChartPathExist(filePath);
        //String filePath = "C:\\whzy\\locationsystem\\src\\main\\resources\\static\\goods";
        //获取原始图片的拓展名
        String originalFilename = file.getOriginalFilename();
        //新的文件名字
        String newFileName = UUID.randomUUID()+originalFilename;
        //封装上传文件位置的全路径
        File targetFile = new File(filePath,newFileName);
        //把本地文件上传到封装上传文件位置的全路径
        try {
            file.transferTo(targetFile);
            goodsType.setImg(targetFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("上传人员类型logo失败");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
    }
    int update = goodsTypeService.updateByPrimaryKey(goodsType);

    if (update>0){
        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("物品类型修改成功");
        List<GoodsType> list = new ArrayList<>();
        list.add(goodsType);
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }else {
        resultBean = new ResultBean();
        resultBean.setCode(-1);
        resultBean.setMsg("物品类型修改失败");
        List<Myuser> list = new ArrayList<>();
        resultBean.setData(list);
        resultBean.setSize(list.size());
        return resultBean;
    }
}
    /*
     * 模糊查询,根据输入信息查询相关物品
     * */
    @RequestMapping(value = "getGoodsByMsg",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getPersonsByMsg(HttpServletRequest request,
                                      @RequestParam(defaultValue = "1") Integer pageIndex,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(defaultValue = "") String msg){

        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        if (msg==null||"".equals(msg)){
            PageInfo<Goods> pageInfo=goodsService.getGoodsByUserId(pageIndex,pageSize,user.getId());
            List<GoodsVO> goodsVOList=new ArrayList<>();
            for (Goods goods : pageInfo.getList()) {
                GoodsVO goodsVO=new GoodsVO();
                goodsVO.setId(goods.getId());
                goodsVO.setGoodsName(goods.getGoodsName());
                goodsVO.setUserId(goods.getUserId());
                goodsVO.setGoodsTypeid(goods.getGoodsTypeid());
                goodsVO.setTagAddress(goods.getTagAddress());
                goodsVO.setImg(goods.getImg());
                goodsVO.setAddTime(goods.getAddTime());
                goodsVO.setGoodsIdcard(goods.getGoodsIdcard());
                //goodsVO.setRank(goods.getRank());

                //物品类型名字
                GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                if (goodsType!=null){
                    goodsVO.setGoodsTypeName(goodsType.getName());
                }
                goodsVOList.add(goodsVO);
            }
            PageInfo<GoodsVO> page= new PageInfo<>(goodsVOList);
            page.setPageNum(pageInfo.getPageNum());
            page.setSize(pageInfo.getSize());
            page.setSize(pageInfo.getSize());
            page.setStartRow(pageInfo.getStartRow());
            page.setEndRow(pageInfo.getEndRow());
            page.setTotal(pageInfo.getTotal());
            page.setPages(pageInfo.getPages());
            page.setList(goodsVOList);
            page.setPrePage(pageInfo.getPrePage());
            page.setNextPage(pageInfo.getNextPage());
            page.setIsFirstPage(pageInfo.isIsFirstPage());
            page.setIsLastPage(pageInfo.isIsLastPage());

            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            List list=new ArrayList<>();
            list.add(page);
            resultBean.setData(list);
            resultBean.setSize(page.getSize());
            return resultBean;
        }
        PageInfo<Goods> pageInfo=goodsService.getGoodsByMsg(pageIndex,pageSize,user.getId(),msg);
        List<GoodsType> GoodsTypeList=goodsTypeMapper.getGoodsTypeByMsg(user.getId(), msg);
        // System.out.println("人员类型表查询结果:"+personTypeList.size());
        if (GoodsTypeList.size()>0){
            for (GoodsType goodsType : GoodsTypeList) {
                List<Goods> personList=goodsMapper.getGoodsByGoodsTypeId(user.getId(),goodsType.getId());
                for (Goods goods : personList) {
                    pageInfo.getList().add(goods);
                }
            }
        }

        List<GoodsVO> goodsVOList=new ArrayList<>();
        for (Goods goods : pageInfo.getList()) {
            GoodsVO goodsVO=new GoodsVO();
            goodsVO.setId(goods.getId());
            goodsVO.setGoodsName(goods.getGoodsName());
            goodsVO.setUserId(goods.getUserId());
            goodsVO.setGoodsTypeid(goods.getGoodsTypeid());
            goodsVO.setTagAddress(goods.getTagAddress());
            goodsVO.setImg(goods.getImg());
            goodsVO.setAddTime(goods.getAddTime());
            goodsVO.setGoodsIdcard(goods.getGoodsIdcard());
            //goodsVO.setRank(goods.getRank());

            //物品类型名字
            GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
            if (goodsType!=null){
                goodsVO.setGoodsTypeName(goodsType.getName());
            }
            goodsVOList.add(goodsVO);
        }
        PageInfo<GoodsVO> page= new PageInfo<>(goodsVOList);
        page.setPageNum(pageInfo.getPageNum());
        page.setSize(pageInfo.getSize());
        page.setSize(pageInfo.getSize());
        page.setStartRow(pageInfo.getStartRow());
        page.setEndRow(pageInfo.getEndRow());
        page.setTotal(pageInfo.getTotal());
        page.setPages(pageInfo.getPages());
        page.setList(goodsVOList);
        page.setPrePage(pageInfo.getPrePage());
        page.setNextPage(pageInfo.getNextPage());
        page.setIsFirstPage(pageInfo.isIsFirstPage());
        page.setIsLastPage(pageInfo.isIsLastPage());

        resultBean = new ResultBean();
        resultBean.setCode(1);
        resultBean.setMsg("操作成功");
        List list=new ArrayList<>();
        list.add(page);
        resultBean.setData(list);
        resultBean.setSize(page.getSize());
        return resultBean;
    }

    /*
     * 得到物品下没有绑定标签的物品
     * */
    @RequestMapping(value = "getGoodsByNoTag",method = RequestMethod.GET)
    @ResponseBody
    public ResultBean getGoodsByNoTag(HttpServletRequest request){
        ResultBean resultBean;
        Myuser user = (Myuser) request.getSession().getAttribute("user");
        //未登录
        if (user==null){
            resultBean = new ResultBean();
            resultBean.setCode(-1);
            resultBean.setMsg("还未登录");
            List<Myuser> list = new ArrayList<>();
            resultBean.setData(list);
            resultBean.setSize(list.size());
            return resultBean;
        }
        List<Goods> goodsList = goodsService.getGoodsByNoTag(user.getId());

        if (goodsList.size()>0){
            List<GoodsVO> goodsVOList = new ArrayList<>();
            for (Goods goods : goodsList) {
                GoodsVO goodsVO=new GoodsVO();
                goodsVO.setId(goods.getId());
                goodsVO.setGoodsName(goods.getGoodsName());
                goodsVO.setUserId(goods.getUserId());
                goodsVO.setGoodsTypeid(goods.getGoodsTypeid());
                goodsVO.setTagAddress(goods.getTagAddress());
                goodsVO.setImg(goods.getImg());
                goodsVO.setAddTime(goods.getAddTime());
                goodsVO.setGoodsIdcard(goods.getGoodsIdcard());
                //goodsVO.setRank(goods.getRank());

                //物品类型名字
                GoodsType goodsType = goodsTypeMapper.selectByPrimaryKey(goods.getGoodsTypeid());
                if (goodsType!=null){
                    goodsVO.setGoodsTypeName(goodsType.getName());
                }
                goodsVOList.add(goodsVO);
            }
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            resultBean.setData(goodsVOList);
            resultBean.setSize(goodsVOList.size());
            return resultBean;
        }else {
            resultBean = new ResultBean();
            resultBean.setCode(1);
            resultBean.setMsg("操作成功");
            resultBean.setData(goodsList);
            resultBean.setSize(goodsList.size());
            return resultBean;
        }
    }



}
