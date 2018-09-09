package com.derek.chinesewriting;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChineseWriting extends AppCompatActivity {
    private String TAG = "Chinese";

    private View mContentView;
    private ImageView imageView;
    private ImageView imageViewBack;
    private ChineseCanvas canvas;
    private TextView targetSizeView;
    private TextView writingSizeView;
    private TextView rateView;
    private View loading;
    private View btn;

    private List<Chinese> chineseList;
    private int curIndex;

    private Bitmap bitmap;
    private Bitmap canvasBitmap;

    private int targetSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mContentView = findViewById(R.id.fullscreen_content);
        canvas = findViewById(R.id.canvas);
        imageView = findViewById(R.id.image_view);
        imageViewBack = findViewById(R.id.image_view_back);
        targetSizeView = findViewById(R.id.target_size);
        writingSizeView = findViewById(R.id.writing_size);
        rateView = findViewById(R.id.rate);
        loading = findViewById(R.id.loading);
        btn = findViewById(R.id.btn);

        initData();

        imageView.setDrawingCacheEnabled(true);
        imageViewBack.setDrawingCacheEnabled(true);

        imageView.setImageResource(chineseList.get(curIndex).getIdRes());
        imageViewBack.setImageResource(chineseList.get(curIndex).getIdResBack());

        findViewById(R.id.btn_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curIndex > 0) {
                    curIndex -= 1;
                    update();
                }
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Result>() {

                    @Override
                    protected void onPreExecute() {
                        btn.setVisibility(View.INVISIBLE);
                        loading.setVisibility(View.VISIBLE);
                        getBitmapFromView(canvas);
                        super.onPreExecute();
                    }

                    @Override
                    protected Result doInBackground(Void... voids) {
                        Result result = new Result();
                        result.setOuterWriting(checkOuterWriting());
                        if (!result.isOuterWriting()) {
                            checkFillWriting(result);
                        }
                        return result;
                    }

                    @Override
                    protected void onPostExecute(Result result) {
                        btn.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.INVISIBLE);

                        if (result.isOuterWriting()) {
                            Toast.makeText(ChineseWriting.this, "Out of bounds", Toast.LENGTH_SHORT).show();
                        } else {
                            targetSizeView.setText(String.valueOf(result.getTargetSize()));
                            writingSizeView.setText(String.valueOf(result.getWritingSize()));

                            float rate = result.getWritingSize() * 1.0f / result.getTargetSize();
                            rateView.setText(String.valueOf(rate));
                            if (rate > chineseList.get(curIndex).getRate()) {
                                //Toast.makeText(ChineseWriting.this, "Good !", Toast.LENGTH_SHORT).show();
                                next();
                            } else {
                                Toast.makeText(ChineseWriting.this, "Not complete yet", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }.execute();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void initData() {
        chineseList = new ArrayList<>();
        chineseList.add(new Chinese(R.mipmap.image1, R.mipmap.image01, 0.5f));
        chineseList.add(new Chinese(R.mipmap.image2, R.mipmap.image02, 0.8f));
        chineseList.add(new Chinese(R.mipmap.image3, R.mipmap.image03, 0.5f));
        chineseList.add(new Chinese(R.mipmap.image4, R.mipmap.image04, 0.48f));
        chineseList.add(new Chinese(R.mipmap.image5, R.mipmap.image05, 0.57f));
        chineseList.add(new Chinese(R.mipmap.image6, R.mipmap.image06, 0.53f));
        chineseList.add(new Chinese(R.mipmap.image7, R.mipmap.image07, 0.3f));
        chineseList.add(new Chinese(R.mipmap.image8, R.mipmap.image08, 0.78f));
        chineseList.add(new Chinese(R.mipmap.image9, R.mipmap.image09, 0.8f));
        chineseList.add(new Chinese(R.mipmap.image10, R.mipmap.image010, 0.7f));
        chineseList.add(new Chinese(R.mipmap.image11, R.mipmap.image011, 0.58f));
        chineseList.add(new Chinese(R.mipmap.image12, R.mipmap.image012, 0.36f));
        chineseList.add(new Chinese(R.mipmap.image13, R.mipmap.image013, 0.72f));
        chineseList.add(new Chinese(R.mipmap.image14, R.mipmap.image014, 0.3f));
        chineseList.add(new Chinese(R.mipmap.image15, R.mipmap.image015, 0.73f));
    }

    private void next() {
        if (curIndex < chineseList.size() - 1) {
            curIndex += 1;
            update();
        }
    }

    private void update() {
        reset();
        imageView.setImageResource(chineseList.get(curIndex).getIdRes());
        imageViewBack.setImageResource(chineseList.get(curIndex).getIdResBack());
        targetSize = 0;
        targetSizeView.setText("0");
    }

    private void reset() {
        canvas.clear();
        writingSizeView.setText("0");
        rateView.setText("0");
    }

    /**
     * 是否写出界了
     */
    private boolean checkOuterWriting() {
        boolean isOuter = false;

        bitmap = imageViewBack.getDrawingCache();
        for (int i = 0; i < canvas.getPathList().size(); i++) {
            Point point = canvas.getPathList().get(i);
            if (bitmap.getPixel(point.x, point.y) == 0 || bitmap.getPixel(point.x, point.y) == -1) {
                isOuter = true;
                break;
            }
        }

        return isOuter;
    }

    /**
     * 是否写满了
     */
    private void checkFillWriting(Result result) {
        if (targetSize == 0) {
            bitmap = imageView.getDrawingCache();
            for (int x = 0; x < bitmap.getWidth(); x++) {
                for (int y = 0; y < bitmap.getHeight(); y++) {
                    if (bitmap.getPixel(x, y) != 0) {
                        targetSize++;
                    }
                }
            }
        }
        result.setTargetSize(targetSize);

        int count = 0;
        for (int x = 0; x < canvasBitmap.getWidth(); x++) {
            for (int y = 0; y < canvasBitmap.getHeight(); y++) {
                if (canvasBitmap.getPixel(x, y) != 0 && canvasBitmap.getPixel(x, y) != -1) {
                    count++;
                }
            }
        }
        result.setWritingSize(count);
    }

    private void getBitmapFromView(View view) {
        canvasBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);

        Canvas c = new Canvas(canvasBitmap);
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        // Draw background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(c);
        } else {
            c.drawColor(Color.WHITE);
        }
        view.draw(c);
    }

}
