package org.richardqiao.io;

/**
 * 1. Split and Merge (Blend)
 * 2. Base64 (for binary file)
 * 3. Compressing
 *
 */
import java.io.*;
import java.util.*;

public class App
{
    private final static String fileName = "README.md"; // text file
    //private final static String fileName = "Spark Translations.docx"; // binary file
    private final static String path = "src/main/resources/data/";
    private static String input = path + fileName;
    private static String output = input + ".bld";
    private static String output2 = input + ".ubld";
    public static void main( String[] args ) throws IOException{
      blend(input, output, 5, "");
      unBlend(output, output2, 5, "");
    }
    
    private static byte[] readBin(String fileName) throws IOException{
      BufferedInputStream bs = null;
      byte[] bytes = null;
      try{
        File file = new File(fileName);
        bs = new BufferedInputStream(new FileInputStream(input));
        bytes = new byte[(int)file.length()];
        int bytesRead = 0;
        while(bytesRead < bytes.length){
          int remained = bytes.length - bytesRead;
          int read = bs.read(bytes, bytesRead, remained);
          bytesRead += read;
        }
      }catch(IOException ioe){
        ioe.printStackTrace();
      }finally{
        if(bs != null) bs.close();
      }
      return bytes;
    }
    
    private static void writeBin(byte[] bytes, String outputFileName) throws IOException{
      BufferedOutputStream bw = null;
      try{
        bw = new BufferedOutputStream(new FileOutputStream(outputFileName));
        bw.write(bytes);
      }catch(IOException e){
        e.printStackTrace();
      }finally{
        if(bw != null) bw.close();
      }
    }
    
    private static void blend(String inputFileName, String outputFileName, int split, String salt) throws IOException{
      InputStream is = null;
      BufferedOutputStream bos = null;
      try{
        File file = new File(input);
        int len = (int)(file.length() / split);
        int mod = (int)file.length() % split; 
        if(mod != 0) len++;
        byte[][] bytes = new byte[split][len];
        is = new FileInputStream(inputFileName);
        int ch = 0;
        int curX = 0, curY = 0;
        while((ch = is.read()) != -1){
          bytes[curX][curY] = (byte)ch;
          curX++;
          if(curX == split){
            curX = 0;
            curY++;
          }
        }
        bos = new BufferedOutputStream(new FileOutputStream(outputFileName));
        for(int i = 0; i < bytes.length; i++){
          if(i < mod){
            bos.write(bytes[i]);
          }else{
            bos.write(bytes[i], 0, len - 1);;
          }
        }
      }catch(IOException ioe){
        ioe.printStackTrace();
      }finally{
        is.close();
        bos.close();
      }
    }
    
    private static void unBlend(String inputFileName, String outputFileName, int split, String salt) throws IOException{
      BufferedOutputStream bos = null;
      BufferedInputStream bis = null;
      try{
        File file = new File(inputFileName);
        int len = (int)(file.length() / split);
        int mod = (int)file.length() % split;
        if(mod != 0) len++;
        bis = new BufferedInputStream(new FileInputStream(file));
        bos = new BufferedOutputStream(new FileOutputStream(outputFileName));
        byte[][] bytes = new byte[split][len];
        for(int i = 0; i < bytes.length; i++){
          if(i < mod){
            bis.read(bytes[i]);
          }else{
            bis.read(bytes[i], 0, len - 1);
          }
        }
        byte[] all = new byte[(int)file.length()];
        int k = 0;
        for(int i = 0; i < len; i++){
          for(int j = 0; j < split; j++){
            if(k == all.length) break;
            all[k++] = bytes[j][i];
          }
        }
        bos.write(all);
      }catch(IOException ioe){
        ioe.printStackTrace();
      }finally{
        if(bis != null) bis.close();
        if(bos != null) bos.close();
      }
    }
    
    private static void base64(String inputFileName, String outputFileName) throws IOException{
      BufferedWriter bw = null;
      try{
        byte[] bytes = readBin(inputFileName);
        String str64 = Base64.getEncoder().encodeToString(bytes);
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName)));
        bw.write(str64);
      }catch(IOException ioe){
        ioe.printStackTrace();
      }finally{
        if(bw != null) bw.close();
      }
      
    }
    
    private static File compress(File input, String output){
      return null;
    }
}
