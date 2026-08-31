package se.sundsvall.comfactfacade.apptests;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.comfactfacade.Application;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;

import static se.sundsvall.comfactfacade.api.ApiConstants.ACCOUNT_KEY_HEADER;

@WireMockAppTestSuite(files = "classpath:/ComfactIT/", classes = Application.class)
@Sql({
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata-it.sql"
})
class ComfactIT extends AbstractAppTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String PATH = "/" + MUNICIPALITY_ID + "/signings";
	private static final String RESPONSE_FILE = "response.json";
	private static final String REQUEST_FILE = "request.json";


	@Test
	void test1_getSigningRequests() {

		setupCall()
			.withServicePath(PATH + "?page=0&size=8&sort=Created,desc")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_getSigningRequest() {

		setupCall()
			.withServicePath(PATH + "/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_createSigningRequest() {

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test4_updateSigningRequest() {

		setupCall()
			.withServicePath(PATH + "/1")
			.withRequest(REQUEST_FILE)
			.withHttpMethod(PATCH)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test5_getSignatory() {

		setupCall()
			.withServicePath(PATH + "/1/signatory/1")
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test6_createSigningRequestReturnsFault() {

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test7_createSigningRequestWithParallelFlowType() {

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test8_createSigningRequestWithAccountKey() {

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withHeader(ACCOUNT_KEY_HEADER, "social-services")
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test9_createSigningRequestWithUnknownAccountKey() {

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withHeader(ACCOUNT_KEY_HEADER, "unknown-key")
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_createSigningRequestWithDefaultAccount() {

		// Municipality 2262 has a default account configured - no header sent, its comfact account id is used.
		setupCall()
			.withServicePath("/2262/signings")
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
