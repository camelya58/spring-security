# spring-security

This repository contains several mini-projects with Spring Security and other related technologies.

## What Spring Security is:
- user authentication by user name and password;
- authentication providers SSO/OKTA/LDAP;
- application level authorization;
- authorization inside the application,ex. Oauth2;
- security of microservices (token, JWT);
- method level security.

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

For using jpa you will need to create a custom **UserDetailsService**. It will allow to load user by username from database. You also will need to create custom **UserDetails**

**AuthenticationProvider**
He has the method "authenticate()" which allows to check in own database does such user exist and 
provides access to the token.

Facebook, Google, Github can be AuthenticationProvider or you can create uour custome provider.

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

## Spring security default behavior
- add mandatory authentication for url;
- add login form;
- handle enter errors (login and password validation);
- create default user - "user" with default password which creates in idea when project runs.

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
