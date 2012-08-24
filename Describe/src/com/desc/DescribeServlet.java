package com.desc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;


@SuppressWarnings("serial")
public class DescribeServlet extends HttpServlet {
	//TODO : to call freebase throuh loudreview
	
	private static final Logger log = Logger.getLogger(DescribeServlet.class.getName());
	

	public static String FREEBASE_API_KEY = "AIzaSyDkitg8UagA0yYR_OP6VgxeWIaYdgg2wjY";

	public void doGet(HttpServletRequest req, HttpServletResponse httpResponse)
			throws IOException {
		
		httpResponse.setContentType("text/html; charset=UTF-8");
		PrintWriter out = httpResponse.getWriter();
		//out.println("<html><head><meta name=\"txtweb-appkey\" content=\"2aad888c-a271-477a-92a1-dda1c1d5ac0d\" /></head><body>");
				
		String desc = null;
		String fbId = null;
		String query = null;
		try {
			query = req.getParameter("txtweb-message");
		//	desc += " Query: "+query;
			fbId = getfbId(query, 1,out);
		//	desc += " FBID: "+fbId+" ";
			desc = getDescription(fbId,out);
			
		//	out.println("</body></html>");
		} catch (Exception eception) {

		}
		
		sendResponse(httpResponse, desc);

	}

	public static String getfbId(String searchUser, int page,PrintWriter out) {

		try {
			String query_api = "*" + searchUser + "*";
			String query_envelope_api = query_api;
			String service_url_api = "https://www.googleapis.com/freebase/v1/search";
			String topicType = "";

			String url_api = service_url_api + "?"
					+ "query="
					+ URLEncoder.encode(query_envelope_api, "UTF-8"); //					+ "&key=" + FREEBASE_API_KEY;
			
//			log.info(" FBURL:"+url_api);
//			System.out.println(" FBURL:"+url_api);
//			out.println(" FBURL:"+url_api);

			URL celebSearchurl = new URL(url_api);
			URLConnection urlc = celebSearchurl.openConnection();
			urlc.setConnectTimeout(7000);
			urlc.setReadTimeout(7000);

			String response = convertStreamToString(urlc.getInputStream());
			
//			log.info(" Response:"+response);
//			System.out.println(" Response:"+response);
//			out.println(" Response:"+response);

			// JSONParser parser = new JSONParser();

			JSONObject json_data = (JSONObject) JSONSerializer.toJSON(response);
			JSONArray results = (JSONArray) json_data.get("result");
			int count = 1;

			for (Object result : results) {

				try {

					String name = (String) (((JSONObject) result).get("name"));

					String fbid = null;
					try {
						fbid = (String) ((JSONObject) result).get("mid");
						if (count++ == page) {
							return fbid;
						}
						
					} catch (Exception exception) {

					}
				} catch (Exception exception) {
					exception.printStackTrace();

				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();

		}
		return null;

	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		/*
		 * To convert the InputStream to String we use the Reader.read(char[]
		 * buffer) method. We iterate until the Reader return -1 which means
		 * there's no more data to read. We use the StringWriter class to
		 * produce the string.
		 */
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	public static String getDescription(String freebaseTopicId,PrintWriter out) {

		try {
			String celebTopicUrl = "https://www.googleapis.com/freebase/v1/topic"
					+ freebaseTopicId ;//+ "?key=" + FREEBASE_API_KEY;

			// System.out.println("temporary url:" + celebTopicUrl);
			URL celebSearchurl = new URL(celebTopicUrl);
			URLConnection urlc = celebSearchurl.openConnection();
			urlc.setConnectTimeout(7000);
			urlc.setReadTimeout(7000);

//			log.info(" Desc URL:"+celebSearchurl); 
//			System.out.println(" Desc URL:"+celebSearchurl);
//			out.println(" Desc URL:"+celebSearchurl);
			
			String response = convertStreamToString(urlc.getInputStream());
			
//			log.info(" Celeb Response:"+response);
//			System.out.println(" Celeb Response:"+response);
//			out.println(" Celeb Response:"+response);

			// System.out.println("Topic Response temporary :" + response);
			JSONObject json_data = (JSONObject) JSONSerializer.toJSON(response);
			JSONObject topic_data = ((JSONObject) json_data.get("property"));

			try {
				return ((String) ((JSONObject) (((JSONArray) ((JSONObject) ((JSONObject) ((JSONObject) (((JSONArray) (((JSONObject) topic_data
						.get("/common/topic/article")).get("values"))).get(0)))
						.get("property")).get("/common/document/text"))
						.get("values")).get(0))).get("value"));
			} catch (Exception exception) {

			}
		} catch (Exception exception) {
			exception.printStackTrace();

		}

		return null;
	}

	
	private static void sendResponse(HttpServletResponse httpResponse,
			String response) {
		try {
			httpResponse.setContentType("text/html; charset=UTF-8");
			PrintWriter out = httpResponse.getWriter();
			out.println("<html><head><meta name=\"txtweb-appkey\" content=\"2aad888c-a271-477a-92a1-dda1c1d5ac0d\" /></head><body>"
					+ response + "</body></html>");

			
		} catch (Exception e) {
			// Exception handling

		}
	}
	
	
	public static void main(String args[]){
		System.out.println(getfbId("tiger", 1,null));
		//System.out.println(getDescription("/m/07dm6"));
	}
}
