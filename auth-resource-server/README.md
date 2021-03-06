# auth-resource-server
Simple project with Spring Security using JWT, when the application is Authorization and Resource server.

Stack: Spring Security, JWT, Oath2.

## Step 1
Create spring boot project using Spring Initializr and add spring boot starter web, spring boot starter security.

## Step 2
Add another dependencies to pom.xml.
```xml
<dependencies>
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
</dependencies>
```

## Step 3
Create web security configurations.
```java
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder encoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var manager = new InMemoryUserDetailsManager();
        var user = User.withUsername("admin")
                .password("12345")
                .authorities("ADMIN")
                .build();
        manager.createUser(user);
        return manager;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
```

## Step 4
Create configurations for Authorization server.
```java
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${security.token.secret-key}")
    private String secretKey;

    @Autowired
    private AuthenticationManager authenticationManager;

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
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()
                .withClient("clientId")
                .secret("secret")
                .authorizedGrantTypes("password", "authorization_token", "refresh_token")
                .scopes("write");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(jwtAccessTokenConverter());
    }
}
```

## Step 5
Add properties.
```properties
security.token.secret-key=*************
```

## Step 6
Create configurations for Resource server. The both servers use one token store, that's why we can just autowire it.
```java
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore);
    }
}
```

## Step 7
Create a simple controller.
```java
@RestController
public class HomeController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello!";
    }
}

```

## Step 8
Run the project at http://localhost:8060/oauth/token?scope=write&grant_type=password&username=admin&password=12345
using PostMan.

You can check your access token at this [web site](https://jwt.io/).
![Image alt](https://i.ibb.co/9qqP9xK/jwt.png)