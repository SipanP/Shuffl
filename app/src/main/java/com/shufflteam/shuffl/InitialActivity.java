package com.shufflteam.shuffl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.Locale;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class InitialActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "2e42d37ff4024cffa8b868e7a30061ca";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private Call mCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        getSupportActionBar().setTitle(String.format(
                Locale.US, "Spotify Auth Sample %s", com.spotify.sdk.android.auth.BuildConfig.VERSION_NAME));
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

//    public void onGetUserProfileClicked(View view) {
//        if (mAccessToken == null) {
//            final Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_initial), R.string.warning_need_token, Snackbar.LENGTH_SHORT);
//            snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
//            snackbar.show();
//            return;
//        }
//
//        final Request request = new Request.Builder()
//                .url("https://api.spotify.com/v1/me")
//                .addHeader("Authorization","Bearer " + mAccessToken)
//                .build();
//
//        cancelCall();
//        mCall = mOkHttpClient.newCall(request);
//
//        mCall.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                setResponse("Failed to fetch data: " + e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    final JSONObject jsonObject = new JSONObject(response.body().string());
//                    setResponse(jsonObject.toString(3));
//                } catch (JSONException e) {
//                    setResponse("Failed to parse data: " + e);
//                }
//            }
//        });
//    }

//    public void onRequestCodeClicked(View view) {
//        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
//        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request);
//    }

    public void onRequestTokenClicked(View view) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, "com.shufflteam.shuffl://callback")
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (response.getError() != null && response.getError().isEmpty()) {
            System.out.println("Error");
//          setResponse(response.getError());
        }
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();
            System.out.println(mAccessToken);
            updateTokenView();

            final String accessToken = mAccessToken;

            SpotifyApi spotifyApi = new SpotifyApi();

            if (accessToken != null) {
                spotifyApi.setAccessToken(accessToken);
            } else {
                System.out.println("No valid access token");
            }
            SpotifyService spotify = spotifyApi.getService();


            spotify.getMe(new Callback<UserPrivate>() {
                @Override
                public void success(UserPrivate userPrivate, Response response) {
                    System.out.println("Obtained User Information.");
                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("FAILED to Obtain User Information.");
                }
            });

            spotify.getPlaylistTracks("225dv6jfkmgoylbeqvjatv3sy", "5Jf7ydhHna8Xt75Wzbk5nL", new Callback<Pager<PlaylistTrack>>() {
                @Override
                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    Log.d("Album success", playlistTrackPager.items.get(5).track.name);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Album failure", error.toString());
                }
            });


            spotify.getTrack("2b6x5oV0ho9kvsPrYtuIcf", new Callback<Track>() {
                @Override
                public void success(Track track, Response response) {
                    Log.d("Album success", track.name);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Album failure", error.toString());
                }
            });

            spotify.getAlbum("4Yu6DuhOreMSxWngOn5Ya4", new Callback<Album>() {
                @Override
                public void success(Album album, Response response) {
                    Log.d("Album success", album.name);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d("Album failure", error.toString());
                }
            });

//            spotify.searchAlbums("0vrKGjXSGcTsxNGxQdXT5p", new Callback<AlbumsPager>() {
//
//                @Override
//                public void success(AlbumsPager albumsPager, Response response) {
//                    System.out.println(albumsPager.albums.items.get(0).name);
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    System.out.println("Error");
//                }
//            });

//            spotify.searchTracks(query, options, new SpotifyCallback<TracksPager>() {
//                @Override
//                public void success(TracksPager tracksPager, Response response) {
//                    listener.onComplete(tracksPager.tracks.items);
//                }
//
//                @Override
//                public void failure(SpotifyError error) {
//                    listener.onError(error);
//                }
//            });

        }
//        else if (requestCode == AUTH_CODE_REQUEST_CODE) {
//            mAccessCode = response.getCode();
//            updateCodeView();
//        }
    }

//    private void setResponse(final String text) {
//        runOnUiThread(() -> {
//            final TextView responseView = findViewById(R.id.response_text_view);
//            responseView.setText(text);
//        });
//    }

    private void updateTokenView() {
        final TextView tokenView = findViewById(R.id.tokenTextView);
        tokenView.setText(getString(R.string.token, mAccessToken));
    }

//    private void updateCodeView() {
//        final TextView codeView = findViewById(R.id.code_text_view);
//        codeView.setText(getString(R.string.code, mAccessCode));
//    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}