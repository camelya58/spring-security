# jwt-resource-server
Simple project with Spring Security using JWT as resource server.

Stack: Spring Security, JWT, Oath2.

## Step 1
Create spring boot project using Spring Initializr and add spring boot starter web, spring boot starter security.

## Step 2
Add another dependencies to pom.xml.
```xml
<dependencies>
       <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security.oauth</groupId>
            <artifactId>spring-security-oauth2</artifactId>
            <version>2.3.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-jwt</artifactId>
            <version>1.1.1.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.1</version>
        </dependency>
        <dependency>
            <groupId>com.sun.xml.bind</groupId>
            <artifactId>jaxb-impl</artifactId>
            <version>2.3.0.1</version>
        </dependency>
        <dependency>
            <groupId>com.sun.xml.bind</groupId>
            <artifactId>jaxb-core</artifactId>
            <version>2.3.0.1</version>
        </dependency>

        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
</dependencies>
```

## Step 3
Create configurations for swagger.
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.camelya58.jwtresourceserver.controller"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(true);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Resource server")
                .description("Example of spring security with JWT")
                .build();
    }
    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }
}
```

## Step 4
Create configurations for Resource server.
```java
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Value("${security.token.secret-key}")
    private String secretKey;

    private final String[] AUTH_SWAGGER = {"/v2/api-docs", "/swagger-resources/**",
            "/", "/swagger-ui.html", "/webjars/**"};

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        var converter = new JwtAccessTokenConverter();
        converter.setSigningKey(secretKey);
        return converter;

    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(tokenStore());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers(AUTH_SWAGGER).permitAll()
                .anyRequest().authenticated();
    }
}
```

## Step 5
Add properties.
```properties
server.port=9090
security.token.secret-key=**************
```
The secret-key must be the same as in Authorization-server.

## Step 6
Create a simple Rest-controller with a page that only authorized users have access to.
```java
@RestController
public class HelloController {

    @GetMapping("/hello")
    @ApiOperation(value = "hello", authorizations = {@Authorization(value = "JWT")})
    public String hello() {
        return "Hello!";
    }
}
```
## Step 7
Run the project at http://localhost:9090.
If you will try to get the page by endpoint "/hello" you will receive:
```
{
  "error": "unauthorized",
  "error_description": "Full authentication is required to access this resource"
}
```
That's why you need to:
- run your Authorization server;
- copy "access token";
- click the button "Authorize";
- enter "Bearer " and your "access token".

And now, try it out again.
![Image alt](https://i.ibb.co/rb5JWtP/Swagger3.png)

Source - https://www.youtube.com/watch?v=ySQgHoCGKww&list=PLEocw3gLFc8WNFEuLzfLFQyLI-wowfoEE&index=9
