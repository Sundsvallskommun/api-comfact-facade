package se.sundsvall.comfactfacade.integration.comfact;


import static se.sundsvall.comfactfacade.integration.comfact.configuration.ComfactConfiguration.CLIENT_ID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import se.sundsvall.comfactfacade.integration.comfact.configuration.ComfactConfiguration;

import comfact.CreateSigningInstanceRequest;
import comfact.CreateSigningInstanceResponse;
import comfact.GetSignatoryRequest;
import comfact.GetSignatoryResponse;
import comfact.GetSigningInstanceInfoRequest;
import comfact.GetSigningInstanceInfoResponse;
import comfact.GetSigningInstanceRequest;
import comfact.GetSigningInstanceResponse;
import comfact.UpdateSigningInstanceRequest;
import comfact.UpdateSigningInstanceResponse;
import comfact.WithdrawSigningInstanceRequest;
import comfact.WithdrawSigningInstanceResponse;

@FeignClient(name = CLIENT_ID, url = "${integration.comfact.url}", configuration = ComfactConfiguration.class)
public interface ComfactClient {

	String TEXT_XML_UTF8 = "text/xml;charset=UTF-8";

	/**
	 * Create a signing instance
	 *
	 * @param createSigningInstance the request to create a signing instance
	 * return {@link CreateSigningInstanceResponse} containing the signing instance id and URL for each signatory to start their
	 * signing flow
	 */
	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {"SOAPAction=CreateSigningInstance"})
	CreateSigningInstanceResponse createSigningInstance(CreateSigningInstanceRequest createSigningInstance);

	/**
	 * Update a signing instance
	 *
	 * @param updateSigningInstance the request to update a signing instance
	 * @return {@link UpdateSigningInstanceResponse} containing information if request was successful or not
	 */
	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {"SOAPAction=UpdateSigningInstance"})
	UpdateSigningInstanceResponse updateSigningInstance(UpdateSigningInstanceRequest updateSigningInstance);

	/**
	 * Withdraw a signing instance
	 *
	 * @param withdrawSigningInstance the request to withdraw a signing instance
	 * @return {@link WithdrawSigningInstanceResponse} containing information if request was successful or not
	 */
	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {"SOAPAction=WithdrawSigningInstance"})
	WithdrawSigningInstanceResponse withdrawSigningInstance(WithdrawSigningInstanceRequest withdrawSigningInstance);


	/**
	 * Get a signing instance
	 *
	 * @param getSigningInstanceRequest the request et a signing instance
	 * @return {@link GetSigningInstanceResponse} containing the signing instance
	 */
	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {"SOAPAction=GetSigningInstance"})
	GetSigningInstanceResponse getSigningInstance(GetSigningInstanceRequest getSigningInstanceRequest);

	/**
	 * Get general information of existing SigningInstances
	 *
	 * @param getSigningInstanceRequest the request to get general information of existing SigningInstances
	 * @return {@link GetSigningInstanceInfoResponse} containing the general information of existing SigningInstances
	 */
	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {"SOAPAction=GetSigningInstanceInfo"})
	GetSigningInstanceInfoResponse getSigningInstanceInfo(GetSigningInstanceInfoRequest getSigningInstanceRequest);


	/**
	 * Get information about a signatory
	 *
	 * @param getSignatoryRequest the request to get signatory
	 * @return {@link GetSignatoryResponse} containing the signatory information
	 */
	@PostMapping(consumes = TEXT_XML_UTF8, produces = TEXT_XML_UTF8, headers = {"SOAPAction=GetSignatory"})
	GetSignatoryResponse getSignatory(GetSignatoryRequest getSignatoryRequest);


}
