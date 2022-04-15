import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
 
public class SForceTest {
 
 
	public static void main(String[] args) {
		get();
	}
 
	static void get() {
		String url = "https://protocomdev-dev-ed.my.salesforce.com/services/apexrest/Case/";
		String method = "GET";
		String accessToken = "00D5j00000BXmyf!ARUAQPK833GIJYxT_W98Vv.mxrr0JVEGOSkx5bfl.4uHuV14eQUbBz9yikoCl.fHyPoRFQVTyE2JopLO.ptwAD8sshbuAkgY";
		byte[] result = executeRequest(url
		, method
		, null
		, accessToken
		, null
		);
 
		if (result != null && result.length > 0) {
			System.out.println(new String(result,StandardCharsets.UTF_8));
		} else {
			System.out.println("レスポンスがないよ!!");
		}
	}
 
	static byte[] executeRequest(String url,String method,String contentType,String accessToken,byte[] body) {
		HttpURLConnection urlConn = null;
		try {
			// HTTP接続
			URL reqURL = new URL(url);
			urlConn = (HttpURLConnection) reqURL.openConnection();
			urlConn.setRequestMethod(method);
			if ( contentType != null ) urlConn.setRequestProperty("Content-Type", contentType);
			if ( accessToken != null ) urlConn.setRequestProperty("Authorization", "Bearer "+accessToken);//Salesforce用アクセストークン
			urlConn.setDoOutput(true);
 
			urlConn.connect();
 
			// body送信
			if (body != null) {
				OutputStream out = urlConn.getOutputStream();
				try {
					out.write(body);
				} finally {
					out.close();
				}
			}
 
			//レスポンスの取得
			InputStream is = null;
			try {
				is = urlConn.getInputStream();
			}catch(Exception e) {
				is = urlConn.getErrorStream();
			}
 
			try (BufferedInputStream bis = new BufferedInputStream(is);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();){
				byte[] bbb = new byte[1024];
				while(true) {
					int r = bis.read(bbb);
					if ( r == -1 ) {
						break;
					}
					if ( r == 0 ) {
						Thread.sleep(100);
						continue;
					}
					baos.write(bbb,0,r);
				}
				return baos.toByteArray();
			}finally {
				if ( is != null ) {
					is.close();
				}
			}
 
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (urlConn != null) {
				// HTTP切断
				urlConn.disconnect();
			}
		}
		return null;
	}
}