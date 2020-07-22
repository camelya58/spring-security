# spring-security-jdbc

Simple project with Spring Security using JDBC.

Stack: Spring Security, JDBC, H2 database.


## Step 1
Create spring boot project using [Spring Initializr](https://start.spring.io/) and add 
spring boot starter web, spring boot starter security, spring boot starter JDBC, H2 database.

## Step 2
Create a controller with different endpoints.
```java
@RestController
public class HomeResource {
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

## Step 3
Create SecurityConfiguration class which extends WebSecurityConfigurerAdapter.
Add annotation @EnableWebSecurity on top of the class.

Override 2 methods: "void configure(AuthenticationManagerBuilder auth)", "void configure(HttpSecurity http)".
 
But now in AuthenticationManagerBuilder instead of in memory authentication we will use jdbc authentication.
 
Also we need to autowire datasource to connect jdbc to our database. Spring boot can create the database schema for us
if we use "withDefaultSchema()". Then add our users.

The class will look like this:
```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Bean
    public PasswordEncoder encoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .withDefaultSchema()
                .withUser(
                        User.withUsername("user")
                                .password("123")
                                .roles("USER")
                )
                .withUser(
                        User.withUsername("admin")
                                .password("admin")
                                .roles("ADMIN")
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/user").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .and().formLogin();
    }
}
```

It works but it's useless. So we need to create our own schema.