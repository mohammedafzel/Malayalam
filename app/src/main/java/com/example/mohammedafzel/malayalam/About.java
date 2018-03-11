package com.example.mohammedafzel.malayalam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Created by Mohammed Afzel on 13-11-2017.
 */

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*setContentView(R.layout.activity_about);*/


        Element adsElement = new Element();
        adsElement.setTitle("Thank you for using my application.");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                //*  .setImage(R.drawable.image)*//*
                .setDescription("This app helps beginners to learn malayalam words and phrases easily with proper pronunciation.")
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(adsElement)
                .addGroup("Connect with us")
                .addEmail("er.mohammed.afzel@gmail.com")
                .addGitHub("mohammedafzel")
                //*.addWebsite("http://mohammedafzel.ml")*//*
                //*.addFacebook("mohammedafzel")*//*
                // *.addPlayStore("My Playstore")*//*
                .addItem(createCopyright())
                .create();
        setContentView(aboutPage);

    }

    private Element createCopyright() {
        Element copyright = new Element();
        final String copyrightString = String.format("Copyright %d by Mohammed Afzel", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setIcon(R.mipmap.ic_launcher);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(About.this, copyrightString, Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }

}
