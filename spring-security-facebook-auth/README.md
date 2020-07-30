# spring-security-oauth
Simple project with Spring Security using Oauth2.

Stack: Spring Security, Oauth2.

## Step 1
Create spring boot project using Spring Initializr and add spring boot starter web, spring boot starter security.

## Step 2
Add another dependencies to pom.xml.
```xml
<dependencies>
<dependency>
            <groupId>org.springframework.security.oauth.boot</groupId>
            <artifactId>spring-security-oauth2-autoconfigure</artifactId>
            <version>2.1.8.RELEASE</version>
        </dependency>
</dependencies>
```
## Step 3
Add the annotation @EnableOAuth2Sso to main class.
This annotation tells that this service doesn't have a login form, it will call an additional service for login.

And we need to set the information in application.yml about what 
Spring Security is going to call to get login information.

## Step 4
For example, login information we will get from facebook.
For that you nee to register your application in facebook using [this page](https://developers.facebook.com/).
You will receive clientID and clientSecret which you need to add to your application.yml.
```
security:
    oauth2:
      client:
        clientId: ***************
        clientSecret: ****************
        access-token-uri: https://graph.facebook.com/oauth/access_token
        user-authorization-uri: https://www.facebook.com/dialog/oauth
        token-name: oauth_token
        authentication-scheme: query
        client-authentication-scheme: form
      resource:
        user-info-uri: https://graph.facebook.com/me

```

## Step 5
Add html page for your app which you will access after facebook authorization.
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Main page</title>
    <style>
        .positions {
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            text-align: center;
        }

    </style>
</head>
<body>
<div class="positions">
    <div class="text">
        <h1 style="color:black">Hello User!</h1>
    </div>
</div>
</body>
</html>
```
Then run the project and go to http://localhost:8080.

## Step 6
Also you can use github instead of facebook. Go to 
[this page](https://docs.github.com/en/developers/apps/creating-a-github-app) for instructions.
```
security:
  oauth2:
    client:
      clientId: *************
      clientSecret: ************
      access-token-uri: https://github.com/login/oauth/access_token
      user-authorization-uri: https://github.com/login/oauth/authorize
      token-name: oauth_token
      authentication-scheme: query
      client-authentication-scheme: form
    resource:
      user-info-uri: https://api.github.com/user
```
