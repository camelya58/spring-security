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

    private Boolean validateToken(String token, UserDetails userDetails) {
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

    @RequestMapping({"/hello"})
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
Add another method to SecurityConfiguration class to delimit access to different endpoints for authenticated 
and unauthenticated users.

As well as you need to override the method "AuthenticationManager authenticationManagerBean()" to create 
an AuthenticationManager bean.
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
}
```

