import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Thread {

    public static void main(String[] args) throws InterruptedException {
	ExecutorService pool = Executors.newFixedThreadPool(20);

	for (int i = 0; i < 1000000; i++) {
	    int t = i;
	    pool.execute(() -> {
		Object ret= HttpRequest.sendGet("http://www.cnblogs.com/solq111/p/7246091.html", "");
		//System.out.println(ret);
	    });
	}

	pool.shutdown();
	pool.awaitTermination(5, TimeUnit.SECONDS);
    }

    public static class HttpRequest {
	public static int sendGetNoResponse(String url, String param) {

	    try {
		String urlNameString = url + "?" + param;
		URL realUrl = new URL(urlNameString);
		// 打开和URL之间的连接
		HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
		// 建立实际的连接
		connection.connect();
	 
			
		return connection.getResponseCode();

	    } catch (Exception e) {
		System.out.println("发送GET请求出现异常！" + e);
		e.printStackTrace();
	    }
	    return -404;
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
	    String result = "";
	    BufferedReader in = null;
	    try {
		String urlNameString = url + "?" + param;
		URL realUrl = new URL(urlNameString);
		// 打开和URL之间的连接
		HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
		// 建立实际的连接
		connection.connect();

		// 获取所有响应头字段
		Map<String, List<String>> map = connection.getHeaderFields();
		// 遍历所有的响应头字段
		// 定义 BufferedReader输入流来读取URL的响应
		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
		    result += line;
		}
	    } catch (Exception e) {
		System.out.println("发送GET请求出现异常！" + e);
		e.printStackTrace();
	    }
	    // 使用finally块来关闭输入流
	    finally {
		try {
		    if (in != null) {
			in.close();
		    }
		} catch (Exception e2) {
		    e2.printStackTrace();
		}
	    }
	    return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
	    PrintWriter out = null;
	    BufferedReader in = null;
	    String result = "";
	    try {
		URL realUrl = new URL(url);
		// 打开和URL之间的连接
		HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		// 发送POST请求必须设置如下两行
		connection.setDoOutput(true);
		connection.setDoInput(true);
		// 获取URLConnection对象对应的输出流
		out = new PrintWriter(connection.getOutputStream());
		// 发送请求参数
		out.print(param);
		// flush输出流的缓冲
		out.flush();
		// 定义BufferedReader输入流来读取URL的响应
		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
		    result += line;
		}
	    } catch (Exception e) {
		System.out.println("发送 POST 请求出现异常！" + e);
		e.printStackTrace();
	    }
	    // 使用finally块来关闭输出流、输入流
	    finally {
		try {
		    if (out != null) {
			out.close();
		    }
		    if (in != null) {
			in.close();
		    }
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	    }
	    return result;
	}
    }
}
