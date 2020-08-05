# spring-security

This repository contains several mini-projects with Spring Security and other related technologies.

## What Spring Security is:
- user authentication by user name and password;
- authentication providers SSO/OKTA/LDAP;
- application level authorization;
- authorization inside the application,ex. Oauth2;
- security of microservices (token, JWT);
- method level security.

## Spring Security default behavior
- add mandatory authentication for url;
- add login form;
- handle enter errors (login and password validation);
- create default user - "user" with default password which creates in idea when project runs.

***You can see the default realization of Spring Security behavior in a project - [spring-boot-security](https://github.com/camelya58/spring-security/tree/master/spring-boot-security).***

## 5 basic concepts:
- Authentication;
- Authorization;
- Principal;
- Granted Authority;
- Roles.

### Authentication
"Who you are?"

Knoledge based:
- password;
- pincode.

Possession bassed:
- phone/text message;
- key cards and badges;
- access token device.

**AuthenticationManager** is used to configure authentication in Spring Security.
He has the method "authenticate()". 

As well as you can use **AuthenticationManagerBuilder** using the method "configure(...)" of class WebSecurityConfigurerAdapter.
He can checks in memory, using jdbc (datasource) or jpa does such user exist.

Jdbc allows using tables of SQL databases from ready-made schema without creation java classes and connecting them with our application. 

***You can see the jdbc authentication by datasource in a project - [spring-security-jdbc](https://github.com/camelya58/spring-security/tree/master/spring-security-jdbc).***

However, it isn't necessary always to create an sql schema, you can create Java objects and convert them to database entities using jpa.

For using jpa you will need to create a custom **UserDetailsService**. It will allow to load user by username from database. You also will need to create custom **UserDetails**.

***You can see the jpa authentication by such database as PostgreSQL in a project - [spring-security-jpa](https://github.com/camelya58/spring-security/tree/master/spring-security-jpa).***

**AuthenticationProvider** has the method "authenticate()" which allows to check in own database does such user exist and 
provides access to the token.

Facebook, Google, Github, LDAP can be AuthenticationProvider or you can create your custom provider.

***You can see the authentication by Facebook or Github in a project - [spring-security-facebook-oauth](https://github.com/camelya58/spring-security/tree/master/spring-security-facebook-auth).***

***You can see the ldap authentication in a project - [spring-security-ldap](https://github.com/camelya58/spring-security/tree/master/spring-security-ldap).***

### Authorization
"Can this user do it?"

Check user role.

**HttpSecurity** is used to configure authorization in Spring Security.

The method "configure(...)" of class WebSecurityConfigurerAdapter allows you to specify which source and with which user role will be accessed.

**Authorization strategy:**
- Session token (reference token);
- Json Web Token - JWT (value token).

Session ID + Cookies = Session authorization.

### Principal
Currently logged in user

### Granted Authority
Allowed actions for a specific category of users.

### Roles
Specific category of users.

## Json Web Token - JWT
Parts of JWT:
- Header (algorithm and type token);
```
{ "alg":"HS256",
  "typ":"JWT"
}
```
- Payload (user data: id, username, iat - creation date);
```
{  "sub":"12345",
   "name":"Kamila",
   "iat": 1516239022
}
```
- Signature (contains secret-key).
```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  
c2VjcmV0LWtleQo=

)
```
[Base64encoder](https://www.base64encode.org/) for a secret-key.

Online creator and [jwt encoder](https://jwt.io/). 
That's why token looks like the following:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NSIsIm5hbWUiOiJLYW1pbGEiLCJpYXQiOjE1MTYyMzkwMjJ9.yYC4nHAWUEiNDriw1A19HR4pslrYnps3XQRVe6l7CMI
```
JWT:
- doesn't contain confidentional information like password, ect.;
- contain such information which allows to authenticate the user.

We can create own class to generate a token with necessary claims or to use a TokenStore from a package org.springframework.security.oauth2.provider.token.

***You can see the JWT authorization with own jwt token generation in a project - [spring-security-jwt](https://github.com/camelya58/spring-security/tree/master/spring-security-jwt).***

# Authorization between services

## 5 basic concepts:
- Resource;
- Resource owner;
- Resource Server;
- Client;
- Authorization server.

The authorization process:
- Register a Client in a custom Authorization server or in Authentication provider like OKTA, Github, ect.
- The Client will request an access token from Authorization server.
- The Client will try to access the resource by providing an access token (JWT) to Resource server.
- The Resource server must check the token.
- The Resource server provides access the Client to the resource, knowing that the Client has such authorities.

If you need to make authorization between own microservices use **OAuth2.0**.

You can create separate Authorization server and resource server or create it in one application.

***You can see the separate realizations of Authorization server in a project - [jwt-auth-server](https://github.com/camelya58/spring-security/tree/master/jwt-auth-server) and Resource server in a project - [jwt-resource-server](https://github.com/camelya58/spring-security/tree/master/jwt-resource-server).***

***You can see the realization of Authorization server and Resource server in common application in a project - [auth-resource-server](https://github.com/camelya58/spring-security/tree/master/auth-resource-server).***
