package com.randomher0;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.randomher0.databinding.FragmentHomeBinding;
import com.randomher0.rtsp.RTSPForwarder;
import com.randomher0.ui.main.core.Camera;
import com.randomher0.ui.main.core.Restream;
import com.randomher0.ui.main.core.RestreamOptions;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 -> NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_to_Camera));

        binding.buttonSettings.setOnClickListener(view12 -> NavHostFragment.findNavController(HomeFragment.this)
                .navigate(R.id.action_to_Settings));

        binding.buttonConnectCamera.setOnClickListener(v -> {
            Camera camera = CamStreamer.getInstance().getCamera();
            if(camera == null) return;
            camera.connect();
        });

        binding.buttonConnectSsh.setOnClickListener(view13 -> {
            Restream restream = new Restream(binding.getRoot().getContext(), new RestreamOptions());
            restream.start();
        });

        binding.buttonRestream.setOnClickListener(view13 -> {
            RTSPForwarder rtspForwarder = new RTSPForwarder();
            try {
                rtspForwarder.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}