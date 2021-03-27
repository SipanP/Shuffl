package com.shufflteam.shuffl;

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

    // List <Pair<MockUser, MockPlaylist>> usersToAllocate;
    // List <MockRoom> availableRooms;

    // addUserToAllocate(User user, Playlist playlist) -> add it to usersToAllocate
    // matchToRandomRoom(...) TODO
    // private void allocateUser()

}