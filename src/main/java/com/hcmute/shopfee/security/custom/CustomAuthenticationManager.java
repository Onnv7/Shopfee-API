package com.hcmute.shopfee.security.custom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomAuthenticationManager implements AuthenticationManager, MessageSourceAware, InitializingBean {
    private static final Log logger = LogFactory.getLog(CustomAuthenticationManager.class);
    protected MessageSourceAccessor messages;
    private List<AuthenticationProvider> providers = new ArrayList<>();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Class<? extends Authentication> toTest = authentication.getClass();
        AuthenticationException lastException = null;
        AuthenticationException parentException = null;
        Authentication result = null;
        Authentication parentResult = null;
        int currentPosition = 0;
        int size = this.providers.size();
        Iterator var9 = this.getProviders().iterator();
        while (var9.hasNext()) {
            AuthenticationProvider provider = (AuthenticationProvider) var9.next();
            if (provider.supports(toTest)) {
                if (logger.isInfoEnabled()) {
                    Log var10000 = logger;
                    String var10002 = provider.getClass().getSimpleName();
                    ++currentPosition;
                    var10000.info(LogMessage.format("Authenticating request with %s (%d/%d)", var10002, currentPosition, size));
                }
                try {
                    result = provider.authenticate(authentication);
                } catch (AuthenticationException e) {
                    lastException = e;
                }

            }

        }
        if (result != null) {
            // thấy mặc định sau khi authen thì erase
//            if (this.eraseCredentialsAfterAuthentication && result instanceof CredentialsContainer) {
//                ((CredentialsContainer)result).eraseCredentials();
//            }
            return result;
        } else {
            if (lastException == null) {
                lastException = new ProviderNotFoundException(this.messages.getMessage("ProviderManager.providerNotFound", new Object[]{toTest.getName()}, "No AuthenticationProvider found for {0}"));
            }
        }
        throw lastException;
    }

    public List<AuthenticationProvider> getProviders() {
        return this.providers;
    }

    public void addProvider(AuthenticationProvider provider) {
        providers.add(provider);
    }
    public void afterPropertiesSet() {
        this.checkState();
    }

    private void checkState() {
        Assert.isTrue(!this.providers.isEmpty(), "A parent AuthenticationManager or a list of AuthenticationProviders is required");
        Assert.isTrue(!CollectionUtils.contains(this.providers.iterator(), (Object)null), "providers list cannot contain null values");
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
