package com.example.androidacademy2.news;

public interface NewsFragmentListener {
    void onNewsDetailsClicked(String s);
    void onNewsEditClicked(String s);
    void newsListFragmentStart(int frame);
    void deleteFragmentDetails();
    void deleteFragmentEdit();
}
