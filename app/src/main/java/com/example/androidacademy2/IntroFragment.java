package com.example.androidacademy2;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

public class IntroFragment extends Fragment {

    int pageNumber;

   IntroFragment newInstance(int page) {
        IntroFragment pageFragment = new IntroFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("page_number", page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.intro_fragment,
                container, false);

        pageNumber = getArguments().getInt("page_number");
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
        }


        return view;
    }
}
