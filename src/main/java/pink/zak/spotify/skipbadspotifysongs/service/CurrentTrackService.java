package pink.zak.spotify.skipbadspotifysongs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CurrentTrackService {
    private final SpotifyApi spotifyApi;
    private final BlockedSongsService blockedSongsService;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    protected void checkCurrentTrack() {
        if (this.spotifyApi.getAccessToken() == null) return;

        this.spotifyApi.getUsersCurrentlyPlayingTrack().build()
                .executeAsync()
                .thenAccept(currentlyPlaying -> {
                    String id = currentlyPlaying.getItem().getId();
                    boolean blocked = this.blockedSongsService.isBlocked(id);

                    if (blocked) this.skipSong();
                });
    }

    private void skipSong() {
        this.spotifyApi.skipUsersPlaybackToNextTrack().build().executeAsync();
    }
}
