package io.pharmb.service.ndcp.frontend.impl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import osgi.enroute.servlet.api.ConditionalServlet;

@Component(
		service = { ConditionalServlet.class }, 
		immediate = true, 
		property = {
				"service.ranking:Integer=999", 
				"name=" + NDCPApplicationServer.NAME, 
		}, 
		name = NDCPApplicationServer.NAME, 
		configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class NDCPApplicationServer implements ConditionalServlet {

	static final String NAME = "io.pharmb.service.ndcp.redirect";

	/**
	 * Must start with a "/".
	 */
	private String						redirect	= "/index.html";

	@Activate
	public void activate(BundleContext context) {
		context.getBundle().getEntry("/static/io.pharmb.service.ndcp.frontend/index.html");
	}

	@Override
	public boolean doConditionalService(HttpServletRequest rq, HttpServletResponse rsp) throws Exception {
		if ("/".equals(redirect))
			return false;

		String path = rq.getRequestURI();
		
		
		if (path == null || path.isEmpty() || path.equals("/")) {
			redirect(rq, rsp,"/index.html");
			return true;
		}
//		if( path.contains("jsme") || path.contains("png")) {
//			System.out.println(path);
//		}
		if( path.startsWith("/jsme") ) {
			redirect(rq, rsp, path);
			return true;
		}
		if (path != null && path.startsWith("/"))
			path = path.substring(1);
		if( !path.contains("/") ) {
			redirect(rq, rsp, "/" +path);
			return true;
		}

		return false;
	}
	
	private void redirect(HttpServletRequest rq, HttpServletResponse rsp,String path) throws ServletException, IOException {
		rq.getRequestDispatcher("/io.pharmb.service.ndcp.frontend"+path).forward(rq, rsp);
	}
}
