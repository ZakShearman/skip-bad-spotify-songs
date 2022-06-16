package pink.zak.spotify.skipbadspotifysongs.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pink.zak.spotify.skipbadspotifysongs.service.BlockedSongsService;
import pink.zak.spotify.skipbadspotifysongs.service.SpotifyUpdaterService;

@RestController
@RequiredArgsConstructor
public class CallbackController {
    private final SpotifyUpdaterService spotifyUpdaterService;
    private final BlockedSongsService blockedSongsService;

    @SneakyThrows
    @GetMapping
    public void parseCallbackCode(@RequestParam String code) {
        this.spotifyUpdaterService.setToken(code);
        this.blockedSongsService.updatePlaylists();
    }
}
