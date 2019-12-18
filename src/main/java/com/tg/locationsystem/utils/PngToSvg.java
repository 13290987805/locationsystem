package com.tg.locationsystem.utils;

/**
 * @author hyy
 * @ Date2019/11/27
 */
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;


public class PngToSvg {

    public static boolean png2svg(String img,String svg) throws FileNotFoundException,IOException {
        File in = new File(img);
        BufferedImage sourceImg = ImageIO.read(in);
        int h = sourceImg.getHeight();
        int w = sourceImg.getWidth();
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        sb.append("\r\n");
        sb.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
        sb.append("\r\n");
        sb.append("<svg version=\"1.1\" id=\"Layer_1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" width=\"");
        sb.append(w);
        sb.append("px\" height=\""+h+"px\"");
        sb.append("\r\n");
        sb.append("viewBox=\"0 0 "+w+" "+h+"\" enable-background=\"new 0 0 "+w+" "+h+"\" xml:space=\"preserve\">");
        sb.append("\r\n");
        sb.append("<image id=\"image0\" width=\""+w+"\" height=\""+h+"\" x=\"0\" y=\"0\" href=\"data:image/png;base64,");
        Base64 base64 = new Base64();
        FileWriter out = new FileWriter(new File(svg));
        BufferedWriter bw = new BufferedWriter(out);
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        if (img.endsWith(".png")) {
            ImageIO.write(sourceImg, "png", byteArray);
        }
        ImageIO.write(sourceImg, "jpg", byteArray);
        byte[] bs = byteArray.toByteArray();
        byte[] base64byte = base64.encode(bs);
        sb.append(new String(base64byte));
        sb.append("\" /></svg>");
        bw.write(sb.toString());
        bw.flush();
        bw.close();
        return true;
    }
    public static void main(String[] args) {
        /*Scanner scanner = new Scanner(System.in);
        String msg = scanner.next();
        System.out.println("输入:"+msg);*/
        /*String png = "E:\\Picture\\18.jpg";
        String svg = "E:\\Picture\\mtt.svg";
        try {
            png2svg(png, svg);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }
}