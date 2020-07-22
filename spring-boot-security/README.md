# spring-boot-security

Simple project to get started with spring security.

## Step 1
Create spring boot maven project and add dependencies.
```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.3.1.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
            <version>2.3.1.RELEASE</version>
        </dependency>
</dependencies>
```

## Step 2
Create the main class of the application.
```java
@SpringBootApplication
public class SpringSecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }
}
```

## Step 3
Run the project.
Go to the url - http://localhost:8080 and you will see sign in form.

![Image alt](https://i.ibb.co/dKjBRmX/Sign-in-form.png)

Spring boot default behavior: 
- adds mandatory authentication for URL;
- adds login form; 
- handles login error (if the login or password is wrong you will receive - "Bad credentials");
- creates a user and set a default password.

The default user is "user". The default password generates in IDEA.
For example:

![Image alt](https://i.ibb.co/sQHCKWf/Default-password.png)

## Step 4
As well as you can create your own default user.
Create the file - application.properties.
```properties
spring.security.user.name=user
spring.security.user.password=123456
```

And now IDEA won't generate a password because it doesn't need to.

## Step 5
Add simple rest-controller to get home page after authentication.

Principal - currently logged in user.
```java
@RestController
public class HomeController {

    @GetMapping("/")
    public String home(Principal principal) {
        return ("<h1>Welcome</hi>, ") + principal.getName();
    }
}
```
![Image alt](https://i.ibb.co/HCCHWh5/Welcome.png)

And now it works.

To complicate our application and set own configurations we can continue to add new classes and make changes 
to existing classes.

## Authentication configurations
We can set our configuration for Authentication Manager using Authentication Manager Builder, 
for example, add in memory users with role "USER" and "ADMIN".

You need to create your configuration class extending the class WebSecurityConfigurerAdapter 
and override his method "void configure(AuthenticationManagerBuilder auth)".

Add annotation @EnableWebSecurity before class, that allows Spring to understand that this class 
has web security configurations. 

And now you don't need to have default user and password in the file application.properties.

All passwords must be encoded. That's why you need to create @Bean of type PasswordEncoder.

But you can use "NoOpPasswordEncoder" which means that there is no encoder. 
This way isn't recommended. 

Your class will looks like:
```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password("123456")
                .roles("USER")
                .and()
                .withUser("admin")
                .password("654321")
                .roles("ADMIN");
    }

    @Bean
    public PasswordEncoder encode() {
        return NoOpPasswordEncoder.getInstance();
    }
}
```

## Authorization configurations
You can configure access to different pages for different kinds of user (USER, ADMIN or for all).

Add another endpoints and methods to controller.
```java
@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return ("<h1>Welcome</hi> ");
    }

    @GetMapping("/user")
    public String user(Principal principal) {
        return ("<h1>Welcome</hi>, ") + principal.getName();
    }

    @GetMapping("/admin")
    public String home(Principal principal) {
        return ("<h1>Welcome</hi>, ") + principal.getName();
    }
}
```
You need to override another method of the class WebSecurityConfigurerAdapter 
"void configure(HttpSecurity http)" and set endpoints for pages and roles.

If you want to set access to any pages you need to use pattern ("/**").
```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
// ... existing code

@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "static/css", "static/js").permitAll()
                .antMatchers("/user").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .and().formLogin();
    }
}
```
