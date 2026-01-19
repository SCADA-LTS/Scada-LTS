package org.scada_lts.utils.security;

import java.time.Period;

class CertificateDataImpl implements CertificateData {

    private final String applicationUri;
    private final String commonName;
    private final String organization;
    private final String organizationalUnit;
    private final String localityName;
    private final String stateName;
    private final String countryCode;
    private final String host;
    private final Period validityPeriod;

    public CertificateDataImpl(String applicationUri, String commonName, String organization, String organizationalUnit,
                               String localityName, String stateName, String countryCode, String host, Period validityPeriod) {
        this.applicationUri = applicationUri;
        this.commonName = commonName;
        this.organization = organization;
        this.organizationalUnit = organizationalUnit;
        this.localityName = localityName;
        this.stateName = stateName;
        this.countryCode = countryCode;
        this.host = host;
        this.validityPeriod = validityPeriod;
    }

    @Override
    public String getApplicationUri() {
        return applicationUri;
    }

    @Override
    public String getCommonName() {
        return commonName;
    }

    @Override
    public String getOrganization() {
        return organization;
    }

    @Override
    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    @Override
    public String getLocalityName() {
        return localityName;
    }

    @Override
    public String getStateName() {
        return stateName;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public Period getValidityPeriod() {
        return validityPeriod;
    }
}
