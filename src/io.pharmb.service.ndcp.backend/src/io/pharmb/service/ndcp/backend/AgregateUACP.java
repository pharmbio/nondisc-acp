package io.pharmb.service.ndcp.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import java.net.HttpURLConnection;

import org.osgi.dto.DTO;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.dto.api.DTOs;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.rest.api.REST;
import osgi.enroute.rest.api.RESTRequest;
import osgi.enroute.stackexchange.pagedown.capabilities.RequirePagedownWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@RequirePagedownWebResource(resource = "enmarkdown.js")
@Component(name="io.pharmb.service.ndcp.backend",configurationPid="my.rest.calls.config")
public class AgregateUACP implements REST {

	@Activate
	public void activate() {
		System.out.println("Starting rest...");
	}
	@Reference
	DTOs dtos;
	
	public String getUpper(RESTRequest rr) {
		StringBuilder st = new StringBuilder("Get with no arguments: \n")
				.append("  --> rr._host(): " + rr._host() + "\n");
		System.out.println(st.toString() + "\n");
		return "ChuckSteak is Great".toUpperCase();
	}
	interface PredictRequrest extends RESTRequest {
		String smiles();
		String url();
	}
	
	public String getPredict(PredictRequrest rr) {

		URL baseUrl=null;
		URL url = null;
		
		try {
			baseUrl = new URL(rr.url());
			url = new URL(baseUrl,"predict?smiles="+rr.smiles());

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			if( conn.getResponseCode() != 200) {
				System.err.println(url);
				throw new RuntimeException("Failed: HTTP error code: "+ conn.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader( new InputStreamReader(conn.getInputStream()));
			StringBuilder strBuild = new StringBuilder();
			String output;
			System.out.print("Output from Server .... \n");
			while(( output=br.readLine())!= null) {
				strBuild.append(output);
			}
			return strBuild.toString();
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Malformed url "+ (baseUrl!=null?url.toString():"???"));
		} catch ( IOException e) {
			e.printStackTrace();
		}
		return "Error";
	}
	
	public static class Result extends DTO {
		public String smiles;
		public URL url;
		public String pval;
	}
	
	public static class Example extends DTO {
		public String method;
		public String title;
		public String uri;
		public String expect;
		public Map<String,String> payload;
		
		public Example() {}
		
		Example(String verb, String title, String uri, Map<String,String> payload, String expect) {
			this.title=title;
			this.uri = uri;
			this.expect = expect;
			this.method = verb;
			this.payload = payload;
		}
	}
	Example[] examples;
	public Example[] getExamples(RESTRequest r) {
		if(examples == null) {
			examples = new Example[] {
					new Example("GET", "upper: Basic", "rest/upper",null, "CHUCK IS GREAT"),
					new Example("GET", "predict: Test", "rest/predict?smiles=CCCCC%3DO&url=http%3A%2F%2Flocalhost%3A8085%2Fv1%2Fpredict%3Fsmiles%3DCN1C%3DNC2%3DC1C%28%3DO%29N%28C%28%3DO%29N2C%29C", null,"prediction")
			};
		}
		return examples;
	}
}
