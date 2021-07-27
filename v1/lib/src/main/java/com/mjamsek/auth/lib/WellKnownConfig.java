package com.mjamsek.auth.lib;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WellKnownConfig {
    
    @JsonProperty("issuer")
    private String issuer;
    
    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    
    @JsonProperty("token_introspection_endpoint")
    private String tokenIntrospectionEndpoint;
    
    @JsonProperty("userinfo_endpoint")
    private String userinfoEndpoint;
    
    @JsonProperty("end_session_endpoint")
    private String endSessionEndpoint;
    
    @JsonProperty("jwks_uri")
    private String jwksUri;
    
    @JsonProperty("check_session_iframe")
    private String checkSessionIframe;
    
    @JsonProperty("grant_types_supported")
    private List<String> grantTypesSupported;
    
    @JsonProperty("response_types_supported")
    private List<String> responseTypesSupported;
    
    @JsonProperty("subject_types_supported")
    private List<String> subjectTypesSupported;
    
    @JsonProperty("id_token_signing_alg_values_supported")
    private List<String> idTokenSigningAlgValuesSupported;
    
    @JsonProperty("id_token_encryption_alg_values_supported")
    private List<String> idTokenEncryptionAlgValuesSupported;
    
    @JsonProperty("id_token_encryption_enc_values_supported")
    private List<String> idTokenEncryptionEncValuesSupported;
    
    @JsonProperty("userinfo_signing_alg_values_supported")
    private List<String> userinfoSigningAlgValuesSupported;
    
    @JsonProperty("request_object_signing_alg_values_supported")
    private List<String> requestObjectSigningAlgValuesSupported;
    
    @JsonProperty("response_modes_supported")
    private List<String> responseModesSupported;
    
    @JsonProperty("registration_endpoint")
    private String registrationEndpoint;
    
    @JsonProperty("token_endpoint_auth_methods_supported")
    private List<String> tokenEndpointAuthMethodsSupported;
    
    @JsonProperty("token_endpoint_auth_signing_alg_values_supported")
    private List<String> tokenEndpointAuthSigningAlgValuesSupported;
    
    @JsonProperty("claims_supported")
    private List<String> claimsSupported;
    
    @JsonProperty("claim_types_supported")
    private List<String> claimTypesSupported;
    
    @JsonProperty("claims_parameter_supported")
    private Boolean claimsParameterSupported;
    
    @JsonProperty("scopes_supported")
    private List<String> scopesSupported;
    
    @JsonProperty("request_parameter_supported")
    private Boolean requestParameterSupported;
    
    @JsonProperty("request_uri_parameter_supported")
    private Boolean requestUriParameterSupported;
    
    @JsonProperty("code_challenge_methods_supported")
    private List<String> codeChallengeMethodsSupported;
    
    @JsonProperty("tls_client_certificate_bound_access_tokens")
    private Boolean tlsClientCertificateBoundAccessTokens;
    
    @JsonProperty("introspection_endpoint")
    private String introspectionEndpoint;
    
    public String getIssuer() {
        return issuer;
    }
    
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
    
    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }
    
    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }
    
    public String getTokenEndpoint() {
        return tokenEndpoint;
    }
    
    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }
    
    public String getTokenIntrospectionEndpoint() {
        return tokenIntrospectionEndpoint;
    }
    
    public void setTokenIntrospectionEndpoint(String tokenIntrospectionEndpoint) {
        this.tokenIntrospectionEndpoint = tokenIntrospectionEndpoint;
    }
    
    public String getUserinfoEndpoint() {
        return userinfoEndpoint;
    }
    
    public void setUserinfoEndpoint(String userinfoEndpoint) {
        this.userinfoEndpoint = userinfoEndpoint;
    }
    
    public String getEndSessionEndpoint() {
        return endSessionEndpoint;
    }
    
    public void setEndSessionEndpoint(String endSessionEndpoint) {
        this.endSessionEndpoint = endSessionEndpoint;
    }
    
    public String getJwksUri() {
        return jwksUri;
    }
    
    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }
    
    public String getCheckSessionIframe() {
        return checkSessionIframe;
    }
    
    public void setCheckSessionIframe(String checkSessionIframe) {
        this.checkSessionIframe = checkSessionIframe;
    }
    
    public List<String> getGrantTypesSupported() {
        return grantTypesSupported;
    }
    
    public void setGrantTypesSupported(List<String> grantTypesSupported) {
        this.grantTypesSupported = grantTypesSupported;
    }
    
    public List<String> getResponseTypesSupported() {
        return responseTypesSupported;
    }
    
    public void setResponseTypesSupported(List<String> responseTypesSupported) {
        this.responseTypesSupported = responseTypesSupported;
    }
    
    public List<String> getSubjectTypesSupported() {
        return subjectTypesSupported;
    }
    
    public void setSubjectTypesSupported(List<String> subjectTypesSupported) {
        this.subjectTypesSupported = subjectTypesSupported;
    }
    
    public List<String> getIdTokenSigningAlgValuesSupported() {
        return idTokenSigningAlgValuesSupported;
    }
    
    public void setIdTokenSigningAlgValuesSupported(List<String> idTokenSigningAlgValuesSupported) {
        this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
    }
    
    public List<String> getIdTokenEncryptionAlgValuesSupported() {
        return idTokenEncryptionAlgValuesSupported;
    }
    
    public void setIdTokenEncryptionAlgValuesSupported(List<String> idTokenEncryptionAlgValuesSupported) {
        this.idTokenEncryptionAlgValuesSupported = idTokenEncryptionAlgValuesSupported;
    }
    
    public List<String> getIdTokenEncryptionEncValuesSupported() {
        return idTokenEncryptionEncValuesSupported;
    }
    
    public void setIdTokenEncryptionEncValuesSupported(List<String> idTokenEncryptionEncValuesSupported) {
        this.idTokenEncryptionEncValuesSupported = idTokenEncryptionEncValuesSupported;
    }
    
    public List<String> getUserinfoSigningAlgValuesSupported() {
        return userinfoSigningAlgValuesSupported;
    }
    
    public void setUserinfoSigningAlgValuesSupported(List<String> userinfoSigningAlgValuesSupported) {
        this.userinfoSigningAlgValuesSupported = userinfoSigningAlgValuesSupported;
    }
    
    public List<String> getRequestObjectSigningAlgValuesSupported() {
        return requestObjectSigningAlgValuesSupported;
    }
    
    public void setRequestObjectSigningAlgValuesSupported(List<String> requestObjectSigningAlgValuesSupported) {
        this.requestObjectSigningAlgValuesSupported = requestObjectSigningAlgValuesSupported;
    }
    
    public List<String> getResponseModesSupported() {
        return responseModesSupported;
    }
    
    public void setResponseModesSupported(List<String> responseModesSupported) {
        this.responseModesSupported = responseModesSupported;
    }
    
    public String getRegistrationEndpoint() {
        return registrationEndpoint;
    }
    
    public void setRegistrationEndpoint(String registrationEndpoint) {
        this.registrationEndpoint = registrationEndpoint;
    }
    
    public List<String> getTokenEndpointAuthMethodsSupported() {
        return tokenEndpointAuthMethodsSupported;
    }
    
    public void setTokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) {
        this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
    }
    
    public List<String> getTokenEndpointAuthSigningAlgValuesSupported() {
        return tokenEndpointAuthSigningAlgValuesSupported;
    }
    
    public void setTokenEndpointAuthSigningAlgValuesSupported(List<String> tokenEndpointAuthSigningAlgValuesSupported) {
        this.tokenEndpointAuthSigningAlgValuesSupported = tokenEndpointAuthSigningAlgValuesSupported;
    }
    
    public List<String> getClaimsSupported() {
        return claimsSupported;
    }
    
    public void setClaimsSupported(List<String> claimsSupported) {
        this.claimsSupported = claimsSupported;
    }
    
    public List<String> getClaimTypesSupported() {
        return claimTypesSupported;
    }
    
    public void setClaimTypesSupported(List<String> claimTypesSupported) {
        this.claimTypesSupported = claimTypesSupported;
    }
    
    public Boolean getClaimsParameterSupported() {
        return claimsParameterSupported;
    }
    
    public void setClaimsParameterSupported(Boolean claimsParameterSupported) {
        this.claimsParameterSupported = claimsParameterSupported;
    }
    
    public List<String> getScopesSupported() {
        return scopesSupported;
    }
    
    public void setScopesSupported(List<String> scopesSupported) {
        this.scopesSupported = scopesSupported;
    }
    
    public Boolean getRequestParameterSupported() {
        return requestParameterSupported;
    }
    
    public void setRequestParameterSupported(Boolean requestParameterSupported) {
        this.requestParameterSupported = requestParameterSupported;
    }
    
    public Boolean getRequestUriParameterSupported() {
        return requestUriParameterSupported;
    }
    
    public void setRequestUriParameterSupported(Boolean requestUriParameterSupported) {
        this.requestUriParameterSupported = requestUriParameterSupported;
    }
    
    public List<String> getCodeChallengeMethodsSupported() {
        return codeChallengeMethodsSupported;
    }
    
    public void setCodeChallengeMethodsSupported(List<String> codeChallengeMethodsSupported) {
        this.codeChallengeMethodsSupported = codeChallengeMethodsSupported;
    }
    
    public Boolean getTlsClientCertificateBoundAccessTokens() {
        return tlsClientCertificateBoundAccessTokens;
    }
    
    public void setTlsClientCertificateBoundAccessTokens(Boolean tlsClientCertificateBoundAccessTokens) {
        this.tlsClientCertificateBoundAccessTokens = tlsClientCertificateBoundAccessTokens;
    }
    
    public String getIntrospectionEndpoint() {
        return introspectionEndpoint;
    }
    
    public void setIntrospectionEndpoint(String introspectionEndpoint) {
        this.introspectionEndpoint = introspectionEndpoint;
    }
}
