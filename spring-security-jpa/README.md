# spring-security-jpa

Simple project with Spring Security using JPA.

Stack: Spring Security, JPA, PostgreSQL.


## Step 1
Create spring boot project using [Spring Initializr](https://start.spring.io/) and add 
spring boot starter web, spring boot starter security, spring-boot-starter-data-jpa, postgresql.

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

```java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Qualifier("myUserDetailsService")
    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder encoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/user").hasAnyRole("USER", "ADMIN")
                .antMatchers("/admin").hasRole("ADMIN")
                .and().formLogin();
    }
}
```
Authentication manager can authenticate the user by user details in a database. That's why we need to create own class 
which implements interface UserDetailsService having a single method "UserDetails loadUserByUserName(String username)" 
and create own class implements interface UserDetails.

But for the beginning we need to create our repository which will be connected to database.

## Step 4
Create UserRepository.
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
}
```
Add the annotation on the top of the main class:
```
@EnableJpaRepositories(basePackageClasses = UserRepository.class)

```

## Step 5
Add settings for database in file application.properties. In my case it is postgreSQL.
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/springsecurity
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.platform=postgres
spring.jpa.database=POSTGRESQL
spring.datasource.initialization-mode=always
# JPA config
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto=update
```
Create database "springsecurity" in advance. 
```
$ sudo -u postgres createdb springsecurity -O root

```
Then fill the table users.
```
$ sudo -u root psql springsecurity
springsecurity=> insert into users values(1,true, '12345', 'ROLE_USER', 'user');
springsecurity=> insert into users values(2,true, '54321', 'ROLE_ADMIN', 'admin');
```

## Step 6
Create an entity which will be a table in our database.
```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String userName;
    private String password;
    private boolean active;
    private String roles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
``` 

## Step 7
Create class MyUserDetails that implements interface UserDetails. Override all his methods.

It will be like our converter from our class User to UserDetails.
```java
@SuppressWarnings("unused")
public class MyUserDetails implements UserDetails {

    private String username;
    private String password;
    private boolean active;
    private List<GrantedAuthority> authorities;

    public MyUserDetails() {
    }

    public MyUserDetails(User user) {
        this.username = user.getUserName();
        this.password = user.getPassword();
        this.active = user.isActive();
        this.authorities = Arrays.stream(user.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
```

## Step 8
Then create class MyUserDetailsService that implements interface UserDetailsService. Override a single method.

This method allows to get a user from database by the username if he exists in the database.
```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).
                orElseThrow(()-> new UsernameNotFoundException("Not found: " + username));
        return new MyUserDetails(user);
    }
}
```
And now if we use a user from our database in login form  he can access to the page by endpoints "/user" or "/admin"
(if he has a role "ADMIN"). If the user can't be found in our database he can only access through endpoint "/".

## Step 9
Run the project using url - http://localhost:8080/login.
