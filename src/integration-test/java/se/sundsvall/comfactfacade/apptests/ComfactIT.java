package se.sundsvall.comfactfacade.apptests;


import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;

import se.sundsvall.comfactfacade.Application;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

@WireMockAppTestSuite(files = "classpath:/ComfactIT/", classes = Application.class)
class ComfactIT extends AbstractAppTest {


	@Test
	void test1_getSigningRequests() {

		setupCall()
			.withServicePath("/signings?page=0&size=8&sort=Created,desc")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_getSigningRequest() {

		setupCall()
			.withServicePath("/signings/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_createSigningRequest() {

		setupCall()
			.withServicePath("/signings")
			.withHttpMethod(POST)
			.withRequest("request.json")
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test4_updateSigningRequest() {

		setupCall()
			.withServicePath("/signings/1")
			.withRequest("request.json")
			.withHttpMethod(PATCH)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test5_cancelSigningRequest() {

		setupCall()
			.withServicePath("/signings/1")
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test6_getSignatory() {

		setupCall()
			.withServicePath("/signings/1/signatory/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse("response.json")
			.sendRequestAndVerifyResponse();
	}

}
