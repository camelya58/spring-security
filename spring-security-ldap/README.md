# spring-security-ldap

Simple project with Spring Security using [LDAP](https://en.wikipedia.org/wiki/Lightweight_Directory_Access_Protocol).

Stack: Spring Security, .


## Step 1
Create spring boot project using [Spring Initializr](https://start.spring.io/) and add 
spring boot starter web, spring boot starter security.

## Step 2
Add another dependencies to pom.xml. These dependencies help spring security integrates LDAP.
```xml
<dependency>
    <groupId>com.unboundid</groupId>
    <artifactId>unboundid-ldapsdk</artifactId>
    <version>5.1.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.ldap</groupId>
    <artifactId>spring-ldap-core</artifactId>
    <version>2.3.3.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-ldap</artifactId>
    <version>5.3.3.RELEASE</version>
</dependency>
```

## Step 3
Add settings to application.properties.
```properties
spring.ldap.embedded.port=8389
spring.ldap.embedded.ldif=classpath:ldap-data.ldif
spring.ldap.embedded.base-dn=dc=springframework,dc=org
```

## Step 4
Create in the folder "resource" the file "ldap-data.ldif". You can copy the information from the [web-site](https://spring.io/guides/gs/authenticating-ldap/).

## Step 5 
Create a simple controller for home page.
```java
@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "Home page";
    }
}
```

## Step 6
Create class with security configurations.
```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .contextSource()
                .url("ldap://localhost:8389/dc=springframework,dc=org")
                .and()
                .passwordCompare()
                .passwordEncoder(new BCryptPasswordEncoder())
                .passwordAttribute("userPassword");
    }
}
```

## Step 7
Run the project. Go to http://localhost:9050.
Enter the login "ben" and password "benspassword" and get the home page.
 