package com.tg.locationsystem;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.locationsystem.entity.*;
import com.tg.locationsystem.mapper.*;
import com.tg.locationsystem.maprule.SVGUtil;
import com.tg.locationsystem.maprule.ThroughWall;
import com.tg.locationsystem.service.*;
import com.tg.locationsystem.utils.*;
import com.tg.locationsystem.utils.test.BuildTree;
import com.tg.locationsystem.utils.test.Tree;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ws.schild.jave.*;

import javax.sql.DataSource;
import java.io.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LocationsystemApplicationTests {

	@Autowired
	private MyuserMapper myuserMapper;
	@Autowired
	private IMyUserService myUserService;
	@Autowired
	private IStationService stationService;
	@Autowired
	private ITagService tagService;
	@Autowired
	private IPersonService personService;
	@Autowired
	private ITagHistoryService tagHistoryService;
	@Autowired
	private IPersonTypeService personTypeService;
	@Autowired
	private IGoodsService goodsService;
	@Autowired
	private IGoodsTypeService goodsTypeService;
	@Autowired
	private IFrenceHistoryService frenceHistoryService;
	@Autowired
	private ITagStatusService tagStatusService;
	@Autowired
	private IMapService mapService;
	@Autowired
	private ITagTestService tagTestService;
	@Autowired
	private TableMapper tableMapper;
	@Autowired
	private IMapRuleService mapRuleService;
	@Autowired
	private PersonMapper personMapper;
	@Autowired
	private FrenceHistoryMapper frenceHistoryMapper;
	@Autowired
	private IDepService depService;
	@Autowired
	private PermissionMapper permissionMapper;


	DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Value("${async.executor.thread.core_pool_size}")
	private int corePoolSize;
	@Value("${async.executor.thread.max_pool_size}")
	private int maxPoolSize;

	@Autowired
	private DataSource dataSource;

	@Test
	public void test21(){


	}
	@Test
	public void test20() throws SQLException, IOException {
		String path="C:\\Users\\zhourongchun\\Desktop\\1.xls";
		File file=new File(path);
		InputStream inp = new FileInputStream(file);
		//获得上传的excel文件
		HSSFWorkbook workbook =
				new HSSFWorkbook(new POIFSFileSystem(inp));
		//获取第一个sheet
		HSSFSheet sheet=workbook.getSheetAt( 0);
		//获取行数
		int rowNums=sheet.getPhysicalNumberOfRows();
		//遍历行数
		for (int i = 1; i < rowNums; i++) {
			HSSFRow row = sheet.getRow(i);
			for (int j = 0; j < row.getLastCellNum(); j++) {
				HSSFCell cell = row.getCell(j);
				System.out.println(cell);
			}
		}
	}
	@Test
	public void test19() throws SQLException, IOException {
		String path="C:\\Users\\zhourongchun\\Desktop\\新建 Microsoft Excel 工作表.xlsx";
		File file=new File(path);
		InputStream inp = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(inp);
		//获得上传的excel文件
		/*XSSFWorkbook workbook =
				new XSSFWorkbook(path);*/
		//获取第一个sheet
		XSSFSheet sheet=workbook.getSheetAt( 0);
		//获取行数
		int rowNums=sheet.getPhysicalNumberOfRows();
		//遍历行数
		for(int i= 1;i<rowNums;i++) {
			//得到该行的数据
			XSSFRow row = sheet.getRow(i);
			for (int j=0;j<row.getLastCellNum();j++){
				XSSFCell cell = row.getCell(j);
				System.out.println(cell);
			}

		}
	}
	@Test
	public void test18(){
		List<Permission> permissionList=permissionMapper.getAllPermission();
		List<Tree<com.tg.locationsystem.utils.test.Test>> trees = new ArrayList<Tree<com.tg.locationsystem.utils.test.Test>>();
		for (Permission permission : permissionList) {
			Tree<com.tg.locationsystem.utils.test.Test> tree = new Tree<com.tg.locationsystem.utils.test.Test>();
			tree.setId(String.valueOf(permission.getId()));
			tree.setParentId(String.valueOf(permission.getParentId()));
			tree.setTitle(permission.getPermissionName());
			List<Map<String, Object>> lmp = new ArrayList<Map<String, Object>>();
			Map<String, Object> mp = new HashMap<String, Object>();
            /*mp.put("COSTDEVICE_NUMBER", "");
            mp.put("PRICE_PER", "");
            mp.put("ORDER_INDEX", "");
            mp.put("ADJUST_DATE", "");
            mp.put("IS_LEAF", "");*/
			lmp.add(mp);
			trees.add(tree);
		}
		Tree<com.tg.locationsystem.utils.test.Test> tree = BuildTree.build(trees);

		System.out.println("结果:"+new Gson().toJson(tree));
	}
    @Test
    public void test17() throws Exception {
		//System.out.println(dataSource);
		//创建JdbcRealm对象
		//我们可以看到这里并没有设置查询语句，那么jdbcRealm对象是怎么做到从数据源中拿数据的呢？
		//点进源码，jdbcRealm构造函数，我们就可以看到答案。
		JdbcRealm jdbcRealm=new JdbcRealm();
		jdbcRealm.setDataSource(dataSource);
		//如果你这句话注释，那么下面的check权限的时候就会抛出异常
		jdbcRealm.setPermissionsLookupEnabled(true);

		//从数据库查询数据然后验证用户登录身份
        String userSql="select password from myuser where username=?";
        jdbcRealm.setAuthenticationQuery(userSql);
        //验证角色
        String roleSql="select role_name from myuser_role where username=?";
        jdbcRealm.setUserRolesQuery(roleSql);
        //验证权限
        String permissionSql="select permission from role_permission where role_name=?";
        jdbcRealm.setPermissionsQuery(permissionSql);

        DefaultSecurityManager defaultSecurityManager=new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject=SecurityUtils.getSubject();

        UsernamePasswordToken token=new UsernamePasswordToken("tgck","dGdjaw==");
        subject.login(token);

        System.out.println("isAuthenticated:"+subject.isAuthenticated());
        subject.checkRole("admin");
        subject.checkPermission("query");




    }
	@Test
	public void test16() throws Exception {
	//ALTER TABLE post/*post:表名*/ ADD COLUMN h_id/*h_id:列名*/ INT;
		List<Dep> depList=depService.getDepsByUserId(1);
		List<Tree<com.tg.locationsystem.utils.test.Test>> trees = new ArrayList<Tree<com.tg.locationsystem.utils.test.Test>>();

		for (Dep dep : depList) {
			Tree<com.tg.locationsystem.utils.test.Test> tree = new Tree<com.tg.locationsystem.utils.test.Test>();
			tree.setId(String.valueOf(dep.getId()));
			tree.setParentId(String.valueOf(dep.getPid()));
			tree.setTitle(dep.getName());
			List<Map<String, Object>> lmp = new ArrayList<Map<String, Object>>();
			Map<String, Object> mp = new HashMap<String, Object>();
            /*mp.put("COSTDEVICE_NUMBER", "");
            mp.put("PRICE_PER", "");
            mp.put("ORDER_INDEX", "");
            mp.put("ADJUST_DATE", "");
            mp.put("IS_LEAF", "");*/
			lmp.add(mp);
			trees.add(tree);
		}
		Tree<com.tg.locationsystem.utils.test.Test> t = BuildTree.build(trees);

		System.out.println(new Gson().toJson(t));

	}
	@Test
	public void test15()  throws EncoderException {
		File source = new File("C:\\Users\\zhourongchun\\Desktop\\locationJar\\locationsystem\\src\\main\\resources\\static\\vedio\\002.mp4"); //源avi格式视频
		File target = new File("C:\\Users\\zhourongchun\\Desktop\\locationJar\\locationsystem\\src\\main\\resources\\static\\vedio\\003.mp4");//转换后的mp4格式视频
		 VideoAttributes video = new VideoAttributes();
		 video.setCodec("libx264");//视频编码格式
		 video.setBitRate(new Integer(320000));
		 video.setFrameRate(new Integer(15));//数字设置小了，视频会卡顿
		video.setFaststart(true);
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setVideoAttributes(video);
		Encoder encoder = new Encoder();
		MultimediaObject multimediaObject = new MultimediaObject(source);
		encoder.encode(multimediaObject, target, attrs);//转换开始。。。


	/*	File source = new File("source.avi");
		File target = new File("target.mp4");
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libmp3lame");
		audio.setBitRate(new Integer(56000));
		audio.setChannels(new Integer(1));
		audio.setSamplingRate(new Integer(22050));
		VideoAttributes video = new VideoAttributes();
		video.setCodec("libx264");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp4");  //h264编码
		attrs.setAudioAttributes(audio);
		attrs.setVideoAttributes(video);
		Encoder encoder = new Encoder();
		encoder.encode(source, target, attrs);
*/



	}

	@Test
	public void test14() {
		String area="17.10 3.28,16.70 11.80,23.66 12.08,23.90 3.56,17.06 3.32,17.06 3.32,17.10 3.28";
		String mapkey="656543db-20cf-4fbc-9081-2588f124cacf";
		List<Tag> tagList = tagService.getTagsByMapUUIDAndUsed(mapkey);
		List<Tag> AreaList =new ArrayList<>();
		for (Tag tag : tagList) {
			System.out.println("id:" + tag.getId());
			double[] p = {tag.getX(), tag.getY()};
			List<double[]> poly = StringUtils.setData(area);
			String s = StringUtils.rayCasting(p, poly);
			if ("in".equals(s)) {
				Person person = personService.getPersonByOnlyAddress(tag.getAddress());
				if (person != null) {
					AreaList.add(tag);
				}

			}
		}
		System.out.println("区域人数:" + AreaList.size());

	/*	double[] p={23,7.1};
		List<double[]> poly = StringUtils.setData(area);
		String s = StringUtils.rayCasting(p, poly);
		System.out.println("结果:"+s);*/

	}


	@Test
	public void test13() {
		byte[] c={0x01,0x64};
		int a=(c[0]&0xff)* 256+(c[1]&0xff);
		int b=c[1]&0xff;
		int d=a+b;
		System.out.println(a+"---->"+b);
		System.out.println(d);


	}
	@Test
	public void test12() {
		String IdCard="350628199410241234";
		Map<String, String> birAgeSex = TestUtil.getBirAgeSex(IdCard);
		System.out.println(birAgeSex.get("birthday"));
		System.out.println("结果:"+birAgeSex);

	}
	@Test
	public void test11() {
		List<List<Frence>> arrayFrenceList=new ArrayList<>();
        List<Frence> frenceList=new ArrayList<>();
		Frence frence=new Frence();
		frence.setId(1);
		frenceList.add(frence);
		arrayFrenceList.add(frenceList);

		List<Frence> frenceList2=new ArrayList<>();
		Frence frence2=new Frence();
		frence2.setId(2);
		frenceList2.add(frence2);
		arrayFrenceList.add(frenceList2);

        System.out.println(arrayFrenceList);

    }
	@Test
	public void test10() {
		File file=new File("C:\\Users\\zhourongchun\\Desktop\\goods.txt");
		String path="C:\\Users\\zhourongchun\\Desktop\\aaa.txt";
		String[] result=new String[16];
		StringBuffer sb=new StringBuffer();
		BufferedReader reader=null;
		String temp=null;
		int line=1;
		try{
			reader=new BufferedReader(new FileReader(file));
			while((temp=reader.readLine())!=null){
				System.out.println("line"+line+":"+temp);
				String[] split = temp.split("");
				System.out.println(split.length);
				result[0]=split[14];
				result[1]=split[15];
				result[2]=split[12];
				result[3]=split[13];
				result[4]=split[10];
				result[5]=split[11];
				result[6]=split[8];
				result[7]=split[9];
				result[8]=split[6];
				result[9]=split[7];
				result[10]=split[4];
				result[11]=split[5];
				result[12]=split[2];
				result[13]=split[3];
				result[14]=split[0];
				result[15]=split[1];
				for (int i = 0; i < result.length; i++) {
					sb.append(result[i]);
				}
				filewriteutil.filewrite(sb.toString(),path);
				sb.setLength(0);
				line++;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(reader!=null){
				try{
					reader.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}


	@Test
	public void test9() {
		String path="C:\\img\\e2bd977e-aac0-45b8-96ac-759b339b4bb5.svg";
		String[] split = path.split("\\.");
		System.out.println(split.length);
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < split.length-1; i++) {
			System.out.println("分割:"+split[i]);
			sb.append(split[i]);
			sb.append(".");
		}
		sb.append("svg");
		System.out.println("拼接:"+sb.toString());
	}
	@Test
	public void test8() {
		String photo="C:\\img\\test.png";
		String target="C:\\img\\test.svg";
		boolean b=false;
		try {
			b = PngToSvg.png2svg(photo, target);

		} catch (FileNotFoundException e) {
			System.out.println("文件路径错误");
		} catch (IOException e) {
			System.out.println("图片转换错误");
		}
		System.out.println(b);
	}
	@Test
	public void test7() {
		String rule="[{\n" +
				"\t\"walls\": [{\n" +
				"\t\t\"p1x\": 1.0,\n" +
				"\t\t\"p1y\": 0.0,\n" +
				"\t\t\"p2x\": 3.0,\n" +
				"\t\t\"p2y\": 0.0\n" +
				"\t}, {\n" +
				"\t\t\"p1x\": 1.0,\n" +
				"\t\t\"p1y\": 0.0,\n" +
				"\t\t\"p2x\": 1.0,\n" +
				"\t\t\"p2y\": 3.0\n" +
				"\t}, {\n" +
				"\t\t\"p1x\": 1.0,\n" +
				"\t\t\"p1y\": 3.0,\n" +
				"\t\t\"p2x\": 3.0,\n" +
				"\t\t\"p2y\": 3.0\n" +
				"\t}, {\n" +
				"\t\t\"p1x\": 3.0,\n" +
				"\t\t\"p1y\": 3.0,\n" +
				"\t\t\"p2x\": 3.0,\n" +
				"\t\t\"p2y\": 0.0\n" +
				"\t}],\n" +
				"\t\"gates\": [],\n" +
				"\t\"enterable\": false\n" +
				"}]";
		for (int i = 0; i < 2; i++) {
			if (i==1){
				Tag tag=new Tag(2.0,2.0);
				tag.setAddress("111");
				Tag first = ThroughWall.getTagByRule(tag, rule);
				System.out.println(first.toString());
			}else {
				Tag tag=new Tag(1.5,4.0);
				tag.setAddress("111");
				Tag second = ThroughWall.getTagByRule(tag, rule);
				System.out.println(second.toString());
			}
		}
	}
    @Test
    public void test6() {

       String path="C:\\Users\\zhourongchun\\Documents\\Tencent Files\\774053518\\FileRecv\\tgck.svg";
		String msg = SVGUtil.readSVG(path);
		System.out.println("信息:"+msg);
		Tag tag=new Tag();
		tag.setAddress("111");
		tag.setX(17.1);
		tag.setY(11.1);
		Tag tagByRule = ThroughWall.getTagByRule(tag, msg);
		System.out.println(tagByRule.toString());

	}
	@Test
	public void test5() throws ParseException {
		/*TagTest tag1=new TagTest();
		tag1.setId(1);
		int[] a={1};
		tag1.setA(a);
		TagTest tag2=new TagTest();
		tag2.setId(2);
		int[] b={2};
		tag2.setA(b);
		List list=new ArrayList();
		list.add(tag1);
		list.add(tag2);
		Gson gson=new Gson();
		String toJson = gson.toJson(list);
		System.out.println(toJson);*/
		double[] dian1={1,1};
		double[] dian2={1,2};
		double[] dian3={0,1};
		double[] dian4={1,0.9};
		boolean intersection = RuleUtil.intersection(dian1[0], dian1[1], dian2[0], dian2[1],
				dian3[0], dian3[1], dian4[0], dian4[1]);
		if (intersection){
			System.out.println("相交");
		}else {
			System.out.println("不相交");
		}
		Gson gson=new Gson();
		String toJson="[{\"id\":1,\"a\":[1]},{\"id\":2,\"a\":[]}]";
		List<TagTest> list2=(List)gson.fromJson(toJson,(new TypeToken<List<TagTest>>() {}).getType());
        System.out.println(list2.size());
		System.out.println(list2);

	}

	@Test
	public void test4() {
		//Map<Integer,Integer> map=new HashMap<>();
		//map.put(1,1);
		// System.out.println(map.size());
		/*Set<String> set=new HashSet<>();
		String format = sdf.format(new Date());
		System.out.println(sdf.parse(format));

			AlertVO alertVO=new AlertVO();
			alertVO.setId(1);
			alertVO.setTagAddress("123");
			alertVO.setData(String.valueOf("1"));
			alertVO.setAlertType("1");
			alertVO.setIsdeal("0");
			alertVO.setAddTime(sdf.parse(format));
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
			net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(alertVO,jsonConfig);
			set.add(jsonObject.toString());

		AlertVO alertVO2=new AlertVO();
		alertVO2.setId(2);
		alertVO2.setTagAddress("123");
		alertVO2.setData(String.valueOf("1"));
		alertVO2.setAlertType("1");
		alertVO2.setIsdeal("0");
		alertVO2.setAddTime(sdf.parse(format));
		JsonConfig jsonConfig2 = new JsonConfig();
		jsonConfig2.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor());
		net.sf.json.JSONObject jsonObject2 = net.sf.json.JSONObject.fromObject(alertVO2,jsonConfig2);
		set.add(jsonObject2.toString());
		System.out.println(set.size());*/


	}

	@Test
	public void test3(){
/*		String string="65 3E 48 45 85 6C 11 29 40 9D 1B 27 7D F2 27 27 40 00 00 00 00 00 00 00 00 15 47 81 CF 00 01 CA DE 09 00 10 00 04 00  06 00 08 00 0A 00 05 00 09 00 07 00 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ";
		string=string.replace(" ","");
        System.out.println("数据长度:"+string.length());
		String Xmsg=string.substring(2,18);
		System.out.println(Xmsg);
		byte[] Xbytes = StringUtils.HexToByteArr(Xmsg);
		double x = StringUtils.bytes2Double(Xbytes);
		System.out.println("x:"+x+"四舍五入结果:"+StringUtils.round(x));

		String Ymsg=string.substring(18,34);
		System.out.println(Ymsg);
		byte[] Ybytes = StringUtils.HexToByteArr(Ymsg);
		double Y = StringUtils.bytes2Double(Ybytes);
		System.out.println("y:"+Y+"四舍五入结果:"+StringUtils.round(Y));

		String Zmsg=string.substring(34,50);
		System.out.println(Zmsg);
		byte[] Zbytes = StringUtils.HexToByteArr(Zmsg);
		double z = StringUtils.bytes2Double(Zbytes);
		System.out.println("z:"+z+"四舍五入结果:"+StringUtils.round(z));

		String tagAdd=string.substring(50,66);
		System.out.println("标签mac:"+tagAdd);*/

	}
	@Test
	public void test2(){
	/*SimpleDateFormat sdf=new SimpleDateFormat("YYYYMMddHHmm");
		String format = sdf.format(new Date());
        System.out.println(format);
		format="tag_history_"+format;
        int i = tagHistoryService.existTable(format);
        if (i>0){
            System.out.println("该表存在:"+i);
        }else {
            System.out.println("该表不存在:"+i);
            System.out.println("创建表...");
            int create=tagHistoryService.createNewTable(format);
            System.out.println("创建结果:"+create);
            if (create>0){
                System.out.println("创建成功!");
            }else {
                System.out.println("创建失败!");
            }
        }*/
    }
	@Test
	public void test1() throws Exception {
	/*	Person person = personService.getPersonByAddress(1, "1234567");

		System.out.println(person.toString());*/
/*		String msg="dGdjaw==";
        String string = Base64.getEncoder().encodeToString(msg.getBytes());
        System.out.println(string+":"+string.length());
        byte[] decode = Base64.getDecoder().decode("ZEdkamF3PT0=");
        String s = new String(decode);
        System.out.println(s+":"+s.length());*/

    }
	@Test
	public void test() throws ParseException {
	/*	DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String start="2019-07-02 12:10:20";
		String end="2019-07-03 12:10:20";
		Date now = dateFormat2.parse("2019-07-02 14:10:20");
		Date statrTime = dateFormat2.parse(start);
		Date endTime = dateFormat2.parse(end);
		System.out.println(statrTime.getTime()/1000);
		System.out.println(endTime.getTime()/1000);*/

	}
	@Test
	public void contextLoads() {
		//Myuser myuser = myuserMapper.selectByPrimaryKey(1);
		//Station station = stationService.getStationByAddress("1234", 2);
   /*     Person person=new Person();
        person.setPersonName("hyy");
        person.setUserId(1);
        person.setIdCard("123");
        person.setPersonHeight("175");
        int insert = personService.insertSelective(person);
        System.out.println(insert);
    }*/
	}
}
