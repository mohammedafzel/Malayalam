package com.example.mohammedafzel.malayalam;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Mohammed Afzel on 13-11-2017.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class PhrasesFragment extends Fragment {

    //Handles playback of all the sound files
    private MediaPlayer mMediaPlayer;

    //Handles audio focus when playing a sound file
    private AudioManager mAudioManager;

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        //The AUDIOFOCUS_LOSS_TRANSIENTcase means that we've lost audio focus for a
                        //short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                        //our app is allowed to continue playing soumd but a lower volume. We'll treat
                        //both cases the same way because our app is playing short sound files.

                        //Pause playback and rset player to the start of the file. That way, we can
                        //play the word from the beginning when we resume playback.
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        //The AUDIOFOCUS_GAIN case means we've regained focus and can
                        //resume playback
                        mMediaPlayer.start();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //The AUDIOFOCUS_LOSS case means we've lost audio focus and
                        //stop playback and cleanup resources
                        releaseMediaplayer();
                    }
                }
            };
    /**
     * This listener gets triggered when the {@link MediaPlayer} has completed
     * playing the audio file.
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaplayer();
        }
    };

    public PhrasesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        //Create and  setup the {@link AudioManager} to request audio focus
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        /*super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
*/
        //Create list of words
        final ArrayList<Word> words = new ArrayList<Word>();

        words.add(new Word("What is your name?", "Ningalude peru enthaanu? ", R.raw.phrase_what_is_your_name));
        words.add(new Word("My name is...", "Ende peru...", R.raw.phrase_my_name_is));
        words.add(new Word("How are you?", "Ningalk sukhamaano?", R.raw.phrase_how_are_you));
        words.add(new Word("I'm feeling good", "Enikk sukhamaanu", R.raw.phrase_im_feeling_good));
        words.add(new Word("Where are you going?", "Ningal evideyaanu povunnad?", R.raw.phrase_where_are_you_going));
        words.add(new Word("Are you coming?", "Ningal varunnuvo?", R.raw.phrase_are_you_coming));
        words.add(new Word("Yes. I'm coming.", "Athe, njan varunnu", R.raw.phrase_yes_im_coming));
        words.add(new Word("I'm coming.", "Njan varunnu", R.raw.phrase_im_coming));
        words.add(new Word("Come, let's go.", "Varoo, nammalk povaam", R.raw.phrase_lets_go));
        words.add(new Word("Come here.", "Ivide varoo", R.raw.phrase_come_here));
        words.add(new Word("Help me.", "Enne sahaayikkoo", R.raw.phrase_help_me));
        words.add(new Word("Thanks.", "Nanni", R.raw.phrase_thanks));
        //words.add(new Word("Birthday greetings.", "Janma dina aashamsakal", R.raw.phrase_biryhday_greetings));

        //Create an {@link WordAdapter}, whose data source is a lost of {!link Word}s. The
        //adapter knows how to create a list items for each item in the list.
        WordAdapter adapter =
                new WordAdapter(getActivity(), words, R.color.category_phrases);

        ListView listView = (ListView) rootView.findViewById(R.id.list);

        //{@link ListView} will display list items for each {@link Word} in the list
        listView.setAdapter(adapter);

        //set a click listener to play the audio when the list item is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Release the media player if it currently exists,
                // because we are about to play a different sound file
                releaseMediaplayer();

                //Get the {@link Word} object at the given position the user clicked on
                Word word = words.get(position);

                //Request audio focus for audio playback
                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        //use the music stream
                        AudioManager.STREAM_MUSIC,
                        //Request permanent focus
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //mAudioManager.registerMediaButtonEventReceiver(RemoteControlReceiver);
                    //We've audio focus now.

                    //Create and set up the {@link MediaPlayer} for the audio resource associated
                    //with the current word.
                    mMediaPlayer = MediaPlayer.create(getActivity(), word.getAudioResourceId());

                    //start the audio file
                    mMediaPlayer.start();

                    //Set up a listener on the media player, so that we can stop and release the media player once
                    //sound has finished playing.
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        // When the activity is stopped, release the media player resources because we won't
        // be playing any more sounds.
        releaseMediaplayer();
    }

    /*Clean up the media player by releasing its resources*/
    private void releaseMediaplayer() {

        //if the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {

            //regardless of the current state of the media player, release its resources
            //because we no longe need it.
            mMediaPlayer.release();

            //Set the media player back to null. For our code, we've decided that
            //setting the media player to null is an easy way to tell that the media player
            //is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            //Regardless of whether or not we were granted audio focus, abandon it. This also
            //unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }
}
