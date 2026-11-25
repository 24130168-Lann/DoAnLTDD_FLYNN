package com.example.doanltdd_flynn.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doanltdd_flynn.R;

/** Màn hình giữ chỗ cho các tab chưa làm (Trang chủ, Bách khoa, Cộng đồng, Cá nhân). */
public class PlaceholderFragment extends Fragment {
    private static final String ARG_TEXT = "text";

    public static PlaceholderFragment newInstance(String text) {
        PlaceholderFragment f = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        TextView tv = view.findViewById(R.id.tvPlaceholder);
        Bundle args = getArguments();
        tv.setText(args == null ? "" : args.getString(ARG_TEXT, ""));
        return view;
    }
}
