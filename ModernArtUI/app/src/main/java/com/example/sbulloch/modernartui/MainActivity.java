package com.example.sbulloch.modernartui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;


public class MainActivity extends Activity {
    public static final String TAG = "ModernArt";
    private static final String URL = "http://www.moma.org";

    SeekBar mSeekBar;
    View box1;
    int box1Color;
    View box3;
    int box3Color;
    View box4;
    int box4Color;
    View box8;
    int box8Color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        box1 = findViewById(R.id.box1);
        box3 = findViewById(R.id.box3);
        box4 = findViewById(R.id.box4);
        box8 = findViewById(R.id.box8);

        // possible TODO store colors as tags on Views instead of as class variables
        box1Color = ((ColorDrawable) box1.getBackground()).getColor();
        box3Color = ((ColorDrawable) box3.getBackground()).getColor();
        box4Color = ((ColorDrawable) box4.getBackground()).getColor();
        box8Color = ((ColorDrawable) box8.getBackground()).getColor();

        mSeekBar.setMax(360);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int box1new = rotateColor(progress, box1Color);
                box1.setBackgroundColor(box1new);

                int box3new = rotateColor(progress, box3Color);
                box3.setBackgroundColor(box3new);

                int box4new = rotateColor(progress, box4Color);
                box4.setBackgroundColor(box4new);

                int box8new = rotateColor(progress, box8Color);
                box8.setBackgroundColor(box8new);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_more_info) {
            MoreInfoDialogFragment dialog = MoreInfoDialogFragment.newInstance();
            dialog.show(getFragmentManager(), "more_info");
        }

        return super.onOptionsItemSelected(item);
    }

    private int rotateColor(int progress, int origColor) {
        float[] hsvColor = new float[3];
        Color.colorToHSV(origColor, hsvColor);
        hsvColor[0] = hsvColor[0] + progress;
        hsvColor[0] = hsvColor[0] % 360;
        return Color.HSVToColor(Color.alpha(origColor), hsvColor);
    }

    private int rotateColorOld(int change, int currColor, int origColor) {
        // almost works, but only for continuous seek bar. if the user jumps forward, the colors are not updated correctly
        // change = progress - lastProgress

        int oRed = Color.red(origColor);
        int oGrn = Color.green(origColor);
        int oBlue = Color.blue(origColor);

        int totalChange = Math.abs(oRed - oGrn) + 2 * Math.abs(oBlue - oRed);
        int increment = Math.round(totalChange / 100f);
        increment = 1;

        int red = Color.red(currColor);
        int grn = Color.green(currColor);
        int blue = Color.blue(currColor);

        if ((red == Math.max(red, Math.max(grn, blue)) && grn == blue) || (red > grn && grn > blue)) {
            // entering or are in section 1. increase green till it matches current value of red
            int newGrn = grn + (change * increment);
            if (newGrn > red) {
                newGrn = red;
            }
            return Color.rgb(red, newGrn, blue);
        } else if ((blue == Math.min(red, Math.min(grn, blue)) && red == grn) || (grn > red && red > blue)) {
            // entering or are in section 2. decrease red till it matches current value of blue
            int newRed = red - (change * increment);
            if (newRed < blue) {
                newRed = blue;
            }
            return Color.rgb(newRed, grn, blue);
        } else if ((grn == Math.max(red, Math.max(grn, blue)) && red == blue) || (grn > blue && blue > red)) {
            // entering or are in section 3. increase blue till it matches current value of green
            int newBlue = blue + (change * increment);
            if (newBlue > grn) {
                newBlue = grn;
            }
            return Color.rgb(red, grn, newBlue);
        } else if ((red == Math.min(red, Math.min(grn, blue)) && blue == grn) || (blue > grn && grn > red)) {
            // entering or are in section 4. decrease green till it matches current value of red
            int newGrn = grn - (change * increment);
            if (newGrn < red) {
                newGrn = red;
            }
            return Color.rgb(red, newGrn, blue);
        } else if ((blue == Math.max(red, Math.max(grn, blue)) && red == grn) || (blue > red && red > grn)) {
            // entering or are in section 5. increase red till it matches current value of blue
            int newRed = red + (change * increment);
            if (newRed > blue) {
                newRed = blue;
            }
            return Color.rgb(newRed, grn, blue);
        } else {
            // entering or are in section 6. decrease blue till it matches current value of green
            int newBlue = blue - (change * increment);
            if (newBlue < grn) {
                newBlue = grn;
            }
            return Color.rgb(red, grn, newBlue);
        }
    }

    private int invertColor(int progress, int currColor, int boxNum) {
//        int origR = ( R.color.yellow >> 16 ) & 0x000000FF;
//        int origG = ( R.color.yellow >> 8 ) & 0x000000FF;
//        int origB = R.color.yellow & 0x000000FF;

        Log.i(TAG, "progress = " + progress + " currColor = " + currColor + " boxNum = " + boxNum);
        int invertedColor = (0x00FFFFFF - (currColor | 0xFF000000)) |
                (currColor & 0xFF000000);

        int redInv = 255 - Color.red(currColor);
        int grnInv = 255 - Color.green(currColor);
        int blueInv = 255 - Color.blue(currColor);

        // assumes progress is max 100
        int red = Color.red(currColor) + (redInv - Color.red(currColor)) * progress / 100;
        int grn = Color.green(currColor) + (grnInv - Color.green(currColor)) * progress / 100;
        int blue = Color.blue(currColor) + (blueInv - Color.blue(currColor)) * progress / 100;

        int oRed = Color.red(currColor);
        int oGrn = Color.green(currColor);
        int oBlue = Color.blue(currColor);

        return Color.rgb(red, grn, blue);
    }


    // Class that creates the AlertDialog
    public static class MoreInfoDialogFragment extends DialogFragment {

        public static MoreInfoDialogFragment newInstance() {
            return new MoreInfoDialogFragment();
        }

        // Build AlertDialog using AlertDialog.Builder
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(getString(R.string.more_info))

                            // Set up No Button
                    .setNegativeButton(getString(R.string.not_now),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // do nothing. dialog will just close
                                }
                            })

                            // Set up Yes Button
                    .setPositiveButton(getString(R.string.visit_moma),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {

                                    Intent baseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                                    Intent chooserIntent = Intent.createChooser(baseIntent, getString(R.string.open_with));

                                    startActivity(chooserIntent);
                                }
                            }).create();
        }
    }
}
