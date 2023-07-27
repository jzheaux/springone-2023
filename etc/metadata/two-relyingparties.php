<?php
$metadata['http://localhost:8080/saml2/service-provider-metadata/springtwo2023'] = array(
    'AssertionConsumerService' => 'http://localhost:8080/login/saml2/sso/springtwo2023',
    'SingleLogoutService' => 'http://localhost:8080/logout/saml2/slo',
    'NameIDFormat' => 'urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress',
    'simplesaml.nameidattribute' => 'emailAddress',
    'assertion.encryption' => FALSE,
    'nameid.encryption' => FALSE,
    'validate.authnrequest' => FALSE,
    'redirect.sign' => TRUE,
);

$metadata['http://auth-server:9000/saml2/service-provider-metadata/springtwo2023'] = array(
    'AssertionConsumerService' => 'http://auth-server:9000/login/saml2/sso/springtwo2023',
    'SingleLogoutService' => 'http://auth-server:9000/logout/saml2/slo',
    'NameIDFormat' => 'urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress',
    'simplesaml.nameidattribute' => 'emailAddress',
    'assertion.encryption' => FALSE,
    'nameid.encryption' => FALSE,
    'validate.authnrequest' => FALSE,
    'redirect.sign' => TRUE,
);
?>
