package com.tg.locationsystem.utils;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tg.locationsystem.entity.Tag;
import com.tg.locationsystem.entity.TagHistory;
import com.tg.locationsystem.entity.TagHistoryVO;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ??????????????????int????????
 * 
 * @author geshenjibi
 * 
 */
public class StringUtils {

	/**
	 * ???????????????????
	 * 
	 * @param paramByte
	 * @return
	 */
	public static String Byte2Hex(Byte paramByte) {
		Object[] arrayOfObject = new Object[1];
		arrayOfObject[0] = paramByte;
		return String.format("%02x", arrayOfObject).toUpperCase();
	}

	/**
	 * ??Byte[]?????????????????
	 * 
	 * @param paramArrayOfByte
	 * @return
	 */
	public static String ByteArrToHex(byte[] paramArrayOfByte) {
		StringBuilder localStringBuilder = new StringBuilder();
		int i = paramArrayOfByte.length;
		for (int j = 0; j < i; j++) {
			localStringBuilder.append(Byte2Hex(Byte
					.valueOf(paramArrayOfByte[j])));
			localStringBuilder.append(" ");
		}
		return localStringBuilder.toString();
	}

	/**
	 * ??Byte[]?????????????????
	 * 
	 * @param paramArrayOfByte
	 * @param start
	 *            0???
	 * @param length
	 *            ????
	 * @return
	 */
	public static String ByteArrToHex(byte[] paramArrayOfByte, int start,
			int length) {
		StringBuilder localStringBuilder = new StringBuilder();
		for (int i = start; i < length; i++)
			localStringBuilder.append(Byte2Hex(Byte
					.valueOf(paramArrayOfByte[i])));
		return localStringBuilder.toString();
	}

	/**
	 * ????????????
	 * 
	 * @param paramString
	 * @return
	 */
	public static byte HexToByte(String paramString) {
		paramString.replace(" ", "");
		return (byte) Integer.parseInt(paramString, 16);
	}

	/**
	 * ????????????????
	 * 
	 * @param paramString
	 * @return
	 */
	public static byte[] HexToByteArr(String paramString) {
		int j = paramString.length();
		byte[] arrayOfByte;
		if (isOdd(j) != 1) {
			arrayOfByte = new byte[j / 2];
		} else {
			j++;
			arrayOfByte = new byte[j / 2];
			paramString = "0" + paramString;
		}
		int k = 0;
		for (int i = 0; i < j; i += 2) {
			arrayOfByte[k] = HexToByte(paramString.substring(i, i + 2));
			k++;
		}
		return arrayOfByte;
	}

	/**
	 * ?????????int
	 * 
	 * @param paramString
	 * @return
	 */
	public static int HexToInt(String paramString) {

		return Integer.parseInt(paramString, 16);
	}

	public static int isOdd(int paramInt) {
		return paramInt & 0x1;
	}

	/**
	 * ???int??????byte[]
	 * 
	 * @param iSource
	 * @param length
	 * @return
	 */
	public static byte[] toByteArray(int iSource, int length) {
		byte[] bLocalArr = new byte[length];
		for (int i = 0; (i < 4) && (i < length); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	/***
	 * ?????????????????int????
	 * 
	 * @param b
	 * @param hasFuHao
	 * @return
	 */
	public static int TwoByte2Int(byte b, boolean hasFuHao) {
		if (hasFuHao) {
			int c = b;
			return c;
		} else {
			int n = b & 0xFF;
			return n;
		}
	}

	/***
	 * ??byte[]16��???????????????????int????
	 * 
	 * @param datas
	 * @return
	 */
	public static int[] ByteArray2IntArray(byte[] datas) {
		int[] result = new int[datas.length/2];
		int resultIndex = 0;
		for(int i = 0;i<datas.length;i+=2){
			if((i+1<datas.length)){
				result[resultIndex]= byte2int(datas[i],datas[i+1]);
				resultIndex++;
			}
		}
		return result;

	}
	/**
	 * ????��????��????????????��????????? ?��?????
	 * @param high??��
	 * @param low ??��
	 * @return
	 */
	public static int byte2int(byte high,byte low){
		short z = (short)(((high & 0x00FF) << 8) | (0x00FF & low));
		return z;
	}
	/**????????int??
	 * */
	private static int toUnsigned(short s) {  
	    return s & 0x0FFFF;  
	} 
	/***
	 * ?????????????????????????
	 * @param high
	 * @param low
	 * @return
	 */
	public static int byte2intUnsigned(byte high,byte low ){
		short z = (short)(((high & 0x00FF) << 8) | (0x00FF & low));
		return toUnsigned(z);
	}
	
	private static String hexString = "0123456789ABCDEF";

	//
	// /*
	// * ????????????16????????,????????????????????????
	// */
	// public static String encode(String str) {
	// // ???????????????????
	// byte[] bytes = str.getBytes();
	// StringBuilder sb = new StringBuilder(bytes.length * 2);
	// // ????????????????????2��16????????
	// for (int i = 0; i < bytes.length; i++) {
	// sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
	// sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
	// }
	// return sb.toString();
	// }

	/*
	 * ??16?????????????????,????????????????????????
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// ???2��16???????????????????
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

	/**
	 * ??byte[]???????????????
	 * 
	 * @param bytes
	 *            byte[]
	 * @param radix
	 *            ?????????????????��????Character.MIN_RADIX??Character.MAX_RADIX??
	 *            ??????��????10????
	 * @return ???????????
	 */
	public static String binary(byte[] bytes, int radix) {
		// System.out.println("?????????????��??" + Character.MIN_RADIX + "-" +
		// Character.MAX_RADIX);
		// System.out.println("2?????" + binary(bytes, 2));
		// System.out.println("5?????" + binary(bytes, 5));
		// System.out.println("8?????" + binary(bytes, 8));
		// System.out.println("16?????" + binary(bytes, 16));
		// System.out.println("32?????" + binary(bytes, 32));
		// System.out.println("64?????" + binary(bytes, 64));//
		// ????????????��????????��????10???????
		// ???2???????????????16????16????
		return new BigInteger(1, bytes).toString(2);// ?????1????????
	}

	/**
	 * ??bytes[]??????????????????????
	 * 
	 * @param bytes
	 * @return
	 */
	public static String binary2(byte[] bytes) {
		final String ZERO = "00000000";
		String result = "";
		for (int i = 0; i < bytes.length; i++) {
			String s = Integer.toBinaryString(bytes[i]);
			if (s.length() > 8) {
				s = s.substring(s.length() - 8);
			} else if (s.length() < 8) {
				s = ZERO.substring(s.length()) + s;
			}
			result += s;
		}
		return result;
	}

	/**
	 * ??????bytes??????????????????
	 * 
	 * @param b
	 * @return
	 */
	public static String binary2(byte b) {
		String ZERO = "00000000";
		String result = "";
		String s = Integer.toBinaryString(b);
		if (s.length() > 8) {
			s = s.substring(s.length() - 8);
		} else if (s.length() < 8) {
			s = ZERO.substring(s.length()) + s;
		}
		result += s;
		return result;
	}

	/**
	 * ??????????????????????
	 * 
	 * @param s
	 * @return
	 */
	public static String binaryToHex(String s) {
		String result = Long.toHexString(Long.parseLong(s, 2));
		if (result.length() % 2 == 1) {
			result = "0" + result;
		}
		return result;
	}

	/** bit????????????byte */
	public static byte bit2byte(String bString) {
		byte result = 0;
		for (int i = bString.length() - 1, j = 0; i >= 0; i--, j++) {
			result += (Byte.parseByte(bString.charAt(i) + "") * Math.pow(2, j));
		}
		return result;
	}
	public static double bytes2Double(byte[] arr) {  
        long value = 0;  
        for (int i = 0; i < 8; i++) {  
            value |= ((long) (arr[i] & 0xff)) << (8 * i);  
        }  
        return Double.longBitsToDouble(value);  
    } 
/*
* 把double数据四舍五入,保留两位精度
* */
public static double round(double d){
	BigDecimal bg = new BigDecimal(d);
	double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	return  f1;
}
/*
* 判断x,y是否在围栏里面
* */
public static String rayCasting(double[] p, List<double[]> poly) {
	double px = p[0];
	double py = p[1];
	boolean flag = false;

	for (int i = 0, l = poly.size(), j = l - 1; i < l; j = i, i++) {
		double sx = poly.get(i)[0], sy = poly.get(i)[1], tx = poly.get(j)[0], ty = poly.get(j)[1];

		// 点与多边形顶点重合
		if ((sx == px && sy == py) || (tx == px && ty == py)) {
			return "on";
		}

		// 判断线段两端点是否在射线两侧
		if ((sy < py && ty >= py) || (sy >= py && ty < py)) {
			// 线段上与射线 Y 坐标相同的点的 X 坐标
			double x = sx + (py - sy) * (tx - sx) / (ty - sy);

			// 点在多边形的边上
			if (x == px) {
				return "on";
			}

			// 射线穿过多边形的边界
			if (x > px) {
				flag = !flag;
			}
		}
	}
	// 射线穿过多边形边界的次数为奇数时点在多边形内
	return flag ? "in" : "out";
}

/*
* 将围栏数据变成list集合
* */
public static List<double[]>  setData(String data) {
     List<double[]> pos=new ArrayList<double[]>();
    String[] dd=data.split(",");
    for(String d:dd){
        String[] ds=d.split(" ");
        double[] p=new double[2];
        p[0]=Double.parseDouble(ds[0]);
        p[1]=Double.parseDouble(ds[1]);
        pos.add(p);
    }
    return pos;
}

	public static String getPath(List<TagHistory> list) {
	StringBuffer sb=new StringBuffer("M");
		//String d = "M";
		TagHistory tag;
		for (int i = 0; i < list.size(); i++) {
			tag = list.get(i);
			// System.out.println("X="+tag.getX()+"Y="+tag.getY());
			if (i != list.size() - 1) {
				//d += (tag.getX()) + " " + (tag.getY()) + "L ";
				sb.append(tag.getX());
				sb.append(" ");
				sb.append(tag.getY());
				sb.append("L");
			} else {
				//d += (tag.getX()) + " " + (tag.getY());
				sb.append(tag.getX());
				sb.append(" ");
				sb.append(tag.getY());
			}
		}
		// d="M0 0 H50 A20 20 0 1 0 100 50 v25 C50 125 0 85 0 85L150 50";

		//return d;
		return sb.toString();
	}

	public static String getPath1(List<TagHistoryVO> list) {
		StringBuffer sb=new StringBuffer("M");
		//String d = "M";
		TagHistoryVO tag;
		for (int i = 0; i < list.size(); i++) {
			tag = list.get(i);
			// System.out.println("X="+tag.getX()+"Y="+tag.getY());
			if (i != list.size() - 1) {
				//d += (tag.getX()) + " " + (tag.getY()) + "L ";
				sb.append(tag.getX());
				sb.append(" ");
				sb.append(tag.getY());
				sb.append("L");
			} else {
				//d += (tag.getX()) + " " + (tag.getY());
				sb.append(tag.getX());
				sb.append(" ");
				sb.append(tag.getY());
			}
		}
		// d="M0 0 H50 A20 20 0 1 0 100 50 v25 C50 125 0 85 0 85L150 50";

		//return d;
		return sb.toString();
	}

	public static String getTag_history(List<TagHistory> list) {
		/*List<TagHistory> list = getHistory(start, end, address);
		System.out.println(list.size());*/


		//System.out.println("整个数据长度="+list.size());
		int ss=0;
		for (int i = 0; i < list.size() - 7; i++) {
			double[] xx = new double[7];
			double[] yy = new double[7];
			TagHistory tag;
			int n = 0;

			for (int j = i; j < i + 7; j++) {

				tag = list.get(j);
				xx[n] = tag.getX();
				yy[n] = tag.getY();
				n++;
				tag = null;
			}
			xx = cubicSmooth7(xx, 7);
			yy = cubicSmooth7(yy, 7);
			n = 0;
			for (int j = i; j < i + 7; j++) {
				tag = list.get(j);
				tag.setX(xx[n]);
				tag.setY(yy[n]);
				tag = null;
				n++;
				ss++;
				//System.out.println("循环输出"+ss+"  数据长度="+list.size());
			}
			i = i + 7;
		}
		/*JSONObject json = new JSONObject();
		if (list.size() == 0) {
			json.put("code", 1);
			json.put("len", 0);
		} else {
			json.put("path", getPath(list));
		}*/
		return getPath(list);
	}
	/**
	 * 7点三次平滑
	 *
	 */

	public static double[] cubicSmooth7(double in[], int N) {
		double out[] = new double[7];
		int i;

		out[0] = (39.0 * in[0] + 8.0 * in[1] - 4.0 * in[2] - 4.0 * in[3] + 1.0 * in[4] + 4.0 * in[5] - 2.0 * in[6])
				/ 42.0;
		out[1] = (8.0 * in[0] + 19.0 * in[1] + 16.0 * in[2] + 6.0 * in[3] - 4.0 * in[4] - 7.0 * in[5] + 4.0 * in[6])
				/ 42.0;
		out[2] = (-4.0 * in[0] + 16.0 * in[1] + 19.0 * in[2] + 12.0 * in[3] + 2.0 * in[4] - 4.0 * in[5] + 1.0 * in[6])
				/ 42.0;
		for (i = 3; i <= N - 4; i++) {
			out[i] = (-2.0 * (in[i - 3] + in[i + 3]) + 3.0 * (in[i - 2] + in[i + 2]) + 6.0 * (in[i - 1] + in[i + 1])
					+ 7.0 * in[i]) / 21.0;
		}
		out[N - 3] = (-4.0 * in[N - 1] + 16.0 * in[N - 2] + 19.0 * in[N - 3] + 12.0 * in[N - 4] + 2.0 * in[N - 5]
				- 4.0 * in[N - 6] + 1.0 * in[N - 7]) / 42.0;
		out[N - 2] = (8.0 * in[N - 1] + 19.0 * in[N - 2] + 16.0 * in[N - 3] + 6.0 * in[N - 4] - 4.0 * in[N - 5]
				- 7.0 * in[N - 6] + 4.0 * in[N - 7]) / 42.0;
		out[N - 1] = (39.0 * in[N - 1] + 8.0 * in[N - 2] - 4.0 * in[N - 3] - 4.0 * in[N - 4] + 1.0 * in[N - 5]
				+ 4.0 * in[N - 6] - 2.0 * in[N - 7]) / 42.0;

		return out;
	}
	/**
	 *数据封装成json
	 *
	 * @param items
	 * @return json
	 *
	 */
	public static String toJson(List<Tag> items)  {
		//list转换为json
		Gson gson = new Gson();
		String str = gson.toJson(items);

		return str;
	}
	/**
	 *json转换为list
	 *
	 */
	public static List<Tag> toList(String json)  {
		//json转换为list
		Gson gson = new Gson();
		List<Tag> tagList =
				gson.fromJson(json, new TypeToken<List<Tag>>(){}.getType());

		return tagList;
	}
	public static boolean isNumeric(String str) {
		//Pattern pattern = Pattern.compile("^-?[0-9]+"); //这个也行
		Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");//这个也行
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
}
