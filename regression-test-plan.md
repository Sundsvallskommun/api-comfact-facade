# Regression Test Plan: SOAP-to-REST Migration

## Context
The Comfact integration has been migrated from SOAP/XML (WSDL-based) to REST/JSON. The facade's own REST API to consumers is unchanged — only the backend integration changed. The goal is to verify that all operations still produce correct results through the new REST backend.

## Existing Automated Coverage
The 8 integration tests in `ComfactIT` already cover the full lifecycle with WireMock stubs. These should pass first:

```bash
mvn verify -pl . -Dit.test=ComfactIT
```

## Manual Regression Test Cases (against a real Comfact environment)

### 1. Authentication & Connectivity
| # | Test | Expected |
|---|------|----------|
| 1.1 | Start the service — check logs for successful OAuth2 token fetch | No `invalid_client` or `invalid_scope` errors |
| 1.2 | First API call triggers token acquisition | Bearer token used in Comfact requests (check logs at DEBUG level) |
| 1.3 | Wait for token expiry, make another call | Token refreshes automatically |

### 2. Create Signing Request (POST /{municipalityId}/signings)
| # | Test | Expected |
|---|------|----------|
| 2.1 | Create with 1 signatory, SvensktEId, single PDF document | 200 OK, response contains `signingId` and `signatoryUrls` |
| 2.2 | Create with multiple signatories (2-3) | All signatories get unique signing URLs |
| 2.3 | Create with `flowType: "Parallel"` | Signing instance created with parallel flow |
| 2.4 | Create with `flowType: "Sequential"` | Signing instance created with sequential flow |
| 2.5 | Create with additional documents (attachments) | Documents attached to signing instance |
| 2.6 | Create with `additionalParties` (copy recipients) | Parties added without signing requirement |
| 2.7 | Create with reminder settings (`enabled: true`, `intervalInHours: 24`) | Reminder configured on instance |
| 2.8 | Create with notification message (custom subject/body) | Custom notification stored |
| 2.9 | Create with invalid document (not a real PDF) | 400 error with descriptive message |
| 2.10 | Create with party ID that needs legal ID resolution | Party service called, legal ID mapped correctly |

### 3. Get Signing Requests (GET /{municipalityId}/signings)
| # | Test | Expected |
|---|------|----------|
| 3.1 | List with default pagination | Returns page 0, size 10 |
| 3.2 | List with custom pagination (`?page=0&size=5`) | Returns correct page size |
| 3.3 | List with sorting (`?sort=Created,desc`) | Results sorted correctly |
| 3.4 | Verify `pagingAndSortingMetaData` in response | Total elements/pages match |

### 4. Get Single Signing Request (GET /{municipalityId}/signings/{signingId})
| # | Test | Expected |
|---|------|----------|
| 4.1 | Get a signing instance created in step 2 | Full details returned: signatories, documents, status, timestamps |
| 4.2 | Verify document content is base64-encoded | Can decode and get original PDF |
| 4.3 | Verify signatory signing URLs are present | URLs are valid and unique per signatory |
| 4.4 | Get non-existent signing ID | 404 Not Found |

### 5. Update Signing Request (PATCH /{municipalityId}/signings/{signingId})
| # | Test | Expected |
|---|------|----------|
| 5.1 | Update an existing signing instance (e.g., change expiry) | 204 No Content |
| 5.2 | Verify changes persisted via GET | Updated fields reflected |
| 5.3 | Update non-existent signing ID | 404 Not Found |

### 6. Cancel Signing Request (DELETE /{municipalityId}/signings/{signingId})
| # | Test | Expected |
|---|------|----------|
| 6.1 | Cancel/withdraw an active signing instance | 204 No Content |
| 6.2 | Verify via GET that status is "withdrawn" | Status updated |
| 6.3 | Cancel already-cancelled signing | Appropriate error or idempotent success |

### 7. Get Signatory (GET /{municipalityId}/signings/{signingId}/signatory/{partyId})
| # | Test | Expected |
|---|------|----------|
| 7.1 | Get signatory details for a valid signing/party | 200 with name, email, phone, identifications |
| 7.2 | Get signatory with non-existent party ID | 404 Not Found |

### 8. Edge Cases & Error Handling
| # | Test | Expected |
|---|------|----------|
| 8.1 | Invalid municipality ID (e.g., `9999`) | 400 validation error |
| 8.2 | Comfact service unavailable (if testable) | 502 Bad Gateway or circuit breaker response |
| 8.3 | Request with special characters in customer reference | Handled correctly (no encoding issues) |

## Verification Priority
1. **Run `mvn clean verify` first** — all unit + integration tests must pass
2. **Create → Get → Update → Get → Cancel → Get** lifecycle (tests 2.1, 4.1, 5.1, 5.2, 6.1, 6.2) — this is the critical happy path
3. Party ID resolution (test 2.10) — validates the Party service integration still works
4. Pagination/sorting (tests 3.1-3.4) — validates search filter mapping
5. Error handling (tests 2.9, 8.1-8.3) — validates error propagation from the new REST API
