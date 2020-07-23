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

It works, but it's useless. So we need to create our own schema.

## Step 4
We need to change the method "void configure(AuthenticationManagerBuilder auth)" to:
```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    // the same code

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource);
    }
    // the same code
}
```
And create manually the files "schema.sql" and "data.sql" (both these files spring security creates by default 
when we use "withDefaultSchema()").

schema.sql - is used to create tables with concrete fields.
```sql
create table users(
      username varchar_ignorecase(50) not null primary key,
      password varchar_ignorecase(50) not null,
      enabled boolean not null);

create table authorities (
      username varchar_ignorecase(50) not null,
      authority varchar_ignorecase(50) not null,
      constraint fk_authorities_users foreign key(username) references users(username));

create unique index ix_auth_username on authorities (username,authority);
```
data.sql - is used to fill with concrete data.
```sql
INSERT INTO users(username, password, enabled)
values('user', '1234', true);

INSERT INTO users(username, password, enabled)
values('admin', '4321', true);

INSERT INTO authorities(username, authority)
values('user','ROLE_USER');

INSERT INTO authorities(username, authority)
values('admin','ROLE_ADMIN');
```

This is the default behavior for spring security.
But if you have another schema and names of tables you can tell spring security where the information about users
and authorities can be found. 

In that case you can set in method this code:
```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    // the same code

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled" +
                                "from my_users" +
                                "where username =?")
                        .authoritiesByUsernameQuery("select username, authority" +
                                "from my_authorities" +
                                "where username =?");
    }
    // the same code
}
```
If you have another datasource you just need do set it in application.properties, and it will inject by spring 
in that method.
```properties
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
```
It is very flexible.
For H2 database you don't need to set properties, because spring uses the default meanings.