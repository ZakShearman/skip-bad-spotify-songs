package pink.zak.spotify.skipbadspotifysongs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import se.michaelthelin.spotify.SpotifyApi;

import java.net.URI;
import java.util.Set;

@ConfigurationProperties("spotify")
@ConstructorBinding
public record SpotifyConfig(String clientId, String clientSecret, URI redirectUri,
                            Set<String> blockPlaylistIds) {

    @Bean
    public SpotifyApi spotifyApi() {
        return SpotifyApi.builder()
                .setClientId(this.clientId)
                .setClientSecret(this.clientSecret)
                .setRedirectUri(this.redirectUri)
                .build();
    }
}
