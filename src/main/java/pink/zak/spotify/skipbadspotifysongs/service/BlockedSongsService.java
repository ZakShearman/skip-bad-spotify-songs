package pink.zak.spotify.skipbadspotifysongs.service;

import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pink.zak.spotify.skipbadspotifysongs.config.SpotifyConfig;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.IPlaylistItem;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockedSongsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockedSongsService.class);

    private final SpotifyApi spotifyApi;
    private final SpotifyConfig spotifyConfig;

    private Set<String> blockedSongIds;

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void updatePlaylists() {
        if (this.spotifyApi.getAccessToken() == null) {
            LOGGER.warn("Didn't update playlists as access token is not set!");
            return;
        }
        LOGGER.info("Updating playlists.");

        Set<PlaylistTrack> tracks = new HashSet<>();

        this.spotifyConfig.blockPlaylistIds().parallelStream()
                .map(id -> this.spotifyApi.getPlaylist(id).build())
                .map(request -> {
                    try {
                        return request.execute();
                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(playlist -> playlist.getTracks().getItems())
                .forEach(playlistTracks -> Collections.addAll(tracks, playlistTracks));

        this.blockedSongIds = tracks
                .stream()
                .map(PlaylistTrack::getTrack)
                .map(IPlaylistItem::getId)
                .collect(Collectors.toUnmodifiableSet());

        LOGGER.info("Updated blocked songs (current size {}).", this.blockedSongIds.size());
    }

    public boolean isBlocked(@NotNull String id) {
        return this.blockedSongIds.contains(id);
    }
}
