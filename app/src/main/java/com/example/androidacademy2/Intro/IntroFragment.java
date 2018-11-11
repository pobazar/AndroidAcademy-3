package com.example.androidacademy2.Intro;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.androidacademy2.R;

import androidx.fragment.app.Fragment;

public class IntroFragment extends Fragment {

    int pageNumber;
    private static final String ARGS_NUMBER = "page_number";

    static public IntroFragment newInstance(int page) {
        IntroFragment pageFragment = new IntroFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_NUMBER, page);
        pageFragment.setArguments(bundle);
        return pageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_fragment,
                container, false);

        if (getArguments() != null) {
            pageNumber = getArguments().getInt(ARGS_NUMBER);
        } else {
            pageNumber = -1;
        }
        ImageView image = view.findViewById(R.id.image_intro);

        switch (pageNumber) {
            case 0:
                image.setImageResource(R.drawable.news_screen);
                break;
            case 1:
                image.setImageResource(R.drawable.news_details_screen);
                break;
            case 2:
                image.setImageResource(R.drawable.about_screen);
                break;
            default:
                image.setImageResource(R.drawable.black_square);
                break;
        }


        return view;
    }
}
