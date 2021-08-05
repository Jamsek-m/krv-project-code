# AuthZ Server
[![Build Status](https://jenkins.mjamsek.com/buildStatus/icon?job=krv-authz)](https://jenkins.mjamsek.com/job/krv-authz/)
> Project for class Cryptography and Computer Security, FRI, Ljubljana, August 2021

## Project content
Project is implementation of OpenID Connect provider, alongside with Admin Console, which is OpenID Connect Client.

### Features

* Well-known OpenID Connect configuration
* User authentication via username and password
* Issuing signed Json Web Tokens (access, refresh and id tokens)
* User profile
* Simple authorization using **Role based access (RBAC)**
* Session management (implementation of standard OpenID Connect Session Management 1.0 - draft 30)
* Client consent
* **OpenID Connect flows** supported:
    * Authorization code with PKCE
* **OAuth 2.0 grant types** supported:
    * Authorization code with PKCE
    * Direct grant (password)
    * Client credentials grant
    * Refresh token grant
* **Proof key for Code Exchange (PKCE)** methods supported:
    * plain
    * S256
* **Javascript object signing and encryption (JOSE)** signing algorithms supported:
    * HS256
    * HS384
    * HS512
    * RS256
    * RS382
    * RS512
    * ES256
    * ES384
    * ES512

