package com.derek.chinesewriting;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

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
    private SimpleDraweeView mSimpleDraweeView;

    private List<Chinese> chineseList;
    private List<Integer> gifList;
    private int curIndex;

    private Bitmap bitmap;
    private Bitmap canvasBitmap;

    private int targetSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);

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
        mSimpleDraweeView = findViewById(R.id.gif_view);

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
                                //next();
                                DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setUri(Uri.parse("res://com.derek.chinesewriting/" + gifList.get(curIndex)))
                                    .setAutoPlayAnimations(true)
                                    .build();
                                mSimpleDraweeView.setController(controller);
                                mSimpleDraweeView.setVisibility(View.VISIBLE);
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
        chineseList.add(new Chinese(R.mipmap.image1, R.mipmap.image01, R.mipmap.j1, 0.25f));
        chineseList.add(new Chinese(R.mipmap.image2, R.mipmap.image02, R.mipmap.j2, 0.4f));
        chineseList.add(new Chinese(R.mipmap.image3, R.mipmap.image03, R.mipmap.j3, 0.3f));
        chineseList.add(new Chinese(R.mipmap.image4, R.mipmap.image04, R.mipmap.j4, 0.25f));
        chineseList.add(new Chinese(R.mipmap.image5, R.mipmap.image05, R.mipmap.j5, 0.27f));
        chineseList.add(new Chinese(R.mipmap.image6, R.mipmap.image06, R.mipmap.j6, 0.29f));
        chineseList.add(new Chinese(R.mipmap.image7, R.mipmap.image07, R.mipmap.j7, 0.15f));
        chineseList.add(new Chinese(R.mipmap.image8, R.mipmap.image08, R.mipmap.j8, 0.4f));
        chineseList.add(new Chinese(R.mipmap.image9, R.mipmap.image09, R.mipmap.j9, 0.38f));
        chineseList.add(new Chinese(R.mipmap.image10, R.mipmap.image010, R.mipmap.j10, 0.35f));
        chineseList.add(new Chinese(R.mipmap.image11, R.mipmap.image011, R.mipmap.j11, 0.27f));
        chineseList.add(new Chinese(R.mipmap.image12, R.mipmap.image012, R.mipmap.j12, 0.2f));
        chineseList.add(new Chinese(R.mipmap.image13, R.mipmap.image013, R.mipmap.j13, 0.39f));
        chineseList.add(new Chinese(R.mipmap.image14, R.mipmap.image014, R.mipmap.j14, 0.17f));
        chineseList.add(new Chinese(R.mipmap.image15, R.mipmap.image015, R.mipmap.j15, 0.34f));

        gifList = new ArrayList<>();
        gifList.add(R.mipmap.j1);
        gifList.add(R.mipmap.j2);
        gifList.add(R.mipmap.j3);
        gifList.add(R.mipmap.j4);
        gifList.add(R.mipmap.j5);
        gifList.add(R.mipmap.j6);
        gifList.add(R.mipmap.j7);
        gifList.add(R.mipmap.j8);
        gifList.add(R.mipmap.j9);
        gifList.add(R.mipmap.j10);
        gifList.add(R.mipmap.j11);
        gifList.add(R.mipmap.j12);
        gifList.add(R.mipmap.j13);
        gifList.add(R.mipmap.j14);
        gifList.add(R.mipmap.j15);
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
        mSimpleDraweeView.setVisibility(View.INVISIBLE);
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

        imageViewBack.setDrawingCacheEnabled(true);
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
