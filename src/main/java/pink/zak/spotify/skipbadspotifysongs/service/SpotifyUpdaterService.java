package pink.zak.spotify.skipbadspotifysongs.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class SpotifyUpdaterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyUpdaterService.class);

    private final ScheduledExecutorService scheduler;
    private final SpotifyApi spotifyApi;

    @SneakyThrows
    public void setToken(String code) {
        AuthorizationCodeRequest request = this.spotifyApi.authorizationCode(code).build();

        AuthorizationCodeCredentials credentials = request.execute();
        this.setCredentials(credentials);
    }

    @SneakyThrows
    private void refresh() {
        LOGGER.info("Refreshing credentials");

        AuthorizationCodeCredentials credentials = this.spotifyApi.authorizationCodeRefresh().build().execute();
        this.setCredentials(credentials);
    }

    private void setCredentials(AuthorizationCodeCredentials credentials) {
        LOGGER.info("Set credentials :)");

        this.spotifyApi.setAccessToken(credentials.getAccessToken());
        this.spotifyApi.setRefreshToken(credentials.getRefreshToken());

        this.scheduler.schedule(this::refresh, credentials.getExpiresIn() - 120, TimeUnit.SECONDS);
    }
}
