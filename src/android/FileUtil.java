package com.foreveross.chameleon.cordovaplugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by HuangLi on 2015/7/11.
 */
public class FileUtil {
	/**
	 * 复制asset目录下的文件夹或文件到指定目录
	 * @param assetManager
	 * @param fromAssetPath
	 * @param toPath
	 * @return 复制完全成功
	 */
	public static boolean copyAssetFolder(AssetManager assetManager,
										   String fromAssetPath, String toPath) {
		try {
			String[] files = assetManager.list(fromAssetPath);
			new File(toPath).mkdirs();
			boolean res = true;
			for (String file : files)
				if (file.contains(".")&&assetManager.list(fromAssetPath+"/"+file).length==0)
					res &= copyAsset(assetManager,
							fromAssetPath + "/" + file,
							toPath + "/" + file);
				else
					res &= copyAssetFolder(assetManager,
							fromAssetPath + "/" + file,
							toPath + "/" + file);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean copyAsset(AssetManager assetManager,
									 String fromAssetPath, String toPath) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(fromAssetPath);
			new File(toPath).createNewFile();
			out = new FileOutputStream(toPath);
			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	private static void copyFile(String name,String destPath,Context context) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = context.getAssets().open(name);
			File outFile = new File(context.getExternalFilesDir(null), name);
			out = new FileOutputStream(outFile);
			byte[] buffer = new byte[1024];
			int read;
			while((read = in.read(buffer)) != -1){
				out.write(buffer, 0, read);
			}

		} catch(IOException e) {
			e.printStackTrace();
		} finally{
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		}

	}

	private static void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}
	/**
	 * 判断在App目录下是否存在该目录或者文件
	 * @param path
	 * @return
	 */
	public static boolean isExist(String path){
		File file=new File(path);
		return file.exists();
	}

	public static boolean deleteDirectory(String path){
		File file=new File(path);
		return file.delete();
	}
	public static void createFile(String path) throws IOException{
		File file=new File(path);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if(!file.exists()){
			file.createNewFile();
		}

	}
	/**
	 * 在App目录下删除该目录或者文件。
	 * @param path
	 * @return
	 */
	public static void deleteFile(String path) {
		File file=new File(path);
		if (file.isFile()) {
			file.delete();
			return;
		}

		if(file.isDirectory()){
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteFile(childFiles[i].getPath());
			}
			file.delete();
		}
	}
	public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
		InputStream is = new FileInputStream(filePath);
		String line; // 用来保存每行读取的内容
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		line = reader.readLine(); // 读取第一行
		while (line != null) { // 如果 line 为空说明读完了
			buffer.append(line); // 将读到的内容添加到 buffer 中
			buffer.append("\n"); // 添加换行符
			line = reader.readLine(); // 读取下一行
		}
		reader.close();
		is.close();
	}
	private static int verstionToInt(String version){
		String[] ss=version.split("\\.");
		int r=0;
		for (int i = 0; i < ss.length; i++) {
			int temp=Integer.parseInt(ss[i]);
			int d=(int)Math.pow(10, ss.length-i-1);
			r+=temp*d;
		}
		return r;
	}
	/**
	 * 解压assets目录下的资源
	 * @param assetZipFile
	 * @param location
	 * @param context
	 * @throws IOException
	 */
	public static void unzip(String assetZipFile, String location,Context context,UnzipListener listener) throws IOException {

		File f = new File(location);
		if(!f.exists()) {
			f.mkdirs();
		}
		AssetManager am = context.getAssets();
		InputStream inputStream = am.open(assetZipFile);
		ZipInputStream zin = new ZipInputStream(inputStream);
		if(listener!=null)listener.onStart(inputStream.available());
		long pre=0;
		try {
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				String path = location + ze.getName();
				pre+=ze.getSize();
				if (ze.isDirectory()) {
					File unzipFile = new File(path);
					unzipFile.mkdirs();
				}
				else {
					FileOutputStream fout = new FileOutputStream(path, false);
					BufferedOutputStream o=new BufferedOutputStream(fout);
					byte b[] = new byte[256];
					int n;
					while ((n = zin.read(b)) >= 0) {
						o.write(b,0,n);
					}
					try {
						zin.closeEntry();
					}
					finally {
						if(listener!=null)listener.onProgress((int)pre);
						o.close();
						fout.close();
					}
				}

			}
		}
		finally {
			zin.close();
			inputStream.close();
		}

	}
	/**
	 * 复制一个目录及其子目录、文件到另外一个目录
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// 递归复制
				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;

			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}
	public static boolean isHaveSD() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

}
