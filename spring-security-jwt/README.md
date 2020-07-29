# spring-security-jwt
Simple project with Spring Security using JWT.

Stack: Spring Security, JWT.

## Step 1
Create spring boot project using Spring Initializr and add spring boot starter web, spring boot starter security.

## Step 2
Add another dependencies to pom.xml.
```xml
<dependencies>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
</dependencies>
```

## Step 3
Create controller with a single endpoint.
```java
@RestController
public class HomeResource {

    @RequestMapping({"/hello"})
    public String hello() {
        return "Hello World!";
    }
}
```

## Step 4 
Create MyUserDetailsService implementing UserDetailsService and override 
a single method "UserDetails loadUserByUsername(String username)". Hardcode the user data for testing.
```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User("user", "password", new ArrayList<>());
    }
}
```

## Step 5
Create configuration class extending WebSecurityConfigurerAdapter 
and override the method "void configure(AuthenticationManagerBuilder auth)" using MyUserDetailsService.
```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder encoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }
}
```

## Step 6
Create a class to produce a token. Hardcode a secret key.
Some methods allow generating and validating a token, some methods allow to extract the information from a token.
```java
@Service
public class JwtUtil {

    private final String SECRET_KEY = "secret";

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```
 
 ## Step 7
 Create a class AuthenticationRequest with user login and password.
 ```java
public class AuthenticationRequest {

    private String username;
    private String password;

    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthenticationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

## Step 8
Create a class AuthenticationResponse that receives a token.
```java
public class AuthenticationResponse {

    private String token;

    public AuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
```

## Step 9
Add a new endpoint to controller "/authenticate" which will be accessed by post request.

ResponseEntity will get the object of class AuthenticationRequest, create token for authenticated user and 
return the object of class AuthenticationResponse.  

For that we need to use AuthenticationManager, which allows authenticating by user name and password 
using the object of class UsernamePasswordAuthenticationToken. 

As well as we need create a token using the object of class JwtUtils with help UserDetails 
received from MyUserDetailsService.
```java
@RestController
public class HomeResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.
                    authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
```

## Step 10
Add swagger for convenience. You need to add dependencies:
```xml
<dependencies>
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
Create SwaggerConfiguration class.
```java
@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

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
                .apis(RequestHandlerSelectors.basePackage("com.github.camelya58.springsecurityjwt.controller"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(true);
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Authentication")
                .description("Example of spring security with JWT")
                .build();
    }
    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }
}
```
The method "apikey()" adds authorized button to swagger.

## Step 11
Add another method to SecurityConfiguration class to delimit access to different endpoints for authenticated 
and unauthenticated users.

As well as you need to override the method "AuthenticationManager authenticationManagerBean()" to create 
an AuthenticationManager bean.

Allow swagger to be accessed without authentication.

And now we also can change and set password encoder.
```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // the same code
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/authenticate").permitAll()
                .anyRequest().authenticated();
    }
   @Override
    public void configure(WebSecurity web) {
        // 
        web.ignoring().antMatchers("/v2/api-docs")
                .antMatchers("/swagger-resources/**")
                .antMatchers("/", "/swagger-ui.html")
                .antMatchers("/webjars/**");
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

## Step 12
Change MyUserDetailsService, add password encoder.
```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new User("user", passwordEncoder.encode("password"), new ArrayList<>());
    }
}
```

## Step 13
Run the project at http://localhost:8080/
![Image alt](https://i.ibb.co/7tChRYz/swagger.png)

Enter user name and password.
![Image alt](https://i.ibb.co/VmyVLTQ/swagger2.png)
And you will receive a token:
```
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNTk2MDE5NTQyLCJpYXQiOjE1OTYwMTU5NDJ9.haotU1FgTv3SSwyDygfGhHLuo14pn3h8LeI2lp8DyPY"
}
```

Then copy token, push the button "Authorize" and write "Bearer " and your token.

After that try to get access to hello page using "Get" button.

You will receive:
```
{
  "timestamp": "2020-07-29T09:52:23.571+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "",
  "path": "/hello"
}
```
Because our application doesn't know token in requests.
 
We need to intercept all incoming requests:
- extract JWT from a header;
- validate JWT;
- set in execution context for all requests.

## Step 14
Create JwtRequestFilter class extends OncePerRequestFilter and override a single method  "void doFilterInternal
(HttpServletRequest request, HttpServletResponse response, FilterChain chain)".

That's why wee need:
- get authorization header from request; 
- check if it starts with "Bearer ";
- receive our jwt from header;
- extract user name from jwt;
- create userDetails using userDetailsServer;
- validate token using jwt and userDetails;
- create UsernamePasswordAuthenticationToken using our userDetails;
- set details in authentication token for any requests;
- set authentication for SecurityContextHolder;
- create a chain of filters.     

```java
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtTokenUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
```

## Step 15
Change "configure(HttpSecurity http)" method of SecurityConfiguration class.

We tell Spring Security:
- not to create a session;
- to ask a token for each request;
- to use new filter before default filter.
```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    // the same code

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/authenticate").permitAll()
                .anyRequest().authenticated() 
    // add new lines:
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

## Step 16
Run the project at http://localhost:8080/ and repeat step 13.