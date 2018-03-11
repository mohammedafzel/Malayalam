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
public class FamilyFragment extends Fragment {


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
    public FamilyFragment() {
        // Required empty public constructor
    }

    /*This listener get triggered when the {@link MediaPlayer} has completed
      * playing the audio file.
      */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaplayer();
        }
    };


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
        //Create list of words
        words.add(new Word("father", "acchan", R.drawable.family_father, R.raw.family_father));
        words.add(new Word("mother", "amma", R.drawable.family_mother, R.raw.family_mother));
        words.add(new Word("son", "makan", R.drawable.family_son, R.raw.family_son));
        words.add(new Word("daughter", "makal", R.drawable.family_daughter, R.raw.family_daughter));
        words.add(new Word("elder brother", "jyeshtan", R.drawable.family_older_brother, R.raw.family_older_brother));
        words.add(new Word("younger brother", "anujan", R.drawable.family_younger_brother, R.raw.family_younger_brother));
        words.add(new Word("elder sister", "jyeshtathi", R.drawable.family_older_sister, R.raw.family_older_sister));
        words.add(new Word("younger sister", "anujathi", R.drawable.family_younger_sister, R.raw.family_younger_sister));
        words.add(new Word("grandfather", "muthacchan", R.drawable.family_grandfather, R.raw.family_grandfather));
        words.add(new Word("grandmother", "muthashi", R.drawable.family_grandmother, R.raw.family_grandmother));

        //Create an {@link WordAdapter}, whose data source is a lost of {!link Word}s. The
        //adapter knows how to create a list items for each item in the list.
        WordAdapter adapter =
                new WordAdapter(getActivity(), words, R.color.category_family);

        ListView listView = (ListView) rootView.findViewById(R.id.list);

        //{@link ListView} will display list items for each {@link Word} in the list
        listView.setAdapter(adapter);

        //set a click listener to play the audio when the list item is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the {@link Word} object at the given position the user clicked on
                Word word = words.get(position);

                //Release the media player if it currently exists,
                // because we are about to play a different sound file
                releaseMediaplayer();

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
        if(mMediaPlayer != null) {

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
