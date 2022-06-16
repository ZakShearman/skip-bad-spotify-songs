package pink.zak.spotify.skipbadspotifysongs.controller.command;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import pink.zak.spotify.skipbadspotifysongs.service.BlockedSongsService;
import pink.zak.spotify.skipbadspotifysongs.service.SpotifyUpdaterService;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.AuthorizationScope;

@ShellComponent
@RequiredArgsConstructor
public class SpotifyCommands {
    private final SpotifyUpdaterService spotifyUpdaterService;
    private final BlockedSongsService blockedSongsService;
    private final SpotifyApi spotifyApi;

    @ShellMethod("Get Spotify OAuth2 URL")
    public String getUrl() {
        return this.spotifyApi.authorizationCodeUri()
                .scope(
                        AuthorizationScope.USER_READ_PRIVATE,
                        AuthorizationScope.PLAYLIST_READ_COLLABORATIVE,
                        AuthorizationScope.PLAYLIST_READ_PRIVATE,
                        AuthorizationScope.STREAMING,
                        AuthorizationScope.USER_READ_PLAYBACK_STATE,
                        AuthorizationScope.USER_MODIFY_PLAYBACK_STATE,
                        AuthorizationScope.USER_READ_CURRENTLY_PLAYING,
                        AuthorizationScope.APP_REMOTE_CONTROL
                )
                .show_dialog(false)
                .build().execute().toString();
    }

    @ShellMethod
    public void login(@ShellOption String code) {
        this.spotifyUpdaterService.setToken(code);
        this.blockedSongsService.updatePlaylists();
    }
}
