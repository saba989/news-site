package com.ixm.core.Magento;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Component(service = Servlet.class)
@SlingServletPaths("/bin/servlet/checking")
public class GraphQL extends SlingSafeMethodsServlet{

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	@Reference
	private transient GraphQlConfiguration configuration;
	
	public String result;
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {
		logger.info("............................Servlet starting !.............................");

		try {
			/*Magento's end point URL */
			URL url = new URL(configuration.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

			/*Input will take a graphQl query to run, need to maintain the format well*/
			String input=com.ixm.core.Magento.GraphQLConstants.QUERY;

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String output;
			logger.info("-----------------------------------Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				logger.info("-------------------------------------------------------*{}\n",output);
			}
			conn.disconnect();
		}  catch (MalformedURLException e) {
			logger.info("------------------------------Servlet Exception---------------------"+e);
		} catch (IOException e) {
			logger.info("------------------------------Servlet Exception---------------------"+e);
		}  
	
		response.getWriter().print("Done !");
	}
}
