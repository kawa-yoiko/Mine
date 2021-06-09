package com.example.mine;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class VideoPlayerFragment extends Fragment {
    private final Uri uri;

    public VideoPlayerFragment(@NonNull Uri uri) {
        this.uri = uri;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleExoPlayer player = new SimpleExoPlayer.Builder(view.getContext()).build();

        StyledPlayerView playerView = view.findViewById(R.id.player_view);
        playerView.setPlayer(player);

        player.setMediaItem(MediaItem.fromUri(uri));
        android.util.Log.d("VideoPlayerFragment", uri.toString());
        player.prepare();

        playerView.getLayoutParams().height = 640;
        playerView.setAspectRatioListener((targetAspectRatio, naturalAspectRatio, aspectRatioMismatch) -> {
            playerView.setAspectRatioListener(null);
            int h = (int) (playerView.getMeasuredWidth() / targetAspectRatio + 0.5);
            if (h < 640) h = 640;
            Log.d("VPF", "w = " + playerView.getMeasuredWidth() + ", h = " + h);
            playerView.getLayoutParams().height = h;
        });

        /*VideoView videoView = view.findViewById(R.id.player_view);
        videoView.setVideoURI(uri);
        MediaController mediaController = new MediaController(view.getContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_player, container, false);
    }
}
