package RestAPIHelper;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

import java.util.Map;

import org.json.simple.JSONObject;

import model.Consent;

public class RestUtil{
	public static Response sendPostAPI(String postBody,Map<String,String> headerValues,Map<String,String> queryParams ){
		Response response = 	
				given().
				headers(headerValues).
				body(postBody).log().all().
				queryParams(queryParams).
				when().
				post();
		return response;
	}
	public static Response sendPostAPI(String postBody,Map<String,String> headerValues,String queryParam ){
		Response response = 	
				given().
				headers(headerValues).
				body(postBody).log().all().
				queryParam(queryParam).				
				when().
				post();
		return response;
	}
	public static Response sendPutAPI(String postBody,Map<String,String> headerValues,String queryParam ){
		Response response = 	
				given().
				headers(headerValues).
				body(postBody).log().all().
				queryParam(queryParam).				
				when().
				put();
		return response;
	}
	public static Response sendPutAPI(JSONObject postBody,Map<String,String> headerValues ){
		Response response = 	
				given().
				headers(headerValues).
				body(postBody).log().all().
				//queryParam(queryParam).				
				when().
				put();
		return response;
	}
	public static Response sendGetAPI(String contentType,Map<String,String> queryParams ){
		Response response = 	
				given().
				header("Content-Type",contentType).
				log().all().
				queryParams(queryParams).
				when().
				get();
		return response;
	}
}
