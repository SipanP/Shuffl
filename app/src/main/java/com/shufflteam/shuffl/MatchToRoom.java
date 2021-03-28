package com.shufflteam.shuffl;

import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchToRoom {
     /* IDEAS:

    ROOM ALLOCATION:

    What do we know:
    - List of users to allocate and the associated playlists
    - List of rooms which still have space

    > iterate over new users' associated playlist and calculate stats
    > for every user: iterate over the existing rooms and keep the best one that has at least
      something in common
        >> nothing in common: then repeat this after changing stats
        >> good match: allocate the user to that room. Update room's playlist


    Stats:
    Map<Artist, double> frequency;
    +1 for every track he produced as main artist
    +0.5 for every track he produced as secondary artist

    Changing stats options:
    1) getting artist recommendation from web API passing the top three artists as argument
    2) getting artist recommendation from web API passing the entire associated playlist

    ------------------------------------------------

    ROOM PLAYLIST AND SONG CHOICE

    Room's playlist:
    - songs are played using users' accounts.
      If a user leaves while one of its songs is playing, poll next song from the queue. If no more
      songs are available, play again all the songs or look mat related songs (see queue part below)
    - maintain queue of songs that is updated after every song or when a new user joins (when you
      start playing a new song, remove it from the queue)

    List<Track, User> allSongs;
    Contains all the songs of the users.

    List<Track, User> queue;
    Sorted at the end of every track's play.
    Track gets higher score if the user is nearer to the speakers (because he is there to listen to music)
    When a song ends, the top one of the queue is retrieved and removed. If there are few (or no) songs
      left, look at top artist's top songs or play again all the songs (probably easier to play the
      playlist from the beginning).

     */

    /**
     * In client-side: user presses the join button of a certain playlist. A request to get the
     *                 playlist data is sent to SpotifyAppRemote using APIs.
     *                 The data of the playlist is now processed to generate the stats and to get a
     *                 list of the tracks in a nicer way.
     *                 A request is sent to the backend to be allocated to a room (we pass the playlist,
     *                 its stats and the user who sent the request).
     *                 Backend elaborates the request:
     *                  - adds the user to the pool of users to allocate using this method.
     * what do I have:
     * sendJoinRequest: send user information and list of song titles and related artists
     *
     * backend knowledge: rooms list. for every room we know the users in it and the shared playlist
     *
     * algos to design:
     * 1) when a user wants to join: compare its playlist data with the data of the rooms and choose
     *    the best one (or create a new one based on the threshold)
     * 2) when a new user is assigned to a room, we add its playlist data to the room data
     *
     * N.B. when someone leaves we need to be able to remove its playlist data from the room playlist
     * so we can't use only one shared playlist (or we have to keep track who is the user related to every song)
     */

    public static MockRoom getBestRoomForUser(User newUser, MockPlaylist newUsersTracks, Map<MockRoom, MockPlaylist> roomsPlaylists) {
        int bestScore = -1;
        MockRoom bestMockRoom = null;
        for (MockRoom mockRoom : roomsPlaylists.keySet()) {
            int score = calculateScore(newUsersTracks, roomsPlaylists.get(mockRoom));
            if (score > bestScore) {
                bestMockRoom = mockRoom;
            }
        }

        if (bestMockRoom == null) {
            // there is no room available. Create a new room
            bestMockRoom = new MockRoom();
        }

        return bestMockRoom;
    }

    private static int calculateScore(MockPlaylist newUsersTracks, MockPlaylist roomTracks) {
        Map<Artist, Integer> artistOccurrences = countArtistOccurrences(newUsersTracks);

        int score = 0;
        for (Track track: roomTracks.tracks) {
            score += artistOccurrences.getOrDefault(track.artist, 0);
        }

        return score;
    }

    public static MockPlaylist mergePlaylistsAndSort(MockPlaylist userTracks, MockPlaylist roomTracks) {
        Map<Track, Boolean> allTracks = new HashMap<>();

        for (int i = 0; i < userTracks.tracks.size(); i++) {
            allTracks.put(userTracks.tracks.get(i), false);
        }
        for (int i = 0; i < roomTracks.tracks.size(); i++) {
            allTracks.put(roomTracks.tracks.get(i), roomTracks.alreadyPlayed.get(i));
        }

        Track[] tracks = new Track[allTracks.size()];
        Boolean[] alreadyPlayed = new Boolean[allTracks.size()];
        int index = 0;
        for (Track track: allTracks.keySet()) {
            tracks[index] = track;
            alreadyPlayed[index] = allTracks.get(track);
            index++;
        }

        MockPlaylist result = new MockPlaylist(tracks, alreadyPlayed);
        sortPlaylist(result);
        return result;

    }

    public static void sortPlaylist(MockPlaylist roomTracks) {
        Map<Artist, Integer> artistOccurrences = countArtistOccurrences(roomTracks);
        roomTracks.sort(artistOccurrences);
    }

    private static Map<Artist, Integer> countArtistOccurrences(MockPlaylist tracks) {
        Map<Artist, Integer> artistOccurrences = new HashMap<>();
        for (Track track: tracks.tracks) {
            int cnt = artistOccurrences.getOrDefault(track.artist, 0);
            artistOccurrences.put(track.artist, cnt + 1);
        }
        return artistOccurrences;
    }
}


class MockRoom {

}

class MockPlaylist {
    List<Track> tracks;
    List<Boolean> alreadyPlayed;

    public MockPlaylist(Track[] tracks) {
        this.tracks = Arrays.asList(tracks);
        alreadyPlayed = new ArrayList<>(tracks.length);
        for (int i = 0; i < tracks.length; i++) {
            alreadyPlayed.add(false);
        }
    }

    public MockPlaylist(Track[] tracks, Boolean[] alreadyPlayed) {
        this.tracks = Arrays.asList(tracks);
        this.alreadyPlayed = Arrays.asList(alreadyPlayed);
    }

    public void sort(Map<Artist, Integer> artistOccurrences) {
        List<Wrapper> wrappedTracks = new ArrayList<>(tracks.size());
        for (int i = 0; i < wrappedTracks.size(); i++) {
            wrappedTracks.add(new Wrapper(tracks.get(i), alreadyPlayed.get(i)));
        }

        // sort playlist to have:
        //   - already played songs before (among the already played songs, the ones of the most ranked
        //     artists are before)
        //   - songs to play after (among the songs to play, the one of the most ranked artists are before)
        // N.B. we can optimise the lambda by merging the first and forth case (because inside the
        //     body of those cases we return the same value). I wrote it like this for clarity
        wrappedTracks.sort(
                (first, second) -> {
                    if (first.played == second.played) {
                        return artistOccurrences.get(second.track.artist) - artistOccurrences.get(first.track.artist);
                    } else if (first.played) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
        );

        for (int i = 0; i < wrappedTracks.size(); i++) {
            tracks.set(i, wrappedTracks.get(i).track);
            alreadyPlayed.set(i, wrappedTracks.get(i).played);
        }


    }

    class Wrapper{
        private final Track track;

        private final boolean played;

        public Wrapper(Track track, boolean played) {
            this.track = track;
            this.played = played;
        }
    }
}