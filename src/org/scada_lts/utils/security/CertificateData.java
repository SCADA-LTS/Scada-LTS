package org.scada_lts.utils.security;

import java.time.Period;

public interface CertificateData {
    String getApplicationUri();
    String getCommonName();
    String getOrganization();
    String getOrganizationalUnit();
    String getLocalityName();
    String getStateName();
    String getCountryCode();
    String getHost();
    Period getValidityPeriod();
    static CertificateData newInstance(String applicationUri, String commonName,
                                       String organization, String organizationalUnit,
                                       String localityName, String stateName,
                                       String countryCode, String host,
                                       Period validityPeriod) {
        return new CertificateDataImpl(applicationUri, commonName, organization, organizationalUnit,
                localityName, stateName, countryCode, host, validityPeriod);
    }
}
