package com.github.camelya58.resourceserver.util;

import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

/**
 * Class JwtConverter
 *
 * @author Kamila Meshcheryakova
 * created 30.07.2020
 */
@Component
public class JwtConverter extends DefaultAccessTokenConverter
        implements JwtAccessTokenConverterConfigurer {
    @Override
    public void configure(JwtAccessTokenConverter converter) {
        converter.setAccessTokenConverter(this);
    }
}
