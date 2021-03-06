package de.techdev.trackr.domain.employee.login;

import de.techdev.trackr.core.security.OAuth2ServerConfiguration;
import de.techdev.trackr.core.security.RemoveTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * @author Moritz Schulze
 */
@RepositoryEventHandler(Credential.class)
@Slf4j
public class CredentialEventHandler {

    @Autowired
    private RemoveTokenService removeTokenService;

    @HandleBeforeCreate
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void beforeSaveAuthority(Credential credential) {
        log.debug("Creating credential {}", credential);
    }

    @HandleBeforeSave
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void beforeUpdateAuthority(Credential credential) {
        log.debug("Updating credential {}", credential);
    }

    @HandleAfterDelete
    @HandleAfterSave
    public void afterDeleteOrUpdateDeleteTokens(Credential credential) {
        removeTokenService.removeTokens(OAuth2ServerConfiguration.TRACKR_PAGE_CLIENT, credential.getEmail());
    }

    @HandleBeforeDelete
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void beforeDeleteAuthority(Credential credential) {
        log.debug("Deleting credential {}", credential);
    }

    /**
     * Only admins may add roles.
     *
     * @param credential  The credential object
     * @param authorities The authorities to set
     */
    @HandleBeforeLinkSave
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void beforeAuthoritySave(Credential credential, List<Authority> authorities) {
        log.debug("Updating credential authorities for user {} to {}.", credential, authorities);
        removeTokenService.removeTokens(OAuth2ServerConfiguration.TRACKR_PAGE_CLIENT, credential.getEmail());
    }

    /**
     * Only admins may delete roles
     * @param credential The credentials to update
     */
    @HandleBeforeLinkDelete
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void beforeAuthorityDelete(Credential credential) {
        log.debug("Deleted linked object from credential, is now {}.", credential);
        removeTokenService.removeTokens(OAuth2ServerConfiguration.TRACKR_PAGE_CLIENT, credential.getEmail());
    }
}
